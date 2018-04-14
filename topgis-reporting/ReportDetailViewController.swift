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
    var hlaseni : Hlaseni? = nil
/*    {
        didSet
        {
            // Update the view.

            configureView()

        }
    }*/
    
    @IBOutlet weak var detailDescriptionLabel: UILabel!
    @IBOutlet weak var detailTypeLabel: UILabel!
    
    func configureView()
    {
        // Update the user interface for the detail item.
        /*if let detail = detailItem
        {
            if let label = detailDescriptionLabel
            {
                label.text = String(detail.id.description)
            }
        }*/
        //print(hlaseni)
        /*if let unwrappedHlaseni = self.hlaseni
        {
            if let unwrappedTypeHlaseni = unwrappedHlaseni.hlaseni_typ
            {
                if let unwrappedType = unwrappedTypeHlaseni.typ
                {
                    self.detailTypeLabel.text = unwrappedType
                }
            }
            //self.detailTypeLabel.text = unwrappedHlaseni.hlaseni_typ?.typ
        }*/
        //self.detailTypeLabel.text = hlaseni?.typ?.typ

        self.detailDescriptionLabel.text = hlaseni?.popis
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

