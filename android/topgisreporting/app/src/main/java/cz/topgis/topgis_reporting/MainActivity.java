package cz.topgis.topgis_reporting;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.topgis.topgis_reporting.activities.AddReportActivity;
import cz.topgis.topgis_reporting.basics.Basics;
import cz.topgis.topgis_reporting.database.Report;
import cz.topgis.topgis_reporting.database.ReportAdapter;
import cz.topgis.topgis_reporting.location.GPSLocation;
import cz.topgis.topgis_reporting.location.GPSLocationManager;

/**
 * Main app Activity. It also works as controller for history records.
 */
public class MainActivity extends AppCompatActivity
{
	static final int REQUEST_CODE_PERMISSION_GPS = 100;

	private List<Report> reportList = new ArrayList<>();
	private RecyclerView recyclerView;
	private ReportAdapter reportAdapter;


	/**
	 * This method will be called after activity creations
	 * @param savedInstanceState Stored previous state
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);


		this.recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

		this.reportAdapter = new ReportAdapter(reportList);
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
		this.recyclerView.setLayoutManager(layoutManager);
		this.recyclerView.setItemAnimator(new DefaultItemAnimator());
		this.recyclerView.setAdapter(reportAdapter);

		this.prepareDummyData();
	}

	/**
	 * This method is called after Options button (in menu) is created
	 * @param menu Activity menu
	 * @return True on success creation
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	/**
	 * TODO - find out what exactly this method should do
	 * @param item
	 * @return
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * This method invoke AddNewRecord activity and starts listener
	 * @param view View caller
	 */
	public void addButtonOnClick(View view)
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
			ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MainActivity.REQUEST_CODE_PERMISSION_GPS);
		}
		else // Permission granted
		{
			GPSLocationManager.getInstance(this).registerListener();
			//Basics.giveMeToast(this);
			Intent intent = new Intent(this,AddReportActivity.class);
			startActivity(intent);
			//TODO - start activity AddNewRecord
		}

	}


	private void prepareDummyData()
	{
		this.reportList.add(Report.getDummyReport());
		this.reportList.add(Report.getDummyReport());
		this.reportList.add(Report.getDummyReport());
		this.reportList.add(Report.getDummyReport());
		this.reportList.add(Report.getDummyReport());
		this.reportList.add(Report.getDummyReport());
		this.reportList.add(Report.getDummyReport());
		this.reportList.add(Report.getDummyReport());
		this.reportList.add(Report.getDummyReport());
		this.reportList.add(Report.getDummyReport());
		this.reportList.add(Report.getDummyReport());
		this.reportList.add(Report.getDummyReport());
		this.reportList.add(Report.getDummyReport());
		this.reportAdapter.notifyDataSetChanged();

	}

	/**
	 * What happens after openning Activity
	 */
	@Override
	protected void onStart()
	{
		super.onStart();
	}

	/**
	 * What happens after closing Activity
	 */
	@Override
	protected void onStop()
	{
		super.onStop();
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
			case MainActivity.REQUEST_CODE_PERMISSION_GPS:
			{
				if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
					this.addButtonOnClick(null);
				else //Do not have permission
					Toast.makeText(this,"Really need that permission", Toast.LENGTH_SHORT).show(); //TODO
			}
		}
	}

	@Override
	protected void onDestroy()
	{
		//Toast.makeText(this,"On Destroy", Toast.LENGTH_SHORT).show();
		GPSLocationManager.getInstance(this).unregisterListener(); //Off GPS tracking
		super.onDestroy();
	}
}
