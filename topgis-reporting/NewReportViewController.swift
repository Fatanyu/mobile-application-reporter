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

class NewReportViewController: UIViewController, UIPickerViewDataSource, UIPickerViewDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate
{
    let location = LocationManager()
    let addPhotoImage : UIImage = UIImage(named: "ic_add_a_photo_48pt")!
    
    var storage:HistoryViewController?
    var dataSource = [String]()     //belongs to UIPickerViewDataSource protocol
    {
        didSet
        {
            if(dataSource.count > 0)
            {
                selectedValue = dataSource.first ?? ""
            }
        }
    }
    var selectedValue = String()

    var actualLocation = GPSLocation(longitude: GlobalSettings.GPS_NOT_SET,latitude: GlobalSettings.GPS_NOT_SET)


    @IBOutlet weak var eraseButton: UIButton!
    @IBOutlet weak var imageViewPicker: UIImageView!
    
    @IBOutlet weak var latitudeLabel: UILabel!
    @IBOutlet weak var longitudeLabel: UILabel!
    
    @IBOutlet weak var reportTypePicker: UIPickerView!
    @IBOutlet weak var reportDescriptionLabel: UITextView!

    
    override func viewDidLoad()
    {
        super.viewDidLoad()
        self.reportTypePicker.dataSource = self
        self.reportTypePicker.delegate = self
        self.setImage()

        // Do any additional setup after loading the view.
    }
    override func viewWillAppear(_ animated: Bool)
    {
        //https://stackoverflow.com/questions/24049020/nsnotificationcenter-addobserver-in-swift
        NotificationCenter.default.addObserver(self, selector: #selector(self.updateLocation(notification:)), name: Notification.Name("HasNewLocation"), object: nil)
        location.requestLocationUpdate()
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
        if (self.actualLocation.isDummy())
        {
            return
        }
        
        self.storage?.insertNewReport(newReport : self.createReport())
        self.dismiss(animated: true, completion: nil)

    }
    
    
    @IBAction func eraseButtonClicked()
    {
        self.imageViewPicker.image = nil
        self.setImage()
    }
    
    func createReport() -> Report
    {

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
    
    @objc private func updateLocation(notification: Notification)
    {
        self.actualLocation = self.location.getLocation()
        self.latitudeLabel.text = self.actualLocation.latitude
        self.longitudeLabel.text = self.actualLocation.longitude
    }
    
    //https://stackoverflow.com/questions/27880607/how-to-assign-an-action-for-uiimageview-object-in-swift
    @objc func changeImage(tapGestureRecognizer: UITapGestureRecognizer)
    {

        
        let picker = UIImagePickerController()
        picker.delegate = self
        picker.mediaTypes = [kUTTypeImage as String]
        
        // virtual devices does not have camera
        if UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.camera)
        {
            picker.sourceType = UIImagePickerControllerSourceType.camera;
            //self.requestCameraAccess(picker: picker)
        }
        else
        {
            picker.sourceType = UIImagePickerControllerSourceType.photoLibrary;
        }
        self.present(picker, animated: true, completion: nil)
    }
    
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
    
    func makeImageClickable()
    {
        let tapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(changeImage(tapGestureRecognizer:)))
        self.imageViewPicker.isUserInteractionEnabled = true
        self.imageViewPicker.addGestureRecognizer(tapGestureRecognizer)
    }
    /*
        Implementation of UIPickerViewDataSource, UIPickerViewDelegate protocols
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
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String:Any])
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
        // process the result
        
        self.eraseButton.isHidden = false
        dismiss(animated: true, completion: nil)
    }
    
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController)
    {
        dismiss(animated: true, completion: nil)
    }
    
    func requestCameraAccess(picker : UIImagePickerController)
    {
    //Camera
        AVCaptureDevice.requestAccess(for: AVMediaType.video)
        {
            response in if response
            {
                //access granted
                self.present(picker, animated: true, completion: nil)
            }
            else
            {
    
            }
        }
    }
    /*
    func requestLibraryAccess()
    {
        //Photos
        let photos = PHPhotoLibrary.authorizationStatus()
        if photos == .notDetermined {
            PHPhotoLibrary.requestAuthorization({status in
                if status == .authorized{
                    
    } else {}
    })
    }
    }*/
    
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
