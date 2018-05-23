package cz.topgis.topgis_reporting.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Objects;

import cz.topgis.topgis_reporting.R;
import cz.topgis.topgis_reporting.database.DBContentProvider;
import cz.topgis.topgis_reporting.database.Report;

public class ReportDetailActivity extends AppCompatActivity
{
	private Long reportId;
	private Report report;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.findReport();
		setContentView(R.layout.activity_report_detail);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		// add back button
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * Method for managing toolbar menu
	 * @param item Selected item
	 * @return True on success selected
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
	 * Find id from intent (report id from row in MainActivity)
	 * @return True if id number is not -1 (default missing value)
	 */
	private boolean findReportId()
	{
		Intent intent = getIntent();
		Long defaultValue = Long.valueOf(-1);
		this.reportId = intent.getLongExtra(DBContentProvider._ID, defaultValue);
		Toast.makeText(this, "ID z DB je '" + this.reportId + "", Toast.LENGTH_SHORT).show();
		return this.reportId.equals(defaultValue);
	}

	/**
	 * Every app start should look to db for report
	 */
	@Override
	protected void onStart()
	{
		super.onStart();
		if(this.findReportId())
		{
			this.findReport();
		}
		else
		{
			Toast.makeText(this, getResources().getString(R.string.report_missing), Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Find report in database
	 */
	private void findReport()
	{
		if(this.findReportId())
		{
			DBContentProvider dbContentProvider = new DBContentProvider(this);
			this.report = dbContentProvider.getOneReport(this.reportId);
		}
		else this.report = null;
	}
}
