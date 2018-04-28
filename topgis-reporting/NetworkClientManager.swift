//
//  NetworkClientManager.swift
//  topgis-reporting
//
//  Created by Michal Šrejber on 21/04/2018.
//  Copyright © 2018 TopGis s.r.o. All rights reserved.
//

import UIKit

// Report
struct ReportToInsert : Encodable
{
    let gid, login, name, date, status, type, geom, description : String
}

//Report sugar for transport
struct TransferReport : Encodable
{
    let inserts : [ReportToInsert]
    let fidName : String = "gid"
    let geomName : String = "geom"
    let srs : String = "4326"
}

/*
var reportValues = ["gid" : "", "login" : "", "name" : "", "date" : "", "status" : "1", "type" : "4", "geom" : "POINT(12.135165,31.1358135)", "description" : "sakmdlkasjdla"]

*/

/**
 * Contains information from GisOnline server (responses)
 * *
 */
struct RequestsResult
{
    // Constants and variables from getVillageId response
    // * constants represents KEYs from KEY:VALUE pairs
    // * variables represents VALUEs from KEY:VALUE pairs
    // * only important variable is 'kod', it represents GisOnline internal village ID
    static let LABEL_KOD = "kod"
    static let LABEL_ORP = "orp"
    static let LABEL_NAZEV = "nazev"
    var kod : Int
    var orp : String
    var nazev : String
    
    // Constants and variables from getLayer URL response
    // * constants represents KEYs from KEY:VALUE pairs
    // * variables represents VALUEs from KEY:VALUE pairs
    // * only important variable is 'layer', it contains API ending layer
    static let LABEL_INFO = "info"
    static let LABEL_LAYER = "layer"
    static let LABEL_TITLE = "title"
    var layer : String
    var info : String
    var title : String
    
    static let LABEL_SEND_STATUS : String = "status"
    static let LABEL_SEND_INSERTIDS : String = "insertIds"
    static let LABEL_SEND_INSERTED : String = "inserted"
    var status : String
    var insertedids : String
    var inserted : String
    
    // JSON Constants
    static let LABEL_RESULT = "result"
    
    // nothing must be empty
    func checkData() -> Bool
    {
        return self.kod != -1 || self.layer != ""
    }
    
    /**
     * Basic constructor, variables will be set in future
     */
    init()
    {
        self.kod = -1
        self.orp = ""
        self.nazev = ""
        self.layer = ""
        self.info = ""
        self.title = ""
        self.inserted = ""
        self.insertedids = ""
        self.status = ""
    }
}

/**
 * NetworkClientManager is managing communication with GisOnline servers over https
 *
 */
class NetworkClientManager : NSObject
{
    //static let SERVER_URL_ADDRESS : String = "https://go-beta.topgis.cz"  // does not work right now (VPN needed)
    static let API_VERSION_ADDRESS : String = "/api/version"                // API for getting api version, place after SERVER_URL_ADDRESS
    //static let SERVER_URL_ADDRESS : String = "https://app.gisonline.cz"     // server address
    static let SERVER_URL_ADDRESS : String = "https://appbeta.gisonline.cz"
    static let API_RUIAN : String = "/api/ruian/obce"                       // API for getting ruian data, place after SERVER_URL_ADDRESS
    static let API_RUIAN_SRID : String = "srid=4326"                        // API parameter which will be always used
    static let API_DUMMY_LOGIN : String = "tester"
    static let API_DUMMY_PASSWORD : String = "testujo106"
    static let API_LABEL_LOGIN : String = "login"
    static let API_LABEL_PASSWORD : String = "psw"
    static let LABEL_LAYER : String = "/layers"
    static let API_LABEL_LAYER : String = "/api/layers/"
    static let API_LOGIN : String = "/api/login"
    static let API_LOGOUT : String = "/api/logout"
    
    let cookie : HTTPCookie?
    var requestsResult : RequestsResult         // Stored information retrieved from GisOnline server
    let historyViewController : HistoryViewController
    
    var reportAsData : Data? = nil
    var reportToSend : ReportEntity
    var alreadyLogin : Bool = false
    
    init(report : ReportEntity, historyViewController : HistoryViewController)
    {
        //self.requestsResult = RequestsResult(kod: -1, orp: "", nazev: "", isComplete: false)
        self.requestsResult = RequestsResult()
        self.cookie = nil
        self.reportToSend = report
        self.historyViewController = historyViewController
        super.init()
    }

    /**
     * Start sending sequence
     */
    func sendReport()
    {
        self.requestLogin()
    }
    
