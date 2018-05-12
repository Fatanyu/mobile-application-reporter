package cz.topgis.topgis_reporting.basics;

import android.content.Context;
import android.widget.Toast;


public final class Basics
{


	static public void giveMeToast(Context context)
	{
		//display in short period of time
		Toast.makeText(context, "Your toast message.", Toast.LENGTH_SHORT).show();

		/*
		//display in long period of time
		Toast.makeText(getApplicationContext(), "Your toast message",
				Toast.LENGTH_LONG).show();*/
	}
}
