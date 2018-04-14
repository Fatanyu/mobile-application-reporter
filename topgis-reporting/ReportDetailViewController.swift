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
/*    {
        didSet
        {
            // Update the view.

            configureView()

        }
    }*/
    
    //@IBOutlet weak var detailDescriptionLabel: UILabel!
    @IBOutlet weak var detailTypeLabel: UILabel!
    @IBOutlet weak var detailDescriptionLabel: UITextView!
    
    func configureView()
    {
        // Update the user interface for the detail item.

        self.detailDescriptionLabel.text = report?.reportDescription ?? Report.NOT_SET
        self.detailTypeLabel.text = report?.type ?? Report.NOT_SET
        
    }

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

}

