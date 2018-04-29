//
//  NewReportViewController.swift
//  topgis-reporting
//
//  Created by Michal Šrejber on 04/04/2018.
//  Copyright © 2018 TopGis s.r.o. All rights reserved.
//

import UIKit
import MobileCoreServices
import AVFoundation //camera and library request
import Photos

/**
 * Based on MVC, this class is controller for screen. which is using for creation of new reports.
 * It also contains locationManager, which is getting GPS location
 */
class NewReportViewController: UIViewController, UIPickerViewDataSource, UIPickerViewDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate
{
    let location = LocationManager()
    let addPhotoImage : UIImage = UIImage(named: "ic_add_a_photo_48pt")! // Create dummy picture
    
    // Reference to main app controller, it is needed for adding new reports
    var storage : HistoryViewController?
    
    // Array of reportTypes
    var dataSource = [String]()     //belongs to UIPickerViewDataSource protocol
    {
        didSet
        {
            // Set first item in array as selectedValue
            if(dataSource.count > 0)
            {
                selectedValue = dataSource.first ?? ""
            }
        }
    }
    // variable representing selected reportType from pickerView
    var selectedValue : String = ""

    // setting dummy location to actual - just in case GPS is off/do not have signal
    var actualLocation = GPSLocation(longitude: GlobalSettings.GPS_NOT_SET,latitude: GlobalSettings.GPS_NOT_SET)


    @IBOutlet weak var eraseButton: UIButton!
    @IBOutlet weak var imageViewPicker: UIImageView!
    
    @IBOutlet weak var latitudeLabel: UILabel!
    @IBOutlet weak var longitudeLabel: UILabel!
    
    @IBOutlet weak var reportTypePicker: UIPickerView!
    @IBOutlet weak var reportDescriptionLabel: UITextView!

    /**
     * What to do after view is load
     */
    override func viewDidLoad()
    {
        super.viewDidLoad()
        self.reportTypePicker.dataSource = self
        self.reportTypePicker.delegate = self
        self.setImage()
        //https://stackoverflow.com/questions/24049020/nsnotificationcenter-addobserver-in-swift
        NotificationCenter.default.addObserver(self, selector: #selector(self.updateLocation(notification:)), name: Notification.Name("HasNewLocation"), object: nil)
        // Do any additional setup after loading the view.
    }
    
    /**
     * What to do before View will appear
     */
    override func viewWillAppear(_ animated: Bool)
    {
        location.requestLocationUpdate()
    }

    override func didReceiveMemoryWarning()
    {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    /**
     * Button Cancel is clicked, only dismiss view
     */
    @IBAction func clickedCancel(_ sender: Any)
    {
        self.dismiss(animated: true, completion: nil)
    }
    
    /**
     * Button Save is clicked
     * * if location is dummy (unknown), do nothing
     * * store report, picture and dismiss view
     */
    @IBAction func clickedSave(_ sender: Any)
    {
        
        //self.storage?.insertNewObject(self)
        if (self.actualLocation.isDummy())
        {
            return
        }

        self.storage?.insertNewReport(newReport : self.createReport())
        self.dismiss(animated: true, completion: nil)
    }
    
    /**
     * Drop picture and set default
     */
    @IBAction func eraseButtonClicked()
    {
        self.imageViewPicker.image = nil
        self.setImage()
    }
    
    /**
     * Create report from input labels and pickers
     */
    func createReport() -> Report
    {

        // Check dummy image
        if (self.imageViewPicker.image?.isEqual(self.addPhotoImage))!
        {
            self.imageViewPicker.image = nil
        }

        let newReport = Report(newDescription: self.reportDescriptionLabel.text,
                               newPicture: self.imageViewPicker.image,
                               newLocation: self.actualLocation,
                               newReportType: ReportType(reportType: self.selectedValue))
        
        //Dummy Report
        /*let newReport = Report(newDescription: "Chuj", newPicture: nil, newLocation: GPSLocation(longitude: 1, latitude: 3), newReportType: ReportType(reportType: "Skládka"/*, villageId: 5*/))*/
        return newReport
    }
    
