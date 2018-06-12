package cz.topgis.topgis_reporting;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cz.topgis.topgis_reporting.activities.AddReportActivity;
import cz.topgis.topgis_reporting.activities.ReportDetailActivity;
import cz.topgis.topgis_reporting.database.DBContentProvider;
import cz.topgis.topgis_reporting.database.Report;
import cz.topgis.topgis_reporting.database.ReportAdapter;

/**
 * Main app Activity. It also works as controller for history records.
 */
public class MainActivity extends AppCompatActivity
{

	private List<Report> reportList = new ArrayList<>();
	//https://www.sitepoint.com/mastering-complex-lists-with-the-android-recyclerview/
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

		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
		this.recyclerView.setLayoutManager(layoutManager);
		this.recyclerView.setItemAnimator(new DefaultItemAnimator());
		this.recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
		//this.prepareDummyData();
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
		getMenuInflater().inflate(R.menu.delete_all_from_db, menu);
		return true;
	}

	/**
	 * This method implements Toolbar selection
	 * @param item Identifier of toolbar button/item
	 * @return Success or Failure
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

		if (id == R.id.delete_all_from_db)
		{
			this.deleteAllReports();
			this.prepareReportAdapter(); //reset recycler view
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * This method invoke AddReport activity and starts listener
	 * @param view View caller
	 */
	public void addReportButtonOnClick(View view)
	{
		Intent intent = new Intent(this, AddReportActivity.class);
		startActivity(intent);
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

	private void prepareRealData()
	{
		DBContentProvider dbContentProvider = new DBContentProvider(this);

		this.reportList = dbContentProvider.getAllReports();
	}

	/**
	 * What happens after openning Activity
	 */
	@Override
	protected void onStart()
	{
		super.onStart();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		// Preparing reportAdapter - must be here ->
		this.prepareReportAdapter();
	}

	/**
	 * What happens after closing Activity
	 */
	@Override
	protected void onStop()
	{
		super.onStop();
	}


	@Override
	protected void onDestroy()
	{
		//Toast.makeText(this,"On Destroy", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}

	public void onClickShowReportDetail(View view)
	{
		int childLayoutPosition = this.recyclerView.getChildLayoutPosition(view); //get clicked row id
		Toast.makeText(this, "id is: " + childLayoutPosition, Toast.LENGTH_SHORT).show();
		Report report = this.reportList.get(childLayoutPosition);
		Intent intent = new Intent(this, ReportDetailActivity.class);
		intent.putExtra(DBContentProvider._ID, report.getDbId());
		startActivity(intent);
	}

	private void prepareReportAdapter()
	{
		this.prepareRealData();
		this.reportAdapter = new ReportAdapter(reportList);
		this.recyclerView.setAdapter(reportAdapter);

	}

	private void deleteAllReports()
	{
		DBContentProvider dbContentProvider = new DBContentProvider(this);
		dbContentProvider.deleteAllReports();
	}
}
