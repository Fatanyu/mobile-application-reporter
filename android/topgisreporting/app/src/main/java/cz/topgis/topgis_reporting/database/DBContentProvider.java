package cz.topgis.topgis_reporting.database;

import android.content.ContentValues;
import android.content.Context;
import android.provider.BaseColumns;

abstract class DBContentProvider implements BaseColumns, DBConstants
{
	/**
	 * SQL commands
	 */
	static final String SQL_DROP_TABLE_REPORT = "DROP TABLE IF EXISTS " + TABLE_NAME_REPORT;

	static final String SQL_CREATE_TABLE_REPORT = "CREATE TABLE " + TABLE_NAME_REPORT + " (" +
			_ID + "INTEGER PRIMARY KEY," +
			COLUMN_NAME_CREATE_TIME + "TEXT," +
			COLUMN_NAME_SEND_TIME + "TEXT," +
			COLUMN_NAME_DESCRIPTION + "TEXT," +
			COLUMN_NAME_LATITUDE + "TEXT," +
			COLUMN_NAME_LONGITUDE + "TEXT," +
			COLUMN_NAME_SEND + "INTEGER," +
			COLUMN_REPORT_TYPE + "TEXT" +
			")";

	protected DatabaseHelper dbHelper;

	public DBContentProvider(Context context)
	{
		this.dbHelper = new DatabaseHelper(context);
	}

	public void insertReportType(ReportType reportType)
	{

	}

	public void insertReport(Report report)
	{
		ContentValues contentValues = report.toContentValues();

		//SQLiteDatabase db = this.getWritableDatabase();
	}
}