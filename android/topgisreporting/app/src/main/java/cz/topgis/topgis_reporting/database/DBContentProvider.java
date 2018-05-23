package cz.topgis.topgis_reporting.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import cz.topgis.topgis_reporting.location.GPSLocation;

public class DBContentProvider implements BaseColumns, DBConstants
{
	/**
	 * SQL commands
	 */
	static final String SQL_DROP_TABLE_REPORT = "DROP TABLE IF EXISTS " + TABLE_NAME_REPORT;

	static final String SQL_CREATE_TABLE_REPORT = "CREATE TABLE " + TABLE_NAME_REPORT + " (" +
			_ID + " INTEGER PRIMARY KEY," +
			COLUMN_NAME_CREATE_TIME + " TEXT," +
			COLUMN_NAME_SEND_TIME + " TEXT," +
			COLUMN_NAME_DESCRIPTION + " TEXT," +
			COLUMN_NAME_LATITUDE + " TEXT," +
			COLUMN_NAME_LONGITUDE + " TEXT," +
			COLUMN_NAME_SEND + " INTEGER," +
			COLUMN_NAME_REPORT_TYPE + " TEXT" +
			")";

	private DatabaseHelper dbHelper;

	public DBContentProvider(Context context)
	{
		this.dbHelper = new DatabaseHelper(context);
		this.insertDummyData();
	}

	private void insertDummyData()
	{
		this.insertReport(Report.getDummyReport());
	}
	protected void insertReportType(ReportType reportType)
	{

	}

	protected void insertReport(Report report)
	{
		ContentValues contentValues = report.toContentValues();
		SQLiteDatabase writableDatabase = this.dbHelper.getWritableDatabase();
		writableDatabase.insert(TABLE_NAME_REPORT, null, contentValues);
		writableDatabase.close();
	}

	private List<Report> getListFromCursor(Cursor cursor)
	{
		List<Report> reportList = new ArrayList<>();
		if(cursor != null && cursor.getCount() > 0)
		{
			while(cursor.moveToNext())
			{
				reportList.add(Report.getReportFromCursor(cursor));
			}
			cursor.close();
		}
		return reportList;
	}

	public Report getOneReport(Long dbId)
	{
		if (dbId < 0) throw new AssertionError();

		final SQLiteDatabase readableDatabase = this.dbHelper.getReadableDatabase();

		final String selection = _ID + " = ?";
		final String[] selectionArgs = {dbId.toString()};
		final Cursor cursor = readableDatabase.query(TABLE_NAME_REPORT,null, selection, selectionArgs, null, null, null);
		cursor.moveToFirst();
		Report report = Report.getReportFromCursor(cursor);
		readableDatabase.close();
		return report;
	}

	public List<Report> getAllReports()
	{
		final SQLiteDatabase readableDatabase = this.dbHelper.getReadableDatabase();
		final Cursor cursor = readableDatabase.query(TABLE_NAME_REPORT,null, null, null, null, null, null);
		List<Report> reportList = this.getListFromCursor(cursor);
		readableDatabase.close();
		return reportList;
	}

	public void onDestroy()
	{
		this.dbHelper.close();
	}
}