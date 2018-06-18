package cz.topgis.topgis_reporting.activities;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import cz.topgis.topgis_reporting.R;
import cz.topgis.topgis_reporting.database.DBContentProvider;
import cz.topgis.topgis_reporting.database.Report;
import cz.topgis.topgis_reporting.database.ReportType;
import cz.topgis.topgis_reporting.location.GPSLocation;

/**
 * Controller for adding new report
 * - it uses gps location (needs permission)
 * - it takes pictures from camera (needs permission)
 */
public class AddReportActivity extends AppCompatActivity implements LocationListener, AdapterView.OnItemSelectedListener
{

	/**
	 * Flag which helps with messages for user. True when listener is listening
	 */
	private boolean checkingLocation = false;

	/**
	 * GPS permission internal request code
	 */
	static final int REQUEST_CODE_PERMISSION_GPS = 100;

	/**
	 * Camera permission internal request code
	 */
	static final int REQUEST_CODE_TAKE_PICTURE_CAMERA = 200;

	/**
	 * Library permission internal request code
	 */
	static final int REQUEST_CODE_TAKE_PICTURE_LIBRARY = 201;

	/**
	 * Local directory name for images
	 */
	static final String IMAGE_DIRECTORY_NAME = "imageDirectory";

	/**
	 * Spinner for the report type connected to one in content_add_report.xml
	 * - it is filled by default data for now
	 * - it is not optional value
	 */
	private Spinner spinnerContentType;

	/**
	 * TextView for the report create time connected to one in content_add_report.xml
	 * - it is filled by Date() value during activity creation
	 */
	private TextView textViewContentCreateTime;
	/**
	 * TextView for the report latitude connected to one in content_add_report.xml
	 * - it is not filled by user
	 */

	private TextView textViewContentLatitude;
	/**
	 * TextView for the report longitude connected to one in content_add_report.xml
	 * - it is not filled by user
	 */
	private TextView textViewContentLongitude;

	/**
	 * EditText for the report description connected to one in content_add_report.xml
	 * - it is filled by user
	 * - it is optional value
	 */
	private EditText editTextDescription;

	/**
	 * ImageView for the report image connected to one in content_add_report.xml
	 * - it is filled, when user take picture from camera
	 * - it is optional value
	 */
	private ImageView imageView;

	/**
	 * TODO Is it needed?
	 */
	private GPSLocation currentLocation;

	/**
	 * Current selected report type from the spinner
	 */
	private ReportType selectedReportType;

	/**
	 * TODO Is it needed?
	 */
	private Date createTime;

	/**
	 * LocationManager instance
	 */
	private LocationManager locationManager;

	/**
	 * Activity initialization part
	 * - it inits location manager and starts its listener
	 * - it sets layout
	 * - it sets back button
	 * - it bounds every view from layout to this controller
	 * - it inits default values to bounded views
	 * @param savedInstanceState Previous state stored in bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// gps part
		this.initLocationManager();
		this.registerListener();

		// layout part
		setContentView(R.layout.activity_add_report);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

		// views part
		this.boundViews();
		this.setContentToTextViews();
	}

	/**
	 * LocationManager initialization and setting dummy location
	 */
	private void initLocationManager()
	{
		//Get system service
		this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//Prepare dummy location
		this.currentLocation = GPSLocation.getDummyLocation();
	}

	/**
	 * Sets and prepare spinner to use
	 * - creating ArrayAdapter with data for spinner
	 * - setting listener for spinner selection
	 */
	private void setSpinner()
	{
		//Report type array which will be used
		List<String> reportTypes = new ArrayList<>();

		// add dummy data
		reportTypes.add("Bordel");
		reportTypes.add("Skladka");
		reportTypes.add("Neco dalsiho");
		reportTypes.add("Neco hoooooodne hooodne hoooodne dlouheho");

		//This default ArrayAdapter will be used for spinner
		ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, reportTypes);
		this.spinnerContentType.setAdapter(adapter);

