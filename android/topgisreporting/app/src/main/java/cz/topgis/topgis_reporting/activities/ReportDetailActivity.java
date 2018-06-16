package cz.topgis.topgis_reporting.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;

import cz.topgis.topgis_reporting.R;
import cz.topgis.topgis_reporting.database.DBContentProvider;
import cz.topgis.topgis_reporting.database.Report;

/**
 * Controller for report detail screen
 */
public class ReportDetailActivity extends AppCompatActivity
{
	/**
	 * Id from database, which is send by clicked row
	 */
	private Long reportId;

	/**
	 * Report which will be shown
	 */
	private Report report;

	/**
	 * TextView which is connected to layout
	 */
	private TextView textViewContentType;

	/**
	 * TextView which is connected to layout
	 */
	private TextView textViewContentCreateTime;

	/**
	 * TextView which is connected to layout
	 */
	private TextView textViewContentSendTime;

	/**
	 * TextView which is connected to layout
	 */
	private TextView textViewContentLatitude;

	/**
	 * TextView which is connected to layout
	 */
	private TextView textViewContentLongitude;

	/**
	 * TextView which is connected to layout
	 */
	private TextView textViewContentDescription;

	/**
	 * ImageView which is connected to layout
	 */
	private ImageView imageView;

	/**
	 * Initialize screen
	 * @param savedInstanceState Is not used
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		//setting layout
		setContentView(R.layout.activity_report_detail);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		// add back button
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

		//bound views to layout
		this.boundTextViews();

		//find report in DB
		this.findReport();

		//set content
		this.setContentToTextViews();
	}

	/**
	 * Setter which sets everything at once to layout from this.report
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

		this.initImageView();
	}

	/**
	 * Initializer for ImageView
	 */
	private void initImageView()
	{
		Bitmap imageFromInternalStorage = this.getImageFromInternalStorage();
		//check if there is any image
		if(imageFromInternalStorage != null)
		{
			//show imageView and sets image
			this.imageView.setVisibility(ImageView.VISIBLE);
			this.imageView.setImageBitmap(imageFromInternalStorage);
			findViewById(R.id.detail_text_view_label_picture).setVisibility(TextView.VISIBLE);
		}
	}

	/**
	 * Getter which gets image from internal storage
	 * @return Image or null, if non exists
	 */
	private Bitmap getImageFromInternalStorage()
	{
		Bitmap image = null;
		if(report.hasPicture())
		{
			try
			{
				//get picture and convert it to bitmap
				File file = new File(report.getPicturePath(), report.getCreateTime() + ".jpg");
				image = BitmapFactory.decodeStream(new FileInputStream(file));
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}

		return image;
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
		this.imageView = findViewById(R.id.detail_image_view);
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
		//Toast.makeText(this, "ID z DB je '" + this.reportId + "'", Toast.LENGTH_SHORT).show();
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

	/**
	 * Deletes this report and close screen
	 * @param view Clicked item
	 */
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
