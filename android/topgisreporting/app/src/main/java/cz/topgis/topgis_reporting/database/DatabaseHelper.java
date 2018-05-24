package cz.topgis.topgis_reporting.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//https://developer.android.com/training/data-storage/sqlite
//https://www.tutorialspoint.com/sqlite/sqlite_data_types.htm
/**
 * Main class for managing SQLite database
 */
public class DatabaseHelper extends SQLiteOpenHelper implements DBConstants
{
	private static final int DATABASE_VERSION = 5;
	private static final String DATABASE_NAME = "topgis_reporting_client_db_beta";

	/**
	 * Basic constructor which will init database. Database is initialize by constant DATABASE_NAME
	 * @param context Activity for which DB is created
	 */
	DatabaseHelper(Context context)
	{
		super(context, DatabaseHelper.DATABASE_NAME, null, DatabaseHelper.DATABASE_VERSION);
	}

	/**
	 * Create DB scheme (tables and relations)
	 * @param db Database where the scheme will be created
	 */
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		//TODO create table report_type
		//Create table report
		db.execSQL(DBContentProvider.SQL_CREATE_TABLE_REPORT);
	}

	/**
	 * Drop database tables (including content) and call onCreate(db)
	 * @param db Database where the scheme will be altered
	 * @param oldVersion Database version number (before)
	 * @param newVersion Database version number (after)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO drop table report_type
		// Drop table report
		db.execSQL(DBContentProvider.SQL_DROP_TABLE_REPORT);
		this.onCreate(db);
	}


}
