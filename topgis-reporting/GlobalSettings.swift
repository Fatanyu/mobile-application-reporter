//
//  GlobalSettings.swift
//  topgis-reporting
//
//  Created by Michal Šrejber on 15/04/2018.
//  Copyright © 2018 TopGis s.r.o. All rights reserved.
//

import UIKit

/**
 * Class for global scraps. Mostly global constants and methods for formating
 */
class GlobalSettings: NSObject
{
    // Global constants (czech localization) for showing info
    static let GPS_NOT_SET = "Neznámá"
    static let DATE_NOT_SEND = "Neposláno"
    static let BOOL_STRING_YES = "Ano"
    static let BOOL_STRING_NO = "Ne"
    static let REPORT_NOT_SET = "Nebylo zadáno"
    static let IMAGE_MISSING = "Fotografie nebyla pořízena"
    static let IMAGE_FORMAT = ".jpeg"
    static let IMAGE_DIRECTORY_PATH = "/Documents/"

    /**
     * Getter for czech republic time format (for eyes)
     */
    static func getTimeLocale(date : Date?) -> String
    {
        var stringDate : String = GlobalSettings.DATE_NOT_SEND
        //https://stackoverflow.com/questions/28404154/swift-get-local-date-time
        if let unwrappedDate = date
        {
            let dateFormatter = DateFormatter()
            // 'dd' and 'DD' gives different results!!
            // 'MM' == Months, 'mm' == minutes
            dateFormatter.dateFormat = "dd. MM. yyyy HH:mm:ss"
            stringDate = dateFormatter.string(from: unwrappedDate)
        }
        return stringDate
    }
    
    /**
     * Getter for czech republic time format (for eyes). Added new line for table purpose
     */
    static func getTimeLocaleAsTwoLines(date : Date?) -> String
    {
        var stringDate : String = GlobalSettings.DATE_NOT_SEND
        //https://stackoverflow.com/questions/28404154/swift-get-local-date-time
        if let unwrappedDate = date
        {
            let dateFormatter = DateFormatter()
            // 'dd' and 'DD' gives different results!!
            // 'MM' == Months, 'mm' == minutes
            dateFormatter.dateFormat = "dd. MM. yyyy\nHH:mm:ss"
            stringDate = dateFormatter.string(from: unwrappedDate)
        }
        return stringDate
    }
    
    /**
     * Getter for server date format
     */
    static func getDate(date : Date?) -> String
    {
        var stringDate : String = GlobalSettings.DATE_NOT_SEND
        //https://stackoverflow.com/questions/28404154/swift-get-local-date-time
        if let unwrappedDate = date
        {
            let dateFormatter = DateFormatter()
            // 'dd' and 'DD' gives different results!!
            // 'MM' == Months, 'mm' == minutes
            dateFormatter.dateFormat = "yyyy-MM-dd"
            stringDate = dateFormatter.string(from: unwrappedDate)
        }
        return stringDate
    }
    
    /**
     * Create name for storing pictures
     */
    static func getImageName(date : Date?) -> String
    {
        var filename : String = ""
        
        if let unwrappedDate = date
        {
            let dateFormatter = DateFormatter()
            // 'dd' and 'DD' gives different results!!
            // 'MM' == Months, 'mm' == minutes
            dateFormatter.dateFormat = "dd-MM-yyyy-HH-mm-ss"
            filename = dateFormatter.string(from: unwrappedDate)
            filename.append(GlobalSettings.IMAGE_FORMAT)
        }
        
        return filename
    }
    
    /**
     * Save image to local storage (not Core Data)
     */
    static func saveImage(chosenImage: UIImage?, createTime : Date) -> String
    {
        // create default (empty) path
        var filePath : String = ""
        
        // check if image exists
        if let unwrappedImage = chosenImage
        {
            // Create directory path string
            let directoryPath =  NSHomeDirectory().appending(GlobalSettings.IMAGE_DIRECTORY_PATH)
            
            // Make sure directory exists
            if !FileManager.default.fileExists(atPath: directoryPath)
            {
                do
                {
                    try FileManager.default.createDirectory(at: NSURL.fileURL(withPath: directoryPath), withIntermediateDirectories: true, attributes: nil)
                }
                catch
                {
                    // Directory do not exists and can not be created
                    print(error)
                    return filePath
                }
            }
            // Create file name and path to it. Store path as result which will be return
            let fileName = GlobalSettings.getImageName(date: createTime)
            filePath = directoryPath.appending(fileName)
            
            // Store picture
            let url = NSURL.fileURL(withPath: filePath)
            do
            {
                try UIImageJPEGRepresentation(unwrappedImage, 1.0)?.write(to: url, options: .atomic)
                //print(FileManager.default.fileExists(atPath : filePath))
            }
            catch
            {
                print(error)
                print("file can not be save at path \(filePath), with error : \(error)");
                filePath = ""
            }
        }
        //print("filePath \(filePath)")
        return filePath
    
    }
    
    /**
     * Load image from local storage
     */
    static func loadImage(imagePath : String?) -> UIImage?
    {
        // Create result variable
        var storedImage : UIImage? = nil
        
        // Check if file exists
        if let unwrappedPath = imagePath
        {
            let fileManager = FileManager.default

            if fileManager.fileExists(atPath : unwrappedPath)
            {
                storedImage = UIImage(contentsOfFile: unwrappedPath)
            }
            else
            {
                print("No Image at: \(unwrappedPath)")
            }
        }
        return storedImage
    }

    /**
     * Delete image from local storage
     */
    static func deleteImage(imagePath : String?)
    {
        guard let unwrappedPath = imagePath else
        {
            print("Picture path is nil. Something nasty is with DB")
            return
        }
        
        let fileManager = FileManager.default
        
        guard fileManager.fileExists(atPath : unwrappedPath) else
        {
            print("Picture '\(unwrappedPath)' does not exist")
            return
        }

        guard fileManager.isDeletableFile(atPath: unwrappedPath) else
        {
            print("Picture '\(unwrappedPath)' is undeletable")
            return
        }
        
        do
        {
            try fileManager.removeItem(atPath: unwrappedPath)
        }
        catch
        {
            print("Picture '\(unwrappedPath)' has not been deleted")
        }
    }
    
    override private init()
    {
    }
}
