package cz.topgis.topgis_reporting.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import cz.topgis.topgis_reporting.MainActivity;
import cz.topgis.topgis_reporting.R;
import cz.topgis.topgis_reporting.location.GPSLocation;

public class AddReportActivity extends AppCompatActivity implements LocationListener
{
	private static final String MESSAGE_PROVIDER_DISABLED="Provider is disabled";
	private static final String MESSAGE_PROVIDER_ENABLED="Provider is enabled";
	static final int REQUEST_CODE_PERMISSION_GPS = 100;


	private Spinner textViewContentType;
	private TextView textViewContentCreateTime;
	private TextView textViewContentLatitude;
	private TextView textViewContentLongitude;
	private EditText editTextDescription;

	private GPSLocation currentLocation;

	/**
	 * Instance of LocationManager
	 */
	private LocationManager locationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.initLocationManager();
		this.registerListener();
		setContentView(R.layout.activity_add_report);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		this.boundTextViews();
		this.setContentToTextViews();
	}

	private void initLocationManager()
	{
		//Get system service
		this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//Prepare dummy location
		this.currentLocation = GPSLocation.getDummyLocation();
	}

	private void setSpinner()
	{
		List<String> reportTypes = new ArrayList<>();

		//dummy data
		reportTypes.add("Bordel");
		reportTypes.add("Skladka");

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, reportTypes);
		//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		this.textViewContentType.setAdapter(adapter);
	}

	/**
	 *
	 */
	private void setContentToTextViews()
	{
		this.setSpinner();
		this.textViewContentCreateTime.setText(new Date().toString());
		this.setLocationToViews();

	}

	private void setLocationToViews()
	{
		this.textViewContentLatitude.setText(this.currentLocation.getLatitude());
		this.textViewContentLongitude.setText(this.currentLocation.getLongitude());
	}

	//https://developer.android.com/training/appbar/setting-up
	//https://developer.android.com/training/implementing-navigation/ancestral
	//https://stackoverflow.com/questions/28954586/change-title-color-in-toolbar?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa

	/**
	 * It is called when something in toolbar is selected
	 * @param item
	 * @return
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				//NavUtils.navigateUpFromSameTask(this);
				finish();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Connect textViews from layout to class members
	 */
	private void boundTextViews()
	{
		this.textViewContentType = findViewById(R.id.add_spinner);
		this.textViewContentCreateTime = findViewById(R.id.add_text_view_content_create_time);
		this.textViewContentLatitude = findViewById(R.id.add_text_view_content_latitude);
		this.textViewContentLongitude = findViewById(R.id.add_text_view_content_longitude);
		this.editTextDescription = findViewById(R.id.add_edit_text_description);
	}


	public void saveNewReportOnClick(View view)
	{
		//TODO
		this.unregisterListener();
		finish();
	}

	public void onClickImagePicker(View view)
	{
	}

	/**
	 * When phone has new location, set it as GPSLocation
	 * @param location New actual location
	 */
	@Override
	public void onLocationChanged(Location location)
	{
		this.setCurrentLocation(location);
	}

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
		Toast.makeText(this, MESSAGE_PROVIDER_ENABLED + "( " + provider + " )", Toast.LENGTH_SHORT).show();
	}

	/**
	 * This method is called when location provider is disabled
	 * @param provider Provider
	 */
	@Override
	public void onProviderDisabled(String provider)
	{
		Toast.makeText(this, MESSAGE_PROVIDER_DISABLED + "( " + provider + " )", Toast.LENGTH_SHORT).show();
	}


	/**
	 * Start tracking GPS location
	 */
	private boolean registerListener()
	{
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
		{
			Toast.makeText(this, "Nemas pravo na informace k gps.", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
		{
			Toast.makeText(this, "Nemas pravo na informace k gps.", Toast.LENGTH_SHORT).show();
			return false;
		}
		Toast.makeText(this, "Zapinam GPS listener", Toast.LENGTH_SHORT).show();
		this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		//this.locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
		return true;
	}

	public void askGPSPermission()
	{
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
		{
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, AddReportActivity.REQUEST_CODE_PERMISSION_GPS);
		}
	}

	/**
	 * Stop tracking GPS location
	 */
	private void unregisterListener()
	{
		Toast.makeText(this, "Vypinam GPS listener", Toast.LENGTH_SHORT).show();
		this.locationManager.removeUpdates(this);
	}

	/**
	 * Simple setter. If location is null, set dummy value. Old value will be always scratched.
	 * @param location New location
	 */
	private void setCurrentLocation(Location location)
	{
		if (location != null) this.currentLocation = (new GPSLocation(location));
		else this.currentLocation = GPSLocation.getDummyLocation();

		this.setLocationToViews();
	}

	/**
	 * When app request some permission, this method is invoked after user allow/deny that permission.
	 * @param requestCode Unique identifier for request
	 * @param permissions Array of requested permissions as Strings
	 * @param grantResults Array of granted/denied values. Every value in permissions[] have value in grandResults
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		//Manage request code
		switch(requestCode)
		{
			// Request GPS permission from addButtonOnClick
			case AddReportActivity.REQUEST_CODE_PERMISSION_GPS:
			{
				if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
					this.registerListener();
				else //Do not have permission
					Toast.makeText(this,"Really need that permission", Toast.LENGTH_SHORT).show(); //TODO
			}
		}
	}
}
