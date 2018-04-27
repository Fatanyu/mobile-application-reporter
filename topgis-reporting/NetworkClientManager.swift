//
//  NetworkClientManager.swift
//  topgis-reporting
//
//  Created by Michal Šrejber on 21/04/2018.
//  Copyright © 2018 TopGis s.r.o. All rights reserved.
//

import UIKit

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
    static let SERVER_URL_ADDRESS : String = "https://app3.gisonline.cz"
    static let API_RUIAN : String = "/api/ruian/obce"                       // API for getting ruian data, place after SERVER_URL_ADDRESS
    static let API_RUIAN_SRID : String = "srid=4326"                        // API parameter which will be always used
    static let API_DUMMY_LOGIN : String = "tester"
    static let API_DUMMY_PASSWORD : String = "testujo106"
    static let API_LABEL_LOGIN : String = "login"
    static let API_LABEL_PASSWORD : String = "psw"
    static let API_LOGIN : String = "/api/login"
    static let API_LOGOUT : String = "/api/logout"
    
    let cookie : HTTPCookie?
    var requestsResult : RequestsResult         // Stored information retrieved from GisOnline server
    
    var reportToSend : ReportEntity
    
    init(report : ReportEntity)
    {
        //self.requestsResult = RequestsResult(kod: -1, orp: "", nazev: "", isComplete: false)
        self.requestsResult = RequestsResult()
        self.cookie = nil
        self.reportToSend = report
        super.init()
    }
    
    //dev method
    static func wtf()
    {
        //NetworkClientManager.giveMeApiVersion()
        //NetworkClientManager.postWhereAmI(longitude: 16.606837, latitude: 49.195060)
    }
    
    func sendReport()
    {
        self.requestLogin()
    }
    
    //working example of GET API
    static func giveMeApiVersion()
    {
        // create string for API URL
        let urlString: String = "\(NetworkClientManager.SERVER_URL_ADDRESS)\(NetworkClientManager.API_VERSION_ADDRESS)"
        // create API URL, urlString will be always defined
        let url : URL = URL(string: urlString)!
        
        // create request which will send request to API defined in urlString
        let request = URLSession.shared.dataTask(with: url)
        {
            // request returns up to 3 variables (all can be nil)
            (data, response, error) in
            //print("\(data)")
            //print("\(response)")
            //print("\(error)")
        }
        
        // run request
        request.resume()
    }
   
    
    /**
     * TODO
     */
    func requestLogin()
    {
        let requestUrl : URL = URL(string: "\(NetworkClientManager.SERVER_URL_ADDRESS)\(NetworkClientManager.API_LOGIN)")!
        let requestParameters : [String : Any] = [ NetworkClientManager.API_LABEL_LOGIN : NetworkClientManager.API_DUMMY_LOGIN,
                                                   NetworkClientManager.API_LABEL_PASSWORD : NetworkClientManager.API_DUMMY_PASSWORD ]
        let requestParametersJSON : Data
        do
        {
            requestParametersJSON = try JSONSerialization.data(withJSONObject: requestParameters, options: [])
        }
        catch
        {
            print("TODO - request login")
            return
        }
        
        var requestUrlWithData = URLRequest(url: requestUrl)
        requestUrlWithData.httpMethod = "POST"
        requestUrlWithData.httpBody = requestParametersJSON
        
        
        let request = URLSession.shared.dataTask(with: requestUrlWithData)
        {
            (data, response, error) in
            if let unwrappedError = error
            {
                print("POST login has error:\(unwrappedError)")
                return
            }
            do
            {
                // Check response data
                guard let responseData = data else
                {
                    print("No data available (nil)")
                    return
                }

                let httpUrlResponse : HTTPURLResponse = response as! HTTPURLResponse
                
                //print(httpUrlResponse.allHeaderFields["Set-Cookie"]!)
                
                let setCookieResponse = httpUrlResponse.allHeaderFields["Set-Cookie"]!
                let cookieAsString = "\(setCookieResponse)"
                let cookieField = ["Set-Cookie" : "\(cookieAsString)"]

                let cookie = HTTPCookie.cookies(withResponseHeaderFields: cookieField, for: URL(string: NetworkClientManager.SERVER_URL_ADDRESS)!)
                //HTTPCookieStorage.shared.setCookies(cookie, for: URL(string: "app3.gisonline.cz")!, mainDocumentURL: nil)
                HTTPCookieStorage.shared.setCookie(cookie.first!)
                
                self.requestPositionId(/*location: GPSLocation*/)
                
                //self.requestLogout()
            }
            catch
            {
                print("Some nasty exception:\(error)")
            }

        }
        request.resume()
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
            guard let responseData = data else
            {
                print("No data available (nil)")
                return
            }
            print(response)
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
        let urlParameterLayers = "/\(self.requestsResult.kod)/layers"
        
        // Create layer API URL
        let requestUrl : URL = URL(string: "\(NetworkClientManager.SERVER_URL_ADDRESS)\(NetworkClientManager.API_RUIAN)\(urlParameterLayers)")!
        
        // Create https request to GisOnline server
        let request = URLSession.shared.dataTask(with: requestUrl)
        {
            (data, response, error) in
            //What to do with server response
            do
            {
                // Check if data exists
                guard let responseData = data else
                {
                    print("No data available (nil)")
                    return
                }
                
                // Trying parse json data from server
                guard let unwrappedResponseData = try JSONSerialization.jsonObject(with: responseData, options: []) as? [String: Any] else
                {
                    // Always will fail for API version or another basic text, which is not json
                    //print("Could not get JSON from responseData as dictionary")
                    return
                }

                //print(unwrappedResponseData.description)

                //
                guard let resultDictionary = unwrappedResponseData[RequestsResult.LABEL_RESULT] as? [String : Any] else
                {
                    return
                }
                
                //print(resultDictionary.first)
                
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
                        print("WTF??? If you see this in output, API has changed ...") //if this is in output, API has changed ...
                    }
                }
                //here should continue TODO
                self.requestLogout()
            }
            catch
            {
                print("Error parsing response from requestLayer")
                return
            }
        }
        
        // run request
        request.resume()
    }
    
    /**
     * Send https request to GioOnline server for information about position (GPS in WGS84), which is needed for layer identification.
     * * ID is internal number for specific village
     * * GPS coordinates are needed for creating API request and getting location
     */
    func requestPositionId(/*location : GPSLocation*/)
    {
        //x == longitude
        //y == latitude
        //let urlParameterX="x=\(location.longitude)"
        //let urlParameterY="y=\(location.latitude)"
        
        let urlParameterX="x=\(self.reportToSend.longitude)"
        let urlParameterY="y=\(self.reportToSend.latitude)"
        
        
        // Example: https://app.gisonline.cz/api/ruian/obce?x=16.606837&y=49.195060&srid=4326
        // Create API URL
        let requestUrl : URL = URL(string: "\(NetworkClientManager.SERVER_URL_ADDRESS)\(NetworkClientManager.API_RUIAN)?\(urlParameterX)&\(urlParameterY)&\(NetworkClientManager.API_RUIAN_SRID)")!
        
        // Creating https request for server
        let request = URLSession.shared.dataTask(with: requestUrl)
        {
            (data, response, error) in
            //print(data)
            //print(response)
            
            //What to do with server response
            do
            {
                // Check response data
                guard let responseData = data else
                {
                    print("No data available (nil)")
                    return
                }
                
                // Check json format, API version always fail on this
                guard let unwrappedResponseData = try JSONSerialization.jsonObject(with: responseData, options: []) as? [String: Any] else
                {
                    print("Could not get JSON from responseData as dictionary")
                    return
                }
                //print("getId json result is: " + unwrappedResponseData.description) //json format
                
                //
                guard let resultDictionary = unwrappedResponseData[RequestsResult.LABEL_RESULT] as? [String : Any] else
                {
                    return
                }
                
                //print(resultDictionary.first)
                
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
                        print("WTF??? If you see this in output, API has changed ...") //if this is in output, API has changed ...
                    }
                }
                self.requestLayerInfo()
            }
            catch
            {
                print("Error parsing response from requestPositionId")
                return
            }
            // we have result
            self.requestLayerInfo()
        }
        // Run request 
        request.resume()
    }
}
