//
//  DetailViewController.swift
//  topgis-reporting
//
//  Created by Michal Šrejber on 04/04/2018.
//  Copyright © 2018 TopGis s.r.o. All rights reserved.
//

import UIKit

class ReportDetailViewController: UIViewController
{
    var report : ReportEntity? = nil
    
    @IBOutlet weak var detailTypeLabel: UILabel!
    @IBOutlet weak var detailDescriptionLabel: UITextView!
    @IBOutlet weak var detailCreateTimeLabel: UILabel!
    @IBOutlet weak var detailSendTimeLabel: UILabel!
    @IBOutlet weak var detailSendLabel: UILabel!
    @IBOutlet weak var detailLatitudeLabel: UILabel!
    @IBOutlet weak var detailLongitudeLabel: UILabel!
    
    override func viewDidLoad()
    {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        configureView()
    }

    override func didReceiveMemoryWarning()
    {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func configureView()
    {
        // Update the user interface for the detail item.
        self.setSendLabel()
        self.setTypeLabel()
        self.setSendTimeLabel()
        self.setCreateTimeLabel()
        self.setDescriptionLabel()
        self.setLongitudeLabel()
        self.setLatitudeLabel()
    }
    
    func setDescriptionLabel()
    {
        self.detailDescriptionLabel.text = report?.reportDescription ?? GlobalSettings.REPORT_NOT_SET
    }
    
    func setTypeLabel()
    {
        self.detailTypeLabel.text = report?.type ?? GlobalSettings.REPORT_NOT_SET
    }
    
    func setCreateTimeLabel()
    {
        self.detailCreateTimeLabel.text = GlobalSettings.getTimeLocale(date: report?.createTime)
    }
    
    func setSendTimeLabel()
    {
        self.detailSendTimeLabel.text = GlobalSettings.getTimeLocale(date: report?.sendTime)
    }
    
    func setSendLabel()
    {
        if let unwrappedValue = report?.send
        {
            self.detailSendLabel.text =  unwrappedValue ? GlobalSettings.BOOL_STRING_YES : GlobalSettings.BOOL_STRING_NO
        }
        else
        {
            self.detailSendLabel.text = GlobalSettings.BOOL_STRING_NO
        }
    }
    
    func setLatitudeLabel()
    {
        if let unwrappedValue = report?.latitude
        {
            self.detailLatitudeLabel.text = "\(unwrappedValue)"
        }
        else
        {
            self.detailLatitudeLabel.text = GlobalSettings.GPS_NOT_SET
        }
    }
    
    func setLongitudeLabel()
    {
        if let unwrappedValue = report?.longitude
        {
            self.detailLongitudeLabel.text = "\(unwrappedValue)"
        }
        else
        {
            self.detailLongitudeLabel.text = GlobalSettings.GPS_NOT_SET
        }
    }
}

