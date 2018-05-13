package cz.topgis.topgis_reporting.basics;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import cz.topgis.topgis_reporting.R;


public final class Basics extends Activity
{
	//public static final String GPS_NOT_SET = getResources().getString(R.string.gps_not_set));

	public static void giveMeToast(Context context)
	{
		//display in short period of time
		Toast.makeText(context, "Your toast message.", Toast.LENGTH_SHORT).show();
	}
}