    /**
     * Send request login to server. Accept response and set given cookie
     */
    func requestLogin()
    {
        //create url request
        let requestUrl : URL = URL(string: "\(NetworkClientManager.SERVER_URL_ADDRESS)\(NetworkClientManager.API_LOGIN)")!
        //create url parameters, in this case login name and password
        let requestParameters : [String : Any] = [ NetworkClientManager.API_LABEL_LOGIN : NetworkClientManager.API_DUMMY_LOGIN,
                                                   NetworkClientManager.API_LABEL_PASSWORD : NetworkClientManager.API_DUMMY_PASSWORD ]
        //Add parameters to json data
        let requestParametersJSON : Data
        do
        {
            requestParametersJSON = try JSONSerialization.data(withJSONObject: requestParameters, options: [])
        }
        catch
        {
            self.fail(message: "Login prepare error:\(error.localizedDescription)")
            return
        }
        
        //Create and set request
        var requestUrlWithData = URLRequest(url: requestUrl)
        requestUrlWithData.httpMethod = "POST"
        requestUrlWithData.httpBody = requestParametersJSON
        
        //set request handler
        let request = URLSession.shared.dataTask(with: requestUrlWithData)
        {
            (data, response, error) in
            //in case of error
            if let unwrappedError = error
            {
                self.fail(message: "Login request has error:\(unwrappedError)")
                return
            }
            
            // Check response data
            guard let _ = data, let httpUrlResponse = response as? HTTPURLResponse else
            {
                self.fail(message: "Login unwrapping response and data failed)")
                return
            }

            // check returned code
            guard (httpUrlResponse.statusCode == 200) else
            {
                self.fail(message: "Login request failed. Request status code:\(httpUrlResponse.statusCode)")
                return
            }
        
            self.setCookie(httpUrlResponse: httpUrlResponse)
            
            //yes, I am login
            self.alreadyLogin = true
            
            // Continue with sending
            self.requestPositionId(/*location: GPSLocation*/)
        }
        request.resume()
    }
    
    /**
     * Store http cookie to global store
     */
    private func setCookie(httpUrlResponse : HTTPURLResponse)
    {
        //create cookie as it should be
        //do {
            let setCookieResponse = httpUrlResponse.allHeaderFields["Set-Cookie"]!
            let cookieAsString = "\(setCookieResponse)"
            let cookieField = ["Set-Cookie" : "\(cookieAsString)"]
            let cookie = HTTPCookie.cookies(withResponseHeaderFields: cookieField, for: URL(string: NetworkClientManager.SERVER_URL_ADDRESS)!)
            //Add cookie to phone storage
            //HTTPCookieStorage.shared.setCookies(cookie, for: URL(string: "app3.gisonline.cz")!, mainDocumentURL: nil)
            HTTPCookieStorage.shared.setCookie(cookie.first!) //there always will be cookie, even bad
        //}
        //catch
        //{
        //    self.fail(message: "Bad cookie:\(error.localizedDescription)")
        //}
    }
    
