//
//  Report.swift
//  topgis-reporting
//
//  Created by Michal Šrejber on 03/04/2018.
//  Copyright © 2018 TopGis s.r.o. All rights reserved.
//

import UIKit

struct ReportType
{
    let reportType : String
    //let villageId : Int
}

// https://developer.apple.com/library/content/documentation/Swift/Conceptual/Swift_Programming_Language/Properties.html
class Report
{
    
    //https://developer.apple.com/library/content/documentation/Swift/Conceptual/Swift_Programming_Language/Properties.html

    private var createTime : Date = Date() //https://stackoverflow.com/questions/24070450/how-to-get-the-current-time-as-datetime
    private(set) var CreateTime : Date
    {
        set(value)
        {
            self.createTime = value
        }
        get
        {
            return self.createTime
        }
        
    }
    
    private var description : String = GlobalSettings.REPORT_NOT_SET
    var Description : String
    {
        set(value)
        {
            if (value == "")
            {
                self.description = GlobalSettings.REPORT_NOT_SET
            }
            else
            {
                self.description = value
            }
        }
        get
        {
            return self.description
        }
    }
    
    private(set) var location : GPSLocation
    
    private(set) var picture : UIImage?

    private(set) var reportType : ReportType?
    
    init(/*newName : String,*/ newDescription : String, newPicture : UIImage?, newLocation : GPSLocation, newReportType : ReportType)
    {
        self.location = newLocation
        self.picture = newPicture
        self.reportType = newReportType
        self.CreateTime = Date()
        /*self.Name = newName*/
        self.Description = newDescription
        //self.location = GPS()
//        self.location = newLocation
        
    }
}
