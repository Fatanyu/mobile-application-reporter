//
//  DetailViewController.swift
//  topgis-reporting
//
//  Created by Michal Šrejber on 04/04/2018.
//  Copyright © 2018 TopGis s.r.o. All rights reserved.
//

import UIKit

/**
 * Based on MVC, this class is controller for screen. which is using for showing report details
 */
class ReportDetailViewController: UIViewController
{
    // One already created report with values
    var report : ReportEntity? = nil
    
    @IBOutlet weak var detailTypeLabel: UILabel!
    @IBOutlet weak var detailDescriptionLabel: UITextView!
    @IBOutlet weak var detailCreateTimeLabel: UILabel!
    @IBOutlet weak var detailSendTimeLabel: UILabel!
    @IBOutlet weak var detailSendLabel: UILabel!
    @IBOutlet weak var detailLatitudeLabel: UILabel!
    @IBOutlet weak var detailLongitudeLabel: UILabel!
    
    @IBOutlet weak var detailImageLabel: UILabel!
    @IBOutlet weak var detailImage: UIImageView!
    
    
    override func viewDidLoad()
    {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        // prepare screen
        configureView()
    }

    override func didReceiveMemoryWarning()
    {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    /**
     * Configure/Update screen
     */
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
        self.setImage()
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
        self.detailCreateTimeLabel.text = GlobalSettings.getTimeLocaleAsTwoLines(date: report?.createTime)
    }
    
    func setSendTimeLabel()
    {
        self.detailSendTimeLabel.text = GlobalSettings.getTimeLocaleAsTwoLines(date: report?.sendTime)
    }
    
    // TODO - maybe delete?
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
    
    /**
     * Set Image from local storage. Add default when no image has been found
     */
    func setImage()
    {
        if let unwrappedValue = GlobalSettings.loadImage(imagePath: report?.image)
        {
            // stored picture
            self.detailImage.image = unwrappedValue
            self.detailImage.contentMode = .scaleToFill
        }
        else
        {
            // default picture
            self.detailImage.contentMode = .center
            self.detailImageLabel.text = GlobalSettings.IMAGE_MISSING
            self.detailImage.image = UIImage(named: "ic_highlight_off_48pt")
        }
    }
}