		//sets this class as listener for selection
		this.spinnerContentType.setOnItemSelectedListener(this);
	}

	/**
	 * Set content for entire layout
	 */
	private void setContentToTextViews()
	{
		this.setSpinner();
		this.setCreateTime();
		this.setLocationToViews();
	}

	/**
	 * Simple setter
	 */
	private void setCreateTime()
	{
		this.createTime = new Date();
		this.textViewContentCreateTime.setText(this.createTime.toString());
	}

	/**
	 * Location setter
	 * - set unknown GPS string line when dummy locations
	 */
	private void setLocationToViews()
	{
		if(this.currentLocation.isDummy())
		{
			this.textViewContentLongitude.setText(R.string.gps_not_set);
			this.textViewContentLatitude.setText(R.string.gps_not_set);
		}
		else
		{
			this.textViewContentLatitude.setText(this.currentLocation.getLatitude());
			this.textViewContentLongitude.setText(this.currentLocation.getLongitude());
		}
	}

	//https://developer.android.com/training/appbar/setting-up
	//https://developer.android.com/training/implementing-navigation/ancestral
	//https://stackoverflow.com/questions/28954586/change-title-color-in-toolbar?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa

	/**
	 * It is called when something in toolbar is selected
	 * @param item Item which has been pressed
	 * @return True if is it known, otherwise delegate to super
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				//NavUtils.navigateUpFromSameTask(this);
				this.unregisterListener();
				finish();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Connect Views from layout to class members
	 */
	private void boundViews()
	{
		this.spinnerContentType = findViewById(R.id.add_spinner);
		this.textViewContentCreateTime = findViewById(R.id.add_text_view_content_create_time);
		this.textViewContentLatitude = findViewById(R.id.add_text_view_content_latitude);
		this.textViewContentLongitude = findViewById(R.id.add_text_view_content_longitude);
		this.editTextDescription = findViewById(R.id.add_edit_text_description);
		this.imageView = findViewById(R.id.add_image_picker);
	}

	/**
	 * Check if location is still dummy
	 * @return True if location is dummy
	 */
	private boolean hasDummyLocation()
	{
		return this.createGPSLocation().isDummy();
	}

	/**
	 * Takes data from views and creates GPS location
	 * @return Current GPS location. Can be dummy
	 */
	private GPSLocation createGPSLocation()
	{
		return new GPSLocation(this.textViewContentLatitude.getText().toString(),this.textViewContentLongitude.getText().toString());
	}

	/**
	 * Creates report from filled layout and save it to database
	 * - checks enabled Location
	 * - checks dummy current location
	 * @param view Which was clicked
	 */
	public void saveNewReportOnClick(View view)
	{
		// Providers are disabled
		if(this.providersDisabled())
		{
			Toast.makeText(this, R.string.message_providers_disabled, Toast.LENGTH_SHORT).show();
			return;
		}

		// Current location is dummy but at least one provider is ON (== still looking for location)
		if(this.hasDummyLocation())
		{
			Toast.makeText(this, R.string.message_getting_location, Toast.LENGTH_SHORT).show();
			return;
		}
		// Ok, everything is ready to save report
		Report report = this.createReport();

		//Store image
		saveImageToInternalStorage(report);
		this.insertReportToDB(report);

		this.unregisterListener();
		finish();
	}

	/**
	 * Check location providers
	 * @return True if all providers all disabled
	 */
	private boolean providersDisabled()
	{
		return !(this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || this.locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
	}

	/**
	 * Store report to local DB
	 * @param report Report to store
	 */
	private void insertReportToDB(Report report)
	{
		DBContentProvider dbContentProvider = new DBContentProvider(this);
		dbContentProvider.insertReport(report);
	}

	/**
	 * Create report from layout views
	 * @return Report from user input
	 */
	private Report createReport()
	{
		return new Report(this.createTime,
				null, this.editTextDescription.getText().toString(),
				this.createGPSLocation(),
				this.selectedReportType);
	}

	/**
	 * Image picker (only from camera right now)
	 * @param view
	 */
	public void onClickImagePicker(View view)
	{
		//intent for sending
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null)
		{
			startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PICTURE_CAMERA);
		}

		//This part is for library - which is not needed right now
		//Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		//startActivityForResult(pickPhoto , REQUEST_CODE_TAKE_PICTURE_LIBRARY);
	}

	/**
	 * Overrided method for receiving images from Camera/Library apps
	 * @param requestCode Request identifier
	 * @param resultCode Result from another activity (this time Camera/Library)
	 * @param data Received data from another activity
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		//Handle request code
		switch(requestCode)
		{
			case REQUEST_CODE_TAKE_PICTURE_CAMERA:
				if(resultCode == RESULT_OK)
				{
					this.setImage(data);
				}
				break;

			case REQUEST_CODE_TAKE_PICTURE_LIBRARY:
				if(resultCode == RESULT_OK)
				{
					this.setImage(data);
				}
				break;
		}
	}

	/**
	 * Image setter
	 * - gets data from Intent
	 * - convert data to bitmap
	 * - show views and delete button in layout
	 * @param data Stores image from another activity
	 */
	private void setImage(Intent data)
	{
		Bundle extras = data.getExtras();
		if (extras != null)
		{
			//set image
			Bitmap imageBitmap = (Bitmap) extras.get("data");
			this.imageView.setImageBitmap(imageBitmap);

			//show views and button
			findViewById(R.id.text_view_label_picture).setVisibility(TextView.VISIBLE);
			findViewById(R.id.button_delete_picture).setVisibility(Button.VISIBLE);
			this.imageView.setVisibility(ImageView.VISIBLE);

		}

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
	 * @param provider The provider who is enabled (GPS or Network)
	 */
	@Override
	public void onProviderEnabled(String provider)
	{
		//Toast.makeText(this, MESSAGE_PROVIDER_ENABLED + "( " + provider + " )", Toast.LENGTH_SHORT).show();
		if(!this.checkingLocation || provider.equals(""))
		{
			this.checkingLocation = true;
			Toast.makeText(this, R.string.message_start_getting_location, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * This method is called when location provider is disabled
	 * @param provider The provider who is disabled (GPS or Network)
	 */
	@Override
	public void onProviderDisabled(String provider)
	{
		if(this.checkingLocation || provider.equals(""))
		{
			this.checkingLocation = false;
			Toast.makeText(this, R.string.message_provider_disabled, Toast.LENGTH_SHORT).show();
			//Toast.makeText(this, MESSAGE_PROVIDER_DISABLED + "( " + provider + " )", Toast.LENGTH_SHORT).show();
		}
	}


	/**
	 * Start tracking GPS location
	 */
	private void registerListener()
	{
		this.askGPSPermission();

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
		{
			Toast.makeText(this, R.string.message_missing_permission, Toast.LENGTH_SHORT).show();
			return;
		}
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
		{
			Toast.makeText(this, R.string.message_missing_permission, Toast.LENGTH_SHORT).show();
			return;
		}

		//intro messages
		if(!this.providersDisabled()) this.onProviderEnabled("");
		else this.onProviderDisabled("");

		this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		//this.locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
	}

	/**
	 *
	 */
	public void askGPSPermission()
	{
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
		{
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, AddReportActivity.REQUEST_CODE_PERMISSION_GPS);
		}
	}

	/**
	 * Stop tracking GPS location. Must be called on every exit from this activity
	 */
	private void unregisterListener()
	{
		//Toast.makeText(this, "Vypinam GPS listener", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(this,R.string.message_permission_denied, Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * What will happen after user select report type in spinner
	 * @param parent Parent
	 * @param view View
	 * @param position Selected type position in spinner
	 * @param id Id
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	{
		//Toast.makeText(this,"Spinner selected value: " + (String) this.spinnerContentType.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
		this.selectedReportType = new ReportType((String) this.spinnerContentType.getItemAtPosition(position));
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent)
	{

	}

	/**
	 * Storing image to internal directory (not library!)
	 * @param report Report with image
	 */
	private void saveImageToInternalStorage(Report report)
	{
		//get drawable part
		Drawable bitmapDrawable = this.imageView.getDrawable();
		if(bitmapDrawable == null) //no image
		{
			report.setPicturePath("");
		}
		else //some image is there
		{
			Bitmap bitmapImage = ((BitmapDrawable) bitmapDrawable).getBitmap();

			ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
			// path to /data/data/yourapp/app_data/imageDir
			File directory = contextWrapper.getDir(AddReportActivity.IMAGE_DIRECTORY_NAME, Context.MODE_PRIVATE);
			// Create imageDir
			File mypath=new File(directory,report.getCreateTime() + ".jpg"); //TODO image names

			FileOutputStream outputStream = null;
			try
			{
				outputStream = new FileOutputStream(mypath);
				// Use the compress method on the BitMap object to write image to the OutputStream
				bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					if(outputStream != null) outputStream.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			report.setPicturePath(directory.getAbsolutePath());
		}


	}

	/**
	 * Delete picture from view and hide views and button
	 * @param view Clicked view
	 */
	public void deleteOnClick(View view)
	{
		//delete image
		this.imageView.setImageBitmap(null);

		//hide views and button
		this.imageView.setVisibility(ImageView.GONE);
		findViewById(R.id.text_view_label_picture).setVisibility(TextView.GONE);
		findViewById(R.id.button_delete_picture).setVisibility(Button.GONE);

		Toast.makeText(this, R.string.message_picture_removed, Toast.LENGTH_SHORT).show();
	}
}
