//
//  MasterViewController.swift
//  topgis-reporting
//
//  Created by Michal Šrejber on 04/04/2018.
//  Copyright © 2018 TopGis s.r.o. All rights reserved.
//

import UIKit
import CoreData


/**
 * Based on MVC, this class is controller for main app screen. It shows list of all reports
 * It also managing core data, including inserting new data
 */
class HistoryViewController: UITableViewController, NSFetchedResultsControllerDelegate
{
    // Property for checking first app run. Inserting dummy values to DB depends on it
    private var firstRun : Bool //https://stackoverflow.com/questions/26830285/ios-app-first-launch
    {
        get
        {
            let defaults = UserDefaults.standard
            if let _ = defaults.string(forKey: "isAppAlreadyLaunchedOnce")
            {
                //print("App already launched")
                return false
            }
            else
            {
                defaults.set(true, forKey: "isAppAlreadyLaunchedOnce")
                //print("App launched first time")
                return true
            }
        }
    }
    private var sending : Bool = false

    var detailViewController: ReportDetailViewController? = nil
    var managedObjectContext: NSManagedObjectContext? = nil

    //private static var idHlaseni : Int = 0 //still not working

    override func viewDidLoad()
    {
        super.viewDidLoad()
        
        //insert dummy values
        if(self.firstRun)
        {
            self.insertDummyValues()
        }
        
        // Do any additional setup after loading the view, typically from a nib.
        let deleteButton = editButtonItem
        //deleteButton.title = "Smazat" // TODO - try use it
        navigationItem.leftBarButtonItem = deleteButton

        //let addButton = UIBarButtonItem(barButtonSystemItem: .add, target: self, action: #selector(insertNewObject(_:)))
        //navigationItem.rightBarButtonItem = addButton
        if let split = splitViewController
        {
            let controllers = split.viewControllers
            detailViewController = (controllers[controllers.count-1] as! UINavigationController).topViewController as? ReportDetailViewController
        }
        
     

        
    }

    override func viewWillAppear(_ animated: Bool)
    {
        clearsSelectionOnViewWillAppear = splitViewController!.isCollapsed
        print("here")
        sendReports(reports: getReportsToSend())
        super.viewWillAppear(animated)
    }