    /**
     * TODO
     */
    func requestLogout()
    {
        let requestUrl : URL = URL(string : "\(NetworkClientManager.SERVER_URL_ADDRESS)\(NetworkClientManager.API_LOGOUT)")!
        let urlSession = URLSession(configuration: .default)
        
        //let request = URLSession.shared.dataTask(with: requestUrl)
        let request = urlSession.dataTask(with: requestUrl)
        {
            (data, response, error) in
            // In case of missing this one, there will be infinite cycle
            self.alreadyLogin = false
            
            if let unwrappedError = error
            {
                self.fail(message: "Logout request has error:\(unwrappedError)")
                return
            }
            
            // Check response data
            guard let _ = data, let httpUrlResponse = response as? HTTPURLResponse else
            {
                self.fail(message: "Logout unwrapping response and data failed)")
                return
            }
            
            // check returned code
            guard (httpUrlResponse.statusCode == 200) else
            {
                self.fail(message: "Logout request failed. Request status code:\(httpUrlResponse.statusCode)")
                return
            }
            
            print("Logout successfull")
            //print(response)
        }
        request.resume()
    }
    
    
    /**
     * Send https request to GioOnline server for information about Layer, where will be stored report, defined by villageId
     *
     */
    func requestLayerInfo()
    {
        // Create layer API ending for URL
        let urlParameterLayers = "/\(self.requestsResult.kod)\(NetworkClientManager.LABEL_LAYER)"
        
        // Create layer API URL
        let requestUrl : URL = URL(string: "\(NetworkClientManager.SERVER_URL_ADDRESS)\(NetworkClientManager.API_RUIAN)\(urlParameterLayers)")!
        
        // Create https request to GisOnline server
        let request = URLSession.shared.dataTask(with: requestUrl)
        {
            (data, response, error) in
            //What to do with server response
            if let unwrappedError = error
            {
                self.fail(message: "requestPositionId has error:\(unwrappedError)")
                return
            }
            
            // Check response data
            guard let unwrappedData = data, let httpUrlResponse = response as? HTTPURLResponse else
            {
                self.fail(message: "requestPositionId unwrapping response and data failed)")
                return
            }
            
            // check returned code
            guard (httpUrlResponse.statusCode == 200 && !httpUrlResponse.allHeaderFields.isEmpty) else
            {
                self.fail(message: "requestPositionId has failed. Request status code:\(httpUrlResponse.statusCode)")
                return
            }
            
            
            do
            {
                // Trying parse json data from server
                guard let unwrappedJSONData = try JSONSerialization.jsonObject(with: unwrappedData, options: []) as? [String: Any] else
                {
                    self.fail(message: "requestLayerInfo, could not get JSON from responseData as dictionary")
                    return
                }

                //print(unwrappedResponseData.description)

                //
                guard let resultDictionary = unwrappedJSONData[RequestsResult.LABEL_RESULT] as? [String : Any] else
                {
                    self.fail(message: "requestLayerInfo response data has not 'Result'")
                    return
                }
                
                // Iterate over result dictionary. There are only 3 KEY:VALUE pairs
                for(key, value) in resultDictionary
                {
                    //print("\(key) : \(value)")
                    switch key
                    {
                    case RequestsResult.LABEL_INFO:
                        self.requestsResult.info = value as! String
                        //print("\(key):::\(value)")
                    case RequestsResult.LABEL_LAYER:
                        self.requestsResult.layer = value as! String
                        //print("\(key):::\(value)")
                    case RequestsResult.LABEL_TITLE:
                        self.requestsResult.title = value as! String
                        //print("\(key):::\(value)")
                    default:
                        self.fail(message: "WTF??? If you see this in output, API has changed ...") //if this is in output, API has changed ...
                    }
                }
                
            }
            catch
            {
                self.fail(message: "Error parsing response from requestLayerInfo:\(error.localizedDescription)")
                return
            }
            //here should continue TODO
            //self.requestLogout()
            self.sendData()
        }

        
        // run request
        request.resume()
    }
    
    /**
     * Send https request to GioOnline server for information about position (GPS in WGS84), which is needed for layer identification.
     * * ID is internal number for specific village
     * * GPS coordinates are needed for creating API request and getting location
     */
    func requestPositionId()
    {
        //x == longitude
        //y == latitude
        //let urlParameterX="x=\(self.reportToSend.longitude)"
        //let urlParameterY="y=\(self.reportToSend.latitude)"
        
        //dummy data
        let urlParameterX="x=16.606837"
        let urlParameterY="y=49.195060"
        
        // Example: https://app.gisonline.cz/api/ruian/obce?x=16.606837&y=49.195060&srid=4326
        // Create API URL
        let requestUrl : URL = URL(string: "\(NetworkClientManager.SERVER_URL_ADDRESS)\(NetworkClientManager.API_RUIAN)?\(urlParameterX)&\(urlParameterY)&\(NetworkClientManager.API_RUIAN_SRID)")!
        
        // Creating https request for server
        let request = URLSession.shared.dataTask(with: requestUrl)
        {
            (data, response, error) in
            
            if let unwrappedError = error
            {
                self.fail(message: "requestPositionId has error:\(unwrappedError)")
                return
            }
            
            // Check response data
            guard let unwrappedData = data, let httpUrlResponse = response as? HTTPURLResponse else
            {
                self.fail(message: "requestPositionId unwrapping response and data failed)")
                return
            }
            
            // check returned code
            guard (httpUrlResponse.statusCode == 200 && !httpUrlResponse.allHeaderFields.isEmpty) else
            {
                self.fail(message: "requestPositionId has failed. Request status code:\(httpUrlResponse.statusCode)")
                return
            }
            
            // What to do with server response
            do
            {
                // Check json format, API version always fail on this
                guard let unwrappedJSONData = try JSONSerialization.jsonObject(with: unwrappedData, options: []) as? [String: Any] else
                {
                    self.fail(message: "requestPositionId, could not get JSON from responseData as dictionary")
                    return
                }
                
                // unwrapping result values (NOT status code)
                guard let resultDictionary = unwrappedJSONData[RequestsResult.LABEL_RESULT] as? [String : Any] else
                {
                    self.fail(message: "requestPositionId response data has not 'Result'")
                    return
                }
                
                // Iterate over result dictionary. There are only 3 KEY:VALUE pairs
                for(key, value) in resultDictionary
                {
                    //print("\(key) : \(value)")
                    switch key
                    {
                    case RequestsResult.LABEL_KOD:
                        self.requestsResult.kod = value as! Int
                    case RequestsResult.LABEL_ORP:
                        self.requestsResult.orp = value as! String
                    case RequestsResult.LABEL_NAZEV:
                        self.requestsResult.nazev = value as! String
                    default:
                        self.fail(message: "WTF??? If you see this in output, API has changed ...") //if this is in output, API has changed ...
                    }
                }
            }
            catch
            {
                self.fail(message: "Error parsing response from requestPositionId:\(error.localizedDescription)")
                return
            }
            // we have result, sequence can continue
            self.requestLayerInfo()
        }
        // Run request 
        request.resume()
    }
    
