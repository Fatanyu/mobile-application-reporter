package cz.topgis.topgis_reporting.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import cz.topgis.topgis_reporting.R;
import cz.topgis.topgis_reporting.location.GPSLocation;
import cz.topgis.topgis_reporting.location.GPSLocationManager;

public class AddReportActivity extends AppCompatActivity
{
	private Spinner textViewContentType;
	private TextView textViewContentCreateTime;
	private TextView textViewContentLatitude;
	private TextView textViewContentLongitude;
	private EditText editTextDescription;

	private GPSLocationManager gpsLocationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_report);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		this.boundTextViews();
		this.setLocationManager();
		this.setContentToTextViews();
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

	private void setLocationManager()
	{
		this.gpsLocationManager = GPSLocationManager.getInstance(this);
	}

	/**
	 *
	 */
	private void setContentToTextViews()
	{
		this.setSpinner();
		this.textViewContentCreateTime.setText(new Date().toString());
		GPSLocation actualLocation = gpsLocationManager.getActualLocation();
		this.textViewContentLatitude.setText(actualLocation.getLatitude());
		this.textViewContentLongitude.setText(actualLocation.getLongitude());
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
		finish();
	}

	public void onClickImagePicker(View view)
	{
	}
}
