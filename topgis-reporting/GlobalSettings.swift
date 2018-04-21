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
    static let IMAGE_MISSING = "Fotografie nebyla pořízena"
    static let IMAGE_FORMAT = ".jpeg"
    static let IMAGE_DIRECTORY_PATH = "/Documents/"

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
    
    static func saveImage(chosenImage: UIImage?, createTime : Date) -> String
    {
        var filePath : String = ""
        if let unwrappedImage = chosenImage
        {
            let directoryPath =  NSHomeDirectory().appending(GlobalSettings.IMAGE_DIRECTORY_PATH)
            if !FileManager.default.fileExists(atPath: directoryPath)
            {
                do
                {
                    try FileManager.default.createDirectory(at: NSURL.fileURL(withPath: directoryPath), withIntermediateDirectories: true, attributes: nil)
                }
                catch
                {
                    print(error)
                }
            }
            //        let filename = NSDate().string(withDateFormatter: yyyytoss).appending(".jpg")
            let fileName = GlobalSettings.getImageName(date: createTime)
            filePath = directoryPath.appending(fileName)
            let url = NSURL.fileURL(withPath: filePath)
            do
            {
                try UIImageJPEGRepresentation(unwrappedImage, 1.0)?.write(to: url, options: .atomic)
                print(FileManager.default.fileExists(atPath : filePath))
            }
            catch
            {
                print(error)
                print("file cant not be save at path \(filePath), with error : \(error)");
                filePath = ""
            }
        }
        print("filePath \(filePath)")
        return filePath
    
    }
    
    static func loadImage(imagePath : String?) -> UIImage?
    {
        var storedImage : UIImage? = nil
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
    /*
    func getImage(){
        let imagePAth = (self.getDirectoryPath() as NSString).stringByAppendingPathComponent("apple.jpg")
        if fileManager.fileExistsAtPath(imagePAth){
            self.imageView.image = UIImage(contentsOfFile: imagePAth)
        }else{
            print("No Image")
        }
    }*/
    
    /*
    var photo = yourImageFile as UIImage;
    var documentsDirectory = Environment.GetFolderPath
    (Environment.SpecialFolder.Personal);
    var directoryname = Path.Combine(documentsDirectory, "FolderName");
    Directory.CreateDirectory(directoryname);
    string jpgFilename = System.IO.Path.Combine (directoryname, "Photo.jpg"); // hardcoded filename, overwritten each time. You can make it dynamic as per your requirement.
    
    NSData imgData = photo.AsJPEG();
    NSError err = null;
    if (imgData.Save(jpgFilename, false, out err)) {
    Console.WriteLine("saved as " + jpgFilename);
    } else {
    Console.WriteLine("NOT saved as " + jpgFilename + " because" + err.LocalizedDescription);
    }*/
    
    
    
    /*
    func saveImage(image:UIImage,name:String){
        let selectedImage: UIImage = image
        let data:NSData = UIImagePNGRepresentation(selectedImage)
        let path = NSSearchPathForDirectoriesInDomains(.DocumentDirectory, .UserDomainMask, true)[0] as String
        
        let success = fileManager.createFileAtPath(path, contents: data, attributes: nil)
        print(success)
        // Check image saved successfully
        let getImagePath = (path as NSString).stringByAppendingPathComponent(name)
        if fileManager.fileExistsAtPath(getImagePath) {
            // image save success
            let image = UIImage(contentsOfFile: getImagePath)!
        }
        else{
            // image save error
        }
    }*/
    
    
    override private init()
    {
    }
}