    func fail(message : String)
    {
        print(message)
        if(alreadyLogin)
        {
            self.requestLogout()
        }
        self.alreadyLogin = false
    }
    
    func sendData()
    {
        if(!self.getDecodedReport())
        {
            self.fail(message: "Report encoding failed")
            return
        }
        
        let requestUrl : URL = URL(string : "\(NetworkClientManager.SERVER_URL_ADDRESS)\(NetworkClientManager.API_LABEL_LAYER)\(self.self.requestsResult.layer)")!
        print(requestUrl)
        var requestUrlWithData = URLRequest(url: requestUrl)
        requestUrlWithData.httpMethod = "POST"
        //let requestParameters : [String : Any] = [ NetworkClientManager.API_LABEL_LOGIN : NetworkClientManager.API_DUMMY_LOGIN,                                                   NetworkClientManager.API_LABEL_PASSWORD : NetworkClientManager.API_DUMMY_PASSWORD ]
        requestUrlWithData.httpBody = self.reportAsData
        requestUrlWithData.httpShouldHandleCookies = true
        requestUrlWithData.setValue("Content-Type", forHTTPHeaderField: "application/json")

        //requestUrlWithData.httpBody = requestParametersJSON
        
        //set request handler
        
        let request = URLSession.shared.dataTask(with: requestUrlWithData)
        {
            (data, response, error) in
            
            if let unwrappedError = error
            {
                self.fail(message: "sendData has error:\(unwrappedError)")
                return
            }
            
            // Check response data
            guard let unwrappedData = data, let httpUrlResponse = response as? HTTPURLResponse else
            {
                self.fail(message: "sendData unwrapping response and data failed)")
                return
            }
            
            // check returned code
            guard (httpUrlResponse.statusCode == 200 && !httpUrlResponse.allHeaderFields.isEmpty) else
            {
                print(response)
                print(unwrappedData)
                self.fail(message: "sendData has failed. Request status code:\(httpUrlResponse.statusCode)")
                return
            }
            //print(httpUrlResponse)
            
            // What to do with server response
            do
            {
                // Check json format, API version always fail on this
                guard let unwrappedJSONData = try JSONSerialization.jsonObject(with: unwrappedData, options: []) as? [String: Any] else
                {
                    self.fail(message: "sendData, could not get JSON from responseData as dictionary")
                    return
                }
                
                // unwrapping result values (NOT status code)
                guard let resultDictionary = unwrappedJSONData[RequestsResult.LABEL_RESULT] as? [String : Any] else
                {
                    self.fail(message: "sendData response data has not 'Result'")
                    return
                }
                
                // Iterate over result dictionary. There are only 3 KEY:VALUE pairs
                for(key, value) in resultDictionary
                {
                    print("\(key) : \(value)")
                    switch key
                    {
                    case RequestsResult.LABEL_SEND_STATUS:
                        self.requestsResult.status = value as! String
                    case RequestsResult.LABEL_SEND_INSERTED:
                        self.requestsResult.inserted = value as! String
                    case RequestsResult.LABEL_SEND_INSERTIDS:
                        self.requestsResult.insertedids = value as! String
                    default:
                        self.fail(message: "WTF??? If you see this in output, API has changed ...") //if this is in output, API has changed ...
                    }
                }
            }
            catch
            {
                self.fail(message: "Error parsing response from sendData:\(error.localizedDescription)")
                return
            }
            // Check picture sending
            if let _ = self.reportToSend.image
            {
                //self.sendPhoto()
                self.updateReport()
            }
            else
            {
                self.updateReport()
            }
        }
        
        request.resume()
    }
    
