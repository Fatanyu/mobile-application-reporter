package cz.topgis.topgis_reporting.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents api for database
 */
public class DBContentProvider implements BaseColumns, DBConstants
{
	/**
	 * SQL command for dropping table report
	 */
	static final String SQL_DROP_TABLE_REPORT = "DROP TABLE IF EXISTS " + TABLE_NAME_REPORT;

	/**
	 * SQL command for creating table report
	 */
	static final String SQL_CREATE_TABLE_REPORT = "CREATE TABLE " + TABLE_NAME_REPORT + " (" +
			_ID + " INTEGER PRIMARY KEY," +
			COLUMN_NAME_CREATE_TIME + " TEXT," +
			COLUMN_NAME_SEND_TIME + " TEXT," +
			COLUMN_NAME_DESCRIPTION + " TEXT," +
			COLUMN_NAME_LATITUDE + " TEXT," +
			COLUMN_NAME_LONGITUDE + " TEXT," +
			COLUMN_NAME_SEND + " INTEGER," +
			COLUMN_NAME_REPORT_TYPE + " TEXT," +
			COLUMN_NAME_PICTURE_PATH + " TEXT" +
			")";

	//*************************************************************************

	 private static final String ERROR_MESSAGE = "Některé obrázky se nepovedlo smazat";

	/**
	 * Member which can get writable/readable database
	 */
	private DatabaseHelper dbHelper;

	/**
	 * Basic constructor which will init DatabaseHelper and in debug insert dummy data
	 * @param context Activity for which DB is created
	 */
	public DBContentProvider(Context context)
	{
		this.dbHelper = new DatabaseHelper(context);
		//this.insertDummyData();
	}

	/**
	 * Debug method inserting dummy data to DB
	 */
	private void insertDummyData()
	{
		this.insertReport(Report.getDummyReport());
	}

	/**
	 * Inserting one reportType to database
	 * @param reportType Object which will be inserting to DB
	 */
	public void insertReportType(ReportType reportType)
	{
		//TODO
	}

	/**
	 * Inserting one report to database
	 * @param report Object which will be inserting to DB
	 */
	public void insertReport(Report report)
	{
		//DB can not work with random objects, transform inserting object to ContentValues
		ContentValues contentValues = report.toContentValues();
		SQLiteDatabase writableDatabase = this.dbHelper.getWritableDatabase();
		//there is no need for storing row ID
		writableDatabase.insert(TABLE_NAME_REPORT, null, contentValues);
		writableDatabase.close();
	}

	/**
	 * Get every report from cursor - must be done before cursor is closed
	 * @param cursor Data from database
	 * @return List of all reports from cursor
	 */
	private List<Report> getListFromCursor(Cursor cursor)
	{
		// init list
		List<Report> reportList = new ArrayList<>();

		//check if cursor exist and is not empty
		if(this.isCursorValid(cursor))
		{
			//get data and add them to list
			while(cursor.moveToNext())
			{
				reportList.add(Report.getReportFromCursor(cursor));
			}
			cursor.close(); //close cursor after everything is done
		}
		//List can be empty
		return reportList;
	}

	/**
	 * Check if cursor is null or empty
	 * @param cursor Cursor to validation
	 * @return True if cursor has data
	 */
	private boolean isCursorValid(Cursor cursor)
	{
		return cursor != null && cursor.getCount() > 0;
	}

	/**
	 * Get one report based on its database id
	 * @param dbId Database row ID
	 * @return Report from DB. Can be null
	 */
	public Report getOneReport(Long dbId)
	{
		// Based on app design, everytime you look for specific report, you will have existing ID
		// By design only on click from recyclerview will get you detail from that report
		if (dbId < 0) throw new AssertionError();

		final SQLiteDatabase readableDatabase = this.dbHelper.getReadableDatabase();

		// Where clause for selection
		final String selection = _ID + " = ?";
		// Get row _ID as string
		final String[] selectionArgs = {dbId.toString()};

		// SQL Select
		final Cursor cursor = readableDatabase.query(TABLE_NAME_REPORT,null, selection, selectionArgs, null, null, null);
		if(this.isCursorValid(cursor))
		{
			// cursor init
			cursor.moveToFirst();
			Report report = Report.getReportFromCursor(cursor);

			readableDatabase.close();
			return report;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Select all reports from database. The newest first
	 * @return List of all reports from database
	 */
	public List<Report> getAllReports()
	{
		final SQLiteDatabase readableDatabase = this.dbHelper.getReadableDatabase();
		final Cursor cursor = readableDatabase.query(TABLE_NAME_REPORT,null, null, null, null, null, _ID + " DESC");
		List<Report> reportList = this.getListFromCursor(cursor);

		readableDatabase.close();
		return reportList;
	}

	/**
	 * Delete one report from database and its image
	 * @param id Database report ID
	 * @return True when report AND picture has been successfully deleted
	 */
	public boolean deleteOneReport(Long id)
	{
		boolean pictureDeleted = this.deleteOneImage(id);

		final SQLiteDatabase readableDatabase = this.dbHelper.getWritableDatabase();
		int count = readableDatabase.delete(TABLE_NAME_REPORT, _ID + "=?", new String[]{id.toString()});
		readableDatabase.close();

		return count == 1 && pictureDeleted; // True if 1 deleted row and picture
	}

	/**
	 * Delete one image from internal storage
	 * @param id Database report ID
	 * @return True when image is deleted
	 */
	private boolean deleteOneImage(Long id)
	{
		Report report = this.getOneReport(id);
		boolean pictureDeleted = true;
		if(report.hasPicture())
		{
			File file = new File(report.getPicturePath(), report.getCreateTime() + ".jpg");
			pictureDeleted = file.delete();
		}
		return pictureDeleted;
	}

	/**
	 * Delete all images from internal store (and held by database)
	 * @return True when all images are successfully deleted
	 */
	private boolean deleteAllImages()
	{
		boolean allDeleted = true;
		//get all reports
		List<Report> allReports = this.getAllReports();
		for (Report report : allReports)
		{
			if(!this.deleteOneImage(report.getDbId())) allDeleted = false;
		}
		return allDeleted;
	}

	/**
	 * Delete all reports from database, including images from local store
	 */
	public void deleteAllReports()
	{
		//delete images
		if(!this.deleteAllImages())
		{
			Log.e("IMAGE_DELETE_FAIL", DBContentProvider.ERROR_MESSAGE);
		}

		//drop DB
		final SQLiteDatabase readableDatabase = this.dbHelper.getWritableDatabase();
		readableDatabase.delete(TABLE_NAME_REPORT, null, null);
		readableDatabase.close();
	}
}