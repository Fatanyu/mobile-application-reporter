package cz.topgis.topgis_reporting.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import cz.topgis.topgis_reporting.R;
import cz.topgis.topgis_reporting.database.DBContentProvider;
import cz.topgis.topgis_reporting.database.Report;

/**
 * Managing Report detail screen
 */
public class ReportDetailActivity extends AppCompatActivity
{
	private Long reportId;
	private Report report;

	private TextView textViewContentType;
	private TextView textViewContentCreateTime;
	private TextView textViewContentSendTime;
	private TextView textViewContentLatitude;
	private TextView textViewContentLongitude;
	private TextView textViewContentDescription;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_detail);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		// add back button
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		this.boundTextViews();
		this.findReport();
		this.setContentToTextViews();
	}

	/**
	 *
	 */
	private void setContentToTextViews()
	{
		if(this.report == null) return;
		this.textViewContentType.setText(this.report.getReportType());
		this.textViewContentCreateTime.setText(this.report.getCreateTime());
		this.textViewContentSendTime.setText(this.report.getSendTime());
		this.textViewContentLatitude.setText(this.report.getLocation().getLatitude());
		this.textViewContentLongitude.setText(this.report.getLocation().getLongitude());
		this.textViewContentDescription.setText(this.report.getDescription());
	}

	/**
	 * Connect textViews from layout to class members
	 */
	private void boundTextViews()
	{
		this.textViewContentType = findViewById(R.id.detail_text_view_content_type);
		this.textViewContentCreateTime = findViewById(R.id.detail_text_view_content_create_time);
		this.textViewContentSendTime = findViewById(R.id.detail_text_view_content_send_time);
		this.textViewContentLatitude = findViewById(R.id.detail_text_view_content_latitude);
		this.textViewContentLongitude = findViewById(R.id.detail_text_view_content_longitude);
		this.textViewContentDescription = findViewById(R.id.detail_text_view_content_description);
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
		Toast.makeText(this, "ID z DB je '" + this.reportId + "'", Toast.LENGTH_SHORT).show();
		return this.reportId.equals(defaultValue);
	}

	/**
	 * Every app start should look to db for report
	 */
	@Override
	protected void onStart()
	{
		super.onStart();
	}

	/**
	 * Find report in database
	 */
	private void findReport()
	{
		if(!this.findReportId())
		{
			DBContentProvider dbContentProvider = new DBContentProvider(this);
			this.report = dbContentProvider.getOneReport(this.reportId);
		}
		else this.report = null;
	}

	public void deleteOnClick(View view)
	{
		DBContentProvider dbContentProvider = new DBContentProvider(this);
		if(dbContentProvider.deleteOneReport(this.reportId))
		{
			Toast.makeText(this, R.string.message_report_deleted ,Toast.LENGTH_SHORT).show();
			finish();
		}
	}
}
