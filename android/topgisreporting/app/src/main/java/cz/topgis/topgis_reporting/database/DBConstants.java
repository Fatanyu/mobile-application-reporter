package cz.topgis.topgis_reporting.database;

interface DBConstants
{
	/**
	 * Table 'report' and its column names
	 */
	static final String TABLE_NAME_REPORT = "report";
	static final String COLUMN_NAME_CREATE_TIME = "create_time";
	static final String COLUMN_NAME_SEND_TIME = "send_time";
	static final String COLUMN_NAME_DESCRIPTION = "description";
	static final String COLUMN_NAME_LATITUDE = "latitude";
	static final String COLUMN_NAME_LONGITUDE = "longitude";
	static final String COLUMN_NAME_SEND = "send";
	static final String COLUMN_NAME_REPORT_TYPE = "report_type";
	static final String COLUMN_NAME_PICTURE_PATH = "picture_path";


	/**
	 * Table 'report_type' and its column names
	 */
	static final String TABLE_NAME_REPORT_TYPE = "report_type";
	static final String COLUMN_NAME_TYPE_NAME = "type_name";
}
