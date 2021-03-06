//
//  LocationManager.swift
//  topgis-reporting
//
//  Created by Michal Šrejber on 14/04/2018.
//  Copyright © 2018 TopGis s.r.o. All rights reserved.
//

import UIKit
import CoreLocation
//https://www.hackingwithswift.com/example-code/location/how-to-request-a-users-location-only-once-using-requestlocation

/**
 * Represents GPS location without elevation. Implements equal() and isDummy()
 */
struct GPSLocation
{
    let longitude : String
    let latitude : String
    
    func equal(location : GPSLocation)-> Bool
    {
        return self.longitude == location.longitude && self.latitude == location.latitude
    }
    
    func isDummy() -> Bool
    {
        return self.equal(location: GPSLocation(longitude: GlobalSettings.GPS_NOT_SET,latitude: GlobalSettings.GPS_NOT_SET))
    }
}

/**
 * Represents managing location, request for location is requested only when needed.
 */
class LocationManager: NSObject, CLLocationManagerDelegate
{
    let locationManager : CLLocationManager //https://stackoverflow.com/questions/26142441/cllocationdegrees-to-string-variable-in-swift
    // Dummy location
    let UNKNOWN_LOCATION : GPSLocation
    
    // real location
    var actualLocation : GPSLocation
    
    override init()
    {
        self.locationManager = CLLocationManager()
        self.UNKNOWN_LOCATION = GPSLocation(longitude: GlobalSettings.GPS_NOT_SET, latitude: GlobalSettings.GPS_NOT_SET)
        self.actualLocation = self.UNKNOWN_LOCATION
        super.init()
        self.locationManager.delegate = self
    }
    
    /**
     * Request one time location update
     */
    func requestLocationUpdate() //-> GPSLocation
    {
        //self.locationManager.requestAlwaysAuthorization() //can run on background
        self.locationManager.requestWhenInUseAuthorization() //only on frontend
        if CLLocationManager.locationServicesEnabled()
        {
            locationManager.requestLocation()
        }
        //self.getLocation()
        //return self.actualLocation
    }
    
    /**
     * Classic getter, but it can holds old location
     */
    func getLocation() -> GPSLocation
    {
        return self.actualLocation
    }
    
    /**
     * Classic implementation with event
     */
    func locationManager(_ manager: CLLocationManager , didUpdateLocations locations: [CLLocation])
    {
        //var coordinates = self.UNKNOWN_LOCATION
        if let location = locationManager.location
        {
            //print("here")
            let latitude: String = "\(location.coordinate.longitude)"
            let longtitude: String = "\(location.coordinate.latitude)"
            //coordinates = GPSLocation(longitude: longtitude, latitude: latitude)
            self.actualLocation = GPSLocation(longitude: longtitude, latitude: latitude)
            //print("Creating notification")
            // send event to screen to update labels
            NotificationCenter.default.post(name: Notification.Name("HasNewLocation"), object: nil)
        }
        
        //self.actualLocation = coordinates
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error)
    {
        self.actualLocation = self.UNKNOWN_LOCATION
        print("Failed to find user's location: \(error.localizedDescription)")
    }
}
