package cz.topgis.topgis_reporting.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//https://developer.android.com/training/data-storage/sqlite
//https://www.tutorialspoint.com/sqlite/sqlite_data_types.htm
/**
 *
 */
public class DBHelperWrapper extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "topgis_reporting_client_db";

	public DBHelperWrapper(Context context)
	{
		super(context, DBHelperWrapper.DATABASE_NAME, null, DBHelperWrapper.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(DBInfo.SQL_CREATE_TABLE_REPORT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL(DBInfo.SQL_DROP_TABLE_REPORT);
		this.onCreate(db);
	}

	public boolean insertReport(Report report)
	{
		return true;
	}
}
