package cz.topgis.topgis_reporting.database;

import android.provider.BaseColumns;

final class DBInfo implements BaseColumns
{
	// To prevent someone from accidentally instantiating the contract class,
	// make the constructor private.
	private DBInfo() {}

	/**
	 * Table and column names
	 */
	static final String TABLE_NAME_REPORT = "report";
	static final String COLUMN_NAME_CREATE_TIME = "create_time";
	static final String COLUMN_NAME_SEND_TIME = "send_time";
	static final String COLUMN_NAME_DESCRIPTION = "description";
	static final String COLUMN_NAME_LATITUDE = "latitude";
	static final String COLUMN_NAME_LONGITUDE = "longitude";
	static final String COLUMN_NAME_SEND = "send";

	/**
	 * SQL commands
	 */
	static final String SQL_DROP_TABLE_REPORT = "DROP TABLE IF EXISTS " + DBInfo.TABLE_NAME_REPORT;

	static final String SQL_CREATE_TABLE_REPORT = "CREATE TABLE " + DBInfo.TABLE_NAME_REPORT + " (" +
			DBInfo._ID + "INTEGER PRIMARY KEY" +
			DBInfo.COLUMN_NAME_CREATE_TIME + "TEXT" +
			DBInfo.COLUMN_NAME_SEND_TIME + "TEXT" +
			DBInfo.COLUMN_NAME_DESCRIPTION + "TEXT" +
			DBInfo.COLUMN_NAME_LATITUDE + "REAL" +
			DBInfo.COLUMN_NAME_LONGITUDE + "REAL" +
			DBInfo.COLUMN_NAME_SEND + "INTEGER" +
			")";
}
