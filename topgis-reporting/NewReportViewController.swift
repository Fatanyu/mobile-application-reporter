//
//  NewReportViewController.swift
//  topgis-reporting
//
//  Created by Michal Šrejber on 04/04/2018.
//  Copyright © 2018 TopGis s.r.o. All rights reserved.
//

import UIKit

class NewReportViewController: UIViewController
{
    var storage:HistoryViewController?
    
    override func viewDidLoad()
    {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        /*
        if (self.storage == nil)
        {
            self.storage = storyboard?.instantiateViewController(withIdentifier: "history") as? HistoryViewController
        }*/
    }

    override func didReceiveMemoryWarning()
    {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func clickedCancel(_ sender: Any)
    {
        self.dismiss(animated: true, completion: nil)
    }
    
    
    @IBAction func clickedSave(_ sender: Any)
    {
        //self.storage?.insertNewObject(self)
        self.storage?.insertNewReport(newReport : self.createReport())
        self.dismiss(animated: true, completion: nil)

    }
    
    func createReport() -> Report
    {
        let newReport = Report(newDescription: "Chuj", newPicture: nil, newLocation: GPSLocation(longitude: 1, latitude: 3), newReportType: ReportType(reportType: "Skládka"/*, villageId: 5*/))
        return newReport
    }
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
