package cz.topgis.topgis_reporting;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import cz.topgis.topgis_reporting.basics.Basics;
import cz.topgis.topgis_reporting.location.GPSLocationManager;

public class MainActivity extends AppCompatActivity
{
	static final int PERMISSION_GPS = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

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
			ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MainActivity.PERMISSION_GPS);
		}
		else
		{
			GPSLocationManager.getInstance(this).registerListener(this);
			Basics.giveMeToast(this);
		}

	}

	@Override
	protected void onStart()
	{
		super.onStart();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		GPSLocationManager.getInstance(this).unregisterListener();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch(requestCode)
		{
			case MainActivity.PERMISSION_GPS:
			{
				if (grantResults.length > 0	&& grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					GPSLocationManager.getInstance(this).registerListener(this);
				}
				else
				{
					Toast.makeText(this,"Really need that permission", Toast.LENGTH_SHORT).show();
				}
			}
		}

	}
}
