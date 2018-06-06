package cz.topgis.topgis_reporting.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

/**
 * Blackboxing work with GPS
 */
public class GPSLocationManager implements LocationListener
{
	private static final String MESSAGE_PROVIDER_DISABLED="Provider is disabled";
	private static final String MESSAGE_PROVIDER_ENABLED="Provider is enabled";


	/**
	 * Static attribute of this class (Singleton pattern)
	 */
	@SuppressLint("StaticFieldLeak")
	private static GPSLocationManager ourInstance;

	/**
	 * Instance of LocationManager
	 */
	private LocationManager locationManager;

	/**
	 * Instance of actual Context
	 */
	private Context context;

	/**
	 * Instance of LocationListener (it is this class)
	 */
	private LocationListener locationListener;

	/**
	 * Actual (last known) GPS coordinates
	 */
	private GPSLocation actualLocation;

	/**
	 * Singleton getter
	 * @param context Actual context
	 * @return Instance of this class with context from parameter
	 */
	public static GPSLocationManager getInstance(Context context)
	{
		if (ourInstance == null) ourInstance = new GPSLocationManager(context);
		else ourInstance.setContext(context);
		return ourInstance;
	}

	/**
	 * Simple setter. Do nothing if parameter is null
	 * @param context New context
	 */
	private void setContext(Context context)
	{
		if (context != null) this.context = context;
	}

	/**
	 * Private Constructor which needs context
	 * @param context Actual context (should be from MainActivity)
	 */
	private GPSLocationManager(Context context)
	{
		this.setContext(context);
		//Get system service
		this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		//Set listener (plus this class must implements LocationListener)
		this.locationListener = this;
		//Prepare dummy location
		this.actualLocation = GPSLocation.getDummyLocation();
	}

	/**
	 * Start tracking GPS location
	 */
	public void registerListener()
	{
		if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
		{
			Toast.makeText(this.context, "Nemas pravo na informace k gps.", Toast.LENGTH_SHORT).show();
			return;
		}
		if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
		{
			Toast.makeText(this.context, "Nemas pravo na informace k gps.", Toast.LENGTH_SHORT).show();
			return;
		}
		Toast.makeText(this.context, "Zapinam GPS listener", Toast.LENGTH_SHORT).show();
		this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this.locationListener);
		this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this.locationListener);
		//this.locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
	}

	/**
	 * Stop tracking GPS location
	 */
	public void unregisterListener()
	{
		Toast.makeText(this.context, "Vypinam GPS listener", Toast.LENGTH_SHORT).show();
		this.locationManager.removeUpdates(locationListener);
	}

	/**
	 * Simple setter. If location is null, set dummy value. Old value will be always scratched.
	 * @param location New location
	 */
	private void setActualLocation(Location location)
	{
		if (location != null) this.actualLocation = (new GPSLocation(location.getLatitude(), location.getLongitude()));
		else this.actualLocation = GPSLocation.getDummyLocation();
	}

	/**
	 * Simple getter
	 * @return Actual location as GPSLocation
	 */
	public GPSLocation getActualLocation()
	{
		return actualLocation;
	}

	//
	// LocationListener inferface implementation
	//

	/**
	 * When phone has new location, set it as GPSLocation
	 * @param location New actual location
	 */
	@Override
	public void onLocationChanged(Location location)
	{
		this.setActualLocation(location);
	}

	/**
	 * TODO - for what is this??
	 * @param provider
	 * @param status
	 * @param extras
	 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{

	}

	/**
	 * This method is called when location provider is enabled
	 * @param provider Provider
	 */
	@Override
	public void onProviderEnabled(String provider)
	{
		Toast.makeText(this.context, MESSAGE_PROVIDER_ENABLED,Toast.LENGTH_SHORT).show();
	}

	/**
	 * This method is called when location provider is disabled
	 * @param provider Provider
	 */
	@Override
	public void onProviderDisabled(String provider)
	{
		Toast.makeText(this.context, MESSAGE_PROVIDER_DISABLED + "( " + provider + " )",Toast.LENGTH_SHORT).show();
	}


}