    override func didReceiveMemoryWarning()
    {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    /**
     * Inserting dummy values to DB
     *
     */
    func insertDummyValues()
    {
        var dummyValues = [ReportType]()
        dummyValues.append(ReportType(reportType: "Skládka"))
        dummyValues.append(ReportType(reportType: "Bordel"))
        dummyValues.append(ReportType(reportType: "Mimozemšťan"))
        dummyValues.append(ReportType(reportType: "Chybný"))
        dummyValues.append(ReportType(reportType: "Poctivý politik"))
        self.insertNewReportType(newReportTypes: dummyValues)
    }
    
    func sendReport(oneReport : ReportEntity)
    {
        print("\(oneReport.type) + \(oneReport.createTime)")
        
        // TODO
        let networkClientManager = NetworkClientManager(report: oneReport)
        
        networkClientManager.sendReport()
        //networkClientManager.requestLogin()
        
        //networkClientManager.requestLogout()
        
        //brno
        //networkClientManager.requestPositionId(location : GPSLocation(longitude: "16.606837", latitude: "49.195060"))
        //sokolov
        //networkClientManager.requestPositionId(longitude: 12.642791, latitude: 50.179958)
    }
    
    func sendReports(reports : [ReportEntity])
    {
        if(self.sending)
        {
            return //I am already sending
        }
        else
        {
            self.sending = true
        }
        for oneReport in reports
        {
            sendReport(oneReport: oneReport)
            break
        }
        
        self.sending = false
    }
    
    func getReportsToSend() -> [ReportEntity]
    {
        // variable with results for method's return
        var reportTypeDictionary = [ReportEntity]()
        
        // Prepare fetching to get data from DB
        let request = NSFetchRequest<NSFetchRequestResult>(entityName: "ReportEntity")
        request.returnsObjectsAsFaults = false
        
        // Fetching
        let context = self.fetchedResultsController.managedObjectContext
        do
        {
            let result = try context.fetch(request)
            
            // Add types to dictionary
            for data in result as! [ReportEntity]
            {
                //print("Adding data.typ:\(data.type!)")
                if (!data.send)
                {
                    reportTypeDictionary.append(data)
                }
            }
        }
        catch
        {
            print("Fetching dummy values from ReportType failed")
        }
        return reportTypeDictionary
    }
    
    
    /**
     * Fetch all report types to array as Strings
     *
     */
    func getReportTypeData() -> [String]
    {
        // variable with results for method's return
        var reportTypeDictionary = [String]()
        
        // Prepare fetching to get data from DB
        let request = NSFetchRequest<NSFetchRequestResult>(entityName: "ReportTypeEntity")
        request.returnsObjectsAsFaults = false
        
        // Fetching
        let context = self.fetchedResultsController.managedObjectContext
        do
        {
            let result = try context.fetch(request)
            
            // Add types to dictionary
            for data in result as! [ReportTypeEntity]
            {
                //print("Adding data.typ:\(data.type!)")
                if let openData = data.type
                {
                    reportTypeDictionary.append(openData)
                }
            }
        }
        catch
        {
            print("Fetching dummy values from ReportType failed")
        }
        return reportTypeDictionary
    }
    
    /**
     * Inserting reportTypes to DB
     */
    func insertNewReportType(newReportTypes : [ReportType])
    {
        for oneType in newReportTypes
        {
            self.insertNewReportType(newReportType : oneType)
        }
    }
    
    /**
     * Inserting one reportType to DB
     */
    func insertNewReportType(newReportType : ReportType)
    {
        // Prepare Core Data DB
        let context = self.fetchedResultsController.managedObjectContext
        
        // Something like cursor, setup new record and save (execute) it
        let newTypes = ReportTypeEntity(context:context)
        
        // Setting actual value
        newTypes.type = newReportType.reportType
        
        // This is not needed right now
        /*
        print("newTypes.idObce:\(newTypes.idObce)")
        print("newTypes.typ:\(newTypes.typ!)")
        */
        
        // Save it to DB
        do
        {
            try context.save()
        }
        catch
        {
            // Replace this implementation with code to handle the error appropriately.
            // fatalError() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
            let nserror = error as NSError
            fatalError("Unresolved error \(nserror), \(nserror.userInfo)")
        }
    }
    
    /**
     * Insert one Report to DB
     */
    func insertNewReport(newReport : Report)
    {
        //https://stackoverflow.com/questions/6884562/how-do-i-save-an-image-to-the-sandbox-iphone-sdk
        
        // Prepare Core Data DB
        let context = self.fetchedResultsController.managedObjectContext
        let report = ReportEntity(context: context)

        // Setting values
        report.latitude = Double(newReport.location.latitude)!
        report.longitude = Double(newReport.location.longitude)!
        report.reportDescription = newReport.Description
        report.createTime = newReport.CreateTime
        report.send = false
        report.type = newReport.reportType?.reportType
        report.sendTime = nil
        // Image is stored elsewhere
        report.image = GlobalSettings.saveImage(chosenImage: newReport.picture, createTime: newReport.CreateTime)

        // Save the context.
        do
        {
            try context.save()
        }
        catch
        {
            // Replace this implementation with code to handle the error appropriately.
            // fatalError() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
            let nserror = error as NSError
            fatalError("Unresolved error \(nserror), \(nserror.userInfo)")
        }
    }
  
    // MARK: - Segues

    /**
     * What will be done before new screen will appear
     */
    override func prepare(for segue: UIStoryboardSegue, sender: Any?)
    {
        if (segue.identifier == "showDetail")
        {
            if let indexPath = tableView.indexPathForSelectedRow
            {
                let object = fetchedResultsController.object(at: indexPath)
                let controller = (segue.destination as! UINavigationController).topViewController as! ReportDetailViewController
                controller.report = object as ReportEntity?
            }
        }
        else if (segue.identifier == "newReport")
        {
            // Set this view, so the newReport can inserting to DB
            let controller = (segue.destination as! UINavigationController).topViewController as! NewReportViewController
            controller.storage = self
            // View needs reportTypes
            controller.dataSource = self.getReportTypeData()
        }
    }

    // MARK: - Table View

    override func numberOfSections(in tableView: UITableView) -> Int
    {
        return fetchedResultsController.sections?.count ?? 0
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int
    {
        let sectionInfo = fetchedResultsController.sections![section]
        return sectionInfo.numberOfObjects
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell
    {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath)
        let report = fetchedResultsController.object(at: indexPath)
        configureCell(cell, withReport: report)
        return cell
    }

    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool
    {
        // Return false if you do not want the specified item to be editable.
        return true
    }

    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath)
    {
        if editingStyle == .delete
        {
            let context = fetchedResultsController.managedObjectContext
            //delete picture from local storage
            GlobalSettings.deleteImage(imagePath: fetchedResultsController.object(at: indexPath).image)
            context.delete(fetchedResultsController.object(at: indexPath))
            
            // Save deletion
            do
            {
                try context.save()
            }
            catch
            {
                // Replace this implementation with code to handle the error appropriately.
                // fatalError() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
                let nserror = error as NSError
                fatalError("Unresolved error \(nserror), \(nserror.userInfo)")
            }
        }
    }

    func configureCell(_ cell: UITableViewCell, withReport report: ReportEntity)
    {
        cell.textLabel!.text = "\(GlobalSettings.getTimeLocale(date: report.createTime))\n\(report.type ?? "")"
    }

    // MARK: - Fetched results controller

    var fetchedResultsController: NSFetchedResultsController<ReportEntity>
    {
        if _fetchedResultsController != nil
        {
            return _fetchedResultsController!
        }
     
        let fetchRequest: NSFetchRequest<ReportEntity> = ReportEntity.fetchRequest()
        
        // Set the batch size to a suitable number.
        fetchRequest.fetchBatchSize = 20
        
        // Edit the sort key as appropriate.
        let sortDescriptor = NSSortDescriptor(key: "createTime", ascending: false)
        
        fetchRequest.sortDescriptors = [sortDescriptor]
        
        // Edit the section name key path and cache name if appropriate.
        // nil for section name key path means "no sections".
        let aFetchedResultsController = NSFetchedResultsController(fetchRequest: fetchRequest, managedObjectContext: self.managedObjectContext!, sectionNameKeyPath: nil, cacheName: "Master")
        aFetchedResultsController.delegate = self
        _fetchedResultsController = aFetchedResultsController
        
        do
        {
            try _fetchedResultsController!.performFetch()
        }
        catch
        {
             // Replace this implementation with code to handle the error appropriately.
             // fatalError() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development. 
             let nserror = error as NSError
             fatalError("Unresolved error \(nserror), \(nserror.userInfo)")
        }
 
        return _fetchedResultsController!
    }    
    var _fetchedResultsController: NSFetchedResultsController<ReportEntity>? = nil

    func controllerWillChangeContent(_ controller: NSFetchedResultsController<NSFetchRequestResult>)
    {
        tableView.beginUpdates()
    }

    func controller(_ controller: NSFetchedResultsController<NSFetchRequestResult>, didChange sectionInfo: NSFetchedResultsSectionInfo, atSectionIndex sectionIndex: Int, for type: NSFetchedResultsChangeType)
    {
        switch type
        {
            case .insert:
                tableView.insertSections(IndexSet(integer: sectionIndex), with: .fade)
            case .delete:
                tableView.deleteSections(IndexSet(integer: sectionIndex), with: .fade)
            default:
                return
        }
    }

    func controller(_ controller: NSFetchedResultsController<NSFetchRequestResult>, didChange anObject: Any, at indexPath: IndexPath?, for type: NSFetchedResultsChangeType, newIndexPath: IndexPath?)
    {
        switch type
        {
            case .insert:
                tableView.insertRows(at: [newIndexPath!], with: .fade)
            case .delete:
                tableView.deleteRows(at: [indexPath!], with: .fade)
            case .update:
                configureCell(tableView.cellForRow(at: indexPath!)!, withReport: anObject as! ReportEntity)
            case .move:
                configureCell(tableView.cellForRow(at: indexPath!)!, withReport: anObject as! ReportEntity)
                tableView.moveRow(at: indexPath!, to: newIndexPath!)
        }
    }

    func controllerDidChangeContent(_ controller: NSFetchedResultsController<NSFetchRequestResult>)
    {
        tableView.endUpdates()
    }

    /*
     // Implementing the above methods to update the table view in response to individual changes may have performance implications if a large number of changes are made simultaneously. If this proves to be an issue, you can instead just implement controllerDidChangeContent: which notifies the delegate that all section and object changes have been processed.
     
     func controllerDidChangeContent(controller: NSFetchedResultsController) {
         // In the simplest, most efficient, case, reload the table view.
         tableView.reloadData()
     }
     */

}

