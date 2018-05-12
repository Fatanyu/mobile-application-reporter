package database;

import android.provider.BaseColumns;

final class DBInfo
{
	// To prevent someone from accidentally instantiating the contract class,
	// make the constructor private.
	private DBInfo() {}
	static final String TABLE_NAME = "entry";
	static final String COLUMN_NAME_TITLE = "title";
	static final String COLUMN_NAME_SUBTITLE = "subtitle";

}