    /**
     * Event handler method
     */
    @objc private func updateLocation(notification: Notification)
    {
        self.actualLocation = self.location.getLocation()
        self.latitudeLabel.text = self.actualLocation.latitude
        self.longitudeLabel.text = self.actualLocation.longitude
    }
    
    //https://stackoverflow.com/questions/27880607/how-to-assign-an-action-for-uiimageview-object-in-swift
    /**
     * Event handler method
     */
    @objc func changeImage(tapGestureRecognizer: UITapGestureRecognizer)
    {
        // Create picker (library/camera)
        let picker = UIImagePickerController()
        picker.delegate = self
        picker.mediaTypes = [kUTTypeImage as String]
        picker.allowsEditing = false
        
        // virtual devices does not have camera
        if UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.camera)
        {
            AVCaptureDevice.requestAccess(for: AVMediaType.video)
            {
                response in
                if (response)
                {
                    //access granted
                    picker.sourceType = UIImagePickerControllerSourceType.camera;
                    self.present(picker, animated: true, completion: nil)
                }
                else
                {
                    //yep, do nothing
                }
            }
            
            //self.requestCameraAccess(picker: picker)
        }
        else
        {
            if(PHPhotoLibrary.authorizationStatus() == PHAuthorizationStatus.authorized)
            {
                picker.sourceType = UIImagePickerControllerSourceType.photoLibrary;
                self.present(picker, animated: true, completion: nil)
            }
            else
            {
                PHPhotoLibrary.requestAuthorization(
                    {
                        (status) in
                        if (status == PHAuthorizationStatus.authorized)
                        {
                            picker.sourceType = UIImagePickerControllerSourceType.photoLibrary;
                            self.present(picker, animated: true, completion: nil)
                        }
                        else
                        {
                            //yep, do nothing
                        }
                })
            }
            
        }
        
    }
    
    /**
     * Setter with adding Event Handler
     */
    func setImage()
    {
        self.makeImageClickable()
        
        if (self.imageViewPicker.image == nil)
        {
            self.eraseButton.isHidden = true
            self.imageViewPicker.image = self.addPhotoImage
            //https://stackoverflow.com/questions/15499376/uiimageview-aspect-fit-and-center
            self.imageViewPicker.contentMode = .center
        }
    }
    
    /**
     * Add events to picture
     */
    func makeImageClickable()
    {
        let tapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(changeImage(tapGestureRecognizer:)))
        self.imageViewPicker.isUserInteractionEnabled = true
        self.imageViewPicker.addGestureRecognizer(tapGestureRecognizer)
    }
    
    /**
     * Implementation of UIPickerViewDataSource, UIPickerViewDelegate protocols
     */
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String?
    {
        return self.dataSource[row]
    }
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int
    {
        return 1 //one dimension
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int
    {
        return self.dataSource.count
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int)
    {
        // use the row to get the selected row from the picker view
        // using the row extract the value from your datasource (array[row])
        self.selectedValue = self.dataSource[row]
    }
    
    /**
     * Method managing what to do with user's image pick
     */
    @objc func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String:Any])
    {
        if let possibleImage = info["UIImagePickerControllerEditedImage"] as? UIImage
        {
            self.imageViewPicker.image = possibleImage
        }
        else if let possibleImage = info["UIImagePickerControllerOriginalImage"] as? UIImage
        {
            self.imageViewPicker.image = possibleImage
        }
        self.imageViewPicker.contentMode = .scaleToFill
        
        self.eraseButton.isHidden = false
        dismiss(animated: true, completion: nil)
    }
    
    @objc func imagePickerControllerDidCancel(_ picker: UIImagePickerController)
    {
        dismiss(animated: true, completion: nil)
    }
}
