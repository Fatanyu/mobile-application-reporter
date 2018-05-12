package cz.topgis.topgis_reporting.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

public class GPSLocationManager implements LocationListener
{
	private static GPSLocationManager ourInstance = null;
	private LocationManager locationManager;
	private Context context;
	private LocationListener locationListener;
	private GPSLocation actualLocation;

	public static GPSLocationManager getInstance(Context context)
	{
		if (ourInstance == null)
		{
			ourInstance = new GPSLocationManager(context);
		}
		else
		{
			ourInstance.setContext(context);
		}
		return ourInstance;
	}

	private void setContext(Context context)
	{
		if (context != null) this.context = context;
	}

	private GPSLocationManager(Context context)
	{
		this.setContext(context);
		this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		this.locationListener = this;
		this.actualLocation = GPSLocation.getDummyLocation();
	}

	@Override
	public void onLocationChanged(Location location)
	{
		this.setActualLocation(location);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{

	}

	private void setActualLocation(Location location)
	{
		if (location != null)
			this.actualLocation = (new GPSLocation(location.getLatitude(), location.getLongitude()));
		else this.actualLocation = GPSLocation.getDummyLocation();
	}

	@Override
	public void onProviderEnabled(String provider)
	{

	}

	@Override
	public void onProviderDisabled(String provider)
	{
	}

	public void registerListener(Activity activity)
	{

		/*
		if (ActivityCompat.checkSelfPermission(this.context,
				Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
				ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)*/
		if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
		{
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			Toast.makeText(this.context, "Nemas pravo na informace k gps.", Toast.LENGTH_SHORT).show();
			return;
		}
		Toast.makeText(this.context, "Zapinam GPS listener", Toast.LENGTH_SHORT).show();
		this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this.locationListener);
	}

	public void unregisterListener()
	{
		Toast.makeText(this.context, "Vypinam GPS listener", Toast.LENGTH_SHORT).show();
		this.locationManager.removeUpdates(locationListener);
	}
}