    /**
     * TODO
     */
    func sendPhoto()
    {
        let requestUrl : URL = URL(string : "\(NetworkClientManager.SERVER_URL_ADDRESS)\(NetworkClientManager.API_LABEL_LAYER)\(self.self.requestsResult.layer)")!
        print(requestUrl)
        var requestUrlWithData = URLRequest(url: requestUrl)
        requestUrlWithData.httpMethod = "POST"
        //let requestParameters : [String : Any] = [ NetworkClientManager.API_LABEL_LOGIN : NetworkClientManager.API_DUMMY_LOGIN,                                                   NetworkClientManager.API_LABEL_PASSWORD : NetworkClientManager.API_DUMMY_PASSWORD ]
        //requestUrlWithData.httpBody = self.reportAsData
        requestUrlWithData.httpShouldHandleCookies = true
        requestUrlWithData.setValue("Content-Type", forHTTPHeaderField: "application/json")
        
        //requestUrlWithData.httpBody = requestParametersJSON
        
        //set request handler
        
        let request = URLSession.shared.dataTask(with: requestUrlWithData)
        {
            (data, response, error) in
            
            if let unwrappedError = error
            {
                self.fail(message: "sendPhoto has error:\(unwrappedError)")
                return
            }
            
            // Check response data
            guard let unwrappedData = data, let httpUrlResponse = response as? HTTPURLResponse else
            {
                self.fail(message: "sendPhoto unwrapping response and data failed)")
                return
            }
            
            // check returned code
            guard (httpUrlResponse.statusCode == 200 && !httpUrlResponse.allHeaderFields.isEmpty) else
            {
                print(response)
                print(unwrappedData)
                self.fail(message: "sendPhoto has failed. Request status code:\(httpUrlResponse.statusCode)")
                return
            }
            //print(httpUrlResponse)
            /*
            // What to do with server response
            do
            {
                // Check json format, API version always fail on this
                guard let unwrappedJSONData = try JSONSerialization.jsonObject(with: unwrappedData, options: []) as? [String: Any] else
                {
                    self.fail(message: "sendPhoto, could not get JSON from responseData as dictionary")
                    return
                }
                
                // unwrapping result values (NOT status code)
                guard let resultDictionary = unwrappedJSONData[RequestsResult.LABEL_RESULT] as? [String : Any] else
                {
                    self.fail(message: "sendPhoto response data has not 'Result'")
                    return
                }
                
                // Iterate over result dictionary. There are only 3 KEY:VALUE pairs
                for(key, value) in resultDictionary
                {
                    print("\(key) : \(value)")
                    switch key
                    {
                    case RequestsResult.LABEL_SEND_STATUS:
                        self.requestsResult.status = value as! String
                    case RequestsResult.LABEL_SEND_INSERTED:
                        self.requestsResult.inserted = value as! String
                    case RequestsResult.LABEL_SEND_INSERTIDS:
                        self.requestsResult.insertedids = value as! String
                    default:
                        self.fail(message: "WTF??? If you see this in output, API has changed ...") //if this is in output, API has changed ...
                    }
                }
            }
            catch
            {
                self.fail(message: "Error parsing response from sendPhoto:\(error.localizedDescription)")
                return
            }*/
            
            self.updateReport()

        }
        
        request.resume()
    }
    
    /**
     *
     */
    func updateReport()
    {
        self.historyViewController.updateReport(createTime: self.reportToSend.createTime!)
        self.requestLogout()
    }
    
    /**
     * Take reportEntity and encode it to jsonData
     */
    func getDecodedReport() -> Bool
    {
        //dummy value
        let reportToInsert = ReportToInsert(gid: "", login: NetworkClientManager.API_DUMMY_LOGIN, name: "Test", date: GlobalSettings.getDate(date: self.reportToSend.createTime), status: "1", type: "4", geom: "POINT(16.606837,49.195060)", description: self.reportToSend.reportDescription!)
        //print(JSONSerialization.isValidJSONObject(reportToInsert))
        
        let transferReport = TransferReport(inserts: [reportToInsert])
        //print(JSONSerialization.isValidJSONObject(transferReport))
        
        let jsonEncoder = JSONEncoder()
        do
        {
            let jsonData = try jsonEncoder.encode(transferReport)
            self.reportAsData = jsonData
            //let jsonString = String(data: jsonData, encoding: .utf8)
            //print("\(jsonString!)")
            return true
        }
        catch
        {
            print("erorr")
            self.reportAsData = nil
            return false
        }
    }
}
