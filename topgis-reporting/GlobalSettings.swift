//
//  GlobalSettings.swift
//  topgis-reporting
//
//  Created by Michal Šrejber on 15/04/2018.
//  Copyright © 2018 TopGis s.r.o. All rights reserved.
//

import UIKit

//Static class
class GlobalSettings: NSObject
{
    static let GPS_NOT_SET = "Neznámá"
    static let DATE_NOT_SEND = "Neznám"
    static let BOOL_STRING_YES = "Ano"
    static let BOOL_STRING_NO = "Ne"
    static let REPORT_NOT_SET = "Nebylo zadáno"

    static func getTimeLocale(date : Date?) -> String
    {
        var stringDate : String = GlobalSettings.DATE_NOT_SEND
        //https://stackoverflow.com/questions/28404154/swift-get-local-date-time
        if let unwrappedDate = date
        {
            let dateFormatter = DateFormatter()
            // 'dd' and 'DD' gives different results!!
            // 'MM' == Months, 'mm' == minutes
            dateFormatter.dateFormat = "dd. MM. yyyy - HH:mm:ss"
            stringDate = dateFormatter.string(from: unwrappedDate)
        }
        return stringDate
    }
    override private init()
    {
    }
}
