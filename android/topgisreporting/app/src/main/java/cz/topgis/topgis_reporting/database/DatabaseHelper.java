package cz.topgis.topgis_reporting.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//https://developer.android.com/training/data-storage/sqlite
//https://www.tutorialspoint.com/sqlite/sqlite_data_types.htm
/**
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper implements DBConstants
{
	private static final int DATABASE_VERSION = 5;
	private static final String DATABASE_NAME = "topgis_reporting_client_db_beta";

	public DatabaseHelper(Context context)
	{
		super(context, DatabaseHelper.DATABASE_NAME, null, DatabaseHelper.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(DBContentProvider.SQL_CREATE_TABLE_REPORT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL(DBContentProvider.SQL_DROP_TABLE_REPORT);
		this.onCreate(db);
	}


}
