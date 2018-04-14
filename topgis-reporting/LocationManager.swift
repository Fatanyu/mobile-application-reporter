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

struct GPSLocation
{
    static let NOT_SET = "Neznámá"
    let longitude : String
    let latitude : String
    
    func equal(location : GPSLocation)-> Bool
    {
        return self.longitude == location.longitude && self.latitude == location.latitude
    }
    
    func isDummy() -> Bool
    {
        return self.equal(location: GPSLocation(longitude: GPSLocation.NOT_SET,latitude: GPSLocation.NOT_SET))
    }
}

class LocationManager: NSObject, CLLocationManagerDelegate
{
    let locationManager : CLLocationManager //https://stackoverflow.com/questions/26142441/cllocationdegrees-to-string-variable-in-swift
    let UNKNOWN_LOCATION : GPSLocation
    
    private var actualLocation : GPSLocation
    
    override init()
    {
        self.locationManager = CLLocationManager()
        self.UNKNOWN_LOCATION = GPSLocation(longitude: GPSLocation.NOT_SET, latitude: GPSLocation.NOT_SET)
        self.actualLocation = self.UNKNOWN_LOCATION
        super.init()
        self.locationManager.delegate = self
    }
    
    func getActualLocation() //-> GPSLocation
    {
        //self.locationManager.requestAlwaysAuthorization() //can run on background
        self.locationManager.requestWhenInUseAuthorization() //only on frontend
        locationManager.requestLocation()
        //self.getLocation()
        //return self.actualLocation
    }
    
    
    func locationManager(_ manager: CLLocationManager , didUpdateLocations locations: [CLLocation])
    {
        var coordinates = self.UNKNOWN_LOCATION
        if let location = locationManager.location
        {
            let latitude: String = "\(location.coordinate.longitude)"
            let longtitude: String = "\(location.coordinate.latitude)"
            coordinates = GPSLocation(longitude: longtitude, latitude: latitude)
        }
        
        self.actualLocation = coordinates
    }
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error)
    {
        self.actualLocation = self.UNKNOWN_LOCATION
        print("Failed to find user's location: \(error.localizedDescription)")
    }
}
