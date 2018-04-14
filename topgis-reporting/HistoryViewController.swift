//
//  MasterViewController.swift
//  topgis-reporting
//
//  Created by Michal Šrejber on 04/04/2018.
//  Copyright © 2018 TopGis s.r.o. All rights reserved.
//

import UIKit
import CoreData

class HistoryViewController: UITableViewController, NSFetchedResultsControllerDelegate
{
    private var dbVersion : Int = 0
    
    func addVersion()
    {
        self.dbVersion+=1
    }
    func addVersion(newData : [TypHlaseni])
    {
        self.updateDB(newData: newData)
        self.dbVersion+=1
    }
    
    func updateDB(newData : [TypHlaseni])
    {
        
    }
    
    
    
    var detailViewController: ReportDetailViewController? = nil
    var managedObjectContext: NSManagedObjectContext? = nil

    private static var idHlaseni : Int = 0 //still not working

    override func viewDidLoad()
    {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        navigationItem.leftBarButtonItem = editButtonItem

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
        super.viewWillAppear(animated)
    }

    override func didReceiveMemoryWarning()
    {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
/*
    @objc
    func insertNewObject(_ sender: Any)
    {
        let context = self.fetchedResultsController.managedObjectContext
        let newEvent = Event(context: context)
             
        // If appropriate, configure the new managed object.
        newEvent.timestamp = Date()

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
*/
    static func getNewReportId() -> Int64
    {
        idHlaseni += 1
        return Int64(idHlaseni)
    }
    
    func insertNewReport(newReport : Report)
    {
        let context = self.fetchedResultsController.managedObjectContext
        let newHlaseni = Hlaseni(context: context)
        let newTyp = TypHlaseni(context:context)
        
        // If appropriate, configure the new managed object.
        

        newHlaseni.id = HistoryViewController.getNewReportId()
        newHlaseni.casVytvoreni = Date()
        newHlaseni.popis = newReport.Description
        print("Popis:\(newHlaseni.popis!)")
        newHlaseni.latitude = newReport.location.latitude
        print("latitude:\(newHlaseni.latitude)")
        newHlaseni.longitude = newReport.location.longitude
        print("longitude:\(newHlaseni.longitude)")
        
        print("Typ hlaseni:\(newReport.reportType!.reportType)")
        
        let aaa : String = newReport.reportType!.reportType
        print("Typ hlaseni aaa:\(aaa)")
        //newHlaseni.hlaseni_typ? = TypHlaseni()
        //newHlaseni.hlaseni_typ?.typ = String?()
        /*
        if let x = newReport.reportType?.reportType
        {
            print("x:\(x)")
            newHlaseni.hlaseni_typ? = TypHlaseni()
            if let xxx = newHlaseni.hlaseni_typ
            {
                xxx.typ = x
                print("xxx:\(xxx.typ)")
            }
            //    newHlaseni.hlaseni_typ?.typ="x"
            //print(newHlaseni.hlaseni_typ?.typ)
        }*/
//        newHlaseni.hlaseni_typ!.typ! = aaa.description
//        newHlaseni.hlaseni_typ!.typ = aaa
        
       //print("Typ hlaseni:\(newHlaseni.hlaseni_typ!.typ)")
        //newHlaseni.hlaseni_typ?.idObce = Int64(newReport.reportType!.villageId)
        //print()
        newHlaseni.odeslano = false
        
        //newHlaseni.foto = newReport.picture?.accessibilityIdentifier
        
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

    override func prepare(for segue: UIStoryboardSegue, sender: Any?)
    {
        if segue.identifier == "showDetail"
        {
            if let indexPath = tableView.indexPathForSelectedRow
            {
                let object = fetchedResultsController.object(at: indexPath)
                let controller = (segue.destination as! UINavigationController).topViewController as! ReportDetailViewController
                /*controller.detailItem = object
                controller.navigationItem.leftBarButtonItem = splitViewController?.displayModeButtonItem
                controller.navigationItem.leftItemsSupplementBackButton = true*/
                controller.hlaseni = object as Hlaseni?
            }
        }
        else if segue.identifier == "newReport"
        {
            let controller = (segue.destination as! UINavigationController).topViewController as! NewReportViewController
            controller.storage = self
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
        let hlaseni = fetchedResultsController.object(at: indexPath)
        configureCell(cell, withHlaseni: hlaseni)
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
            context.delete(fetchedResultsController.object(at: indexPath))
                
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

    func configureCell(_ cell: UITableViewCell, withHlaseni hlaseni: Hlaseni)
    {
        cell.textLabel!.text = String(hlaseni.id.description)
    }

    // MARK: - Fetched results controller

    var fetchedResultsController: NSFetchedResultsController<Hlaseni>
    {
        if _fetchedResultsController != nil
        {
            return _fetchedResultsController!
        }
     
        let fetchRequest: NSFetchRequest<Hlaseni> = Hlaseni.fetchRequest()
        
        // Set the batch size to a suitable number.
        fetchRequest.fetchBatchSize = 20
        
        // Edit the sort key as appropriate.
//        let sortDescriptor = NSSortDescriptor(key: "timestamp", ascending: false)
        let sortDescriptor = NSSortDescriptor(key: "id", ascending: false)
        
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
    var _fetchedResultsController: NSFetchedResultsController<Hlaseni>? = nil

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
                configureCell(tableView.cellForRow(at: indexPath!)!, withHlaseni: anObject as! Hlaseni)
            case .move:
                configureCell(tableView.cellForRow(at: indexPath!)!, withHlaseni: anObject as! Hlaseni)
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

