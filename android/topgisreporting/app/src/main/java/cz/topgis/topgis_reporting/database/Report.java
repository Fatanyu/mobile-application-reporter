package cz.topgis.topgis_reporting.database;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Date;

import cz.topgis.topgis_reporting.location.GPSLocation;

/**
 * Represents one record from user
 */
public class Report
{
	/**
	 * ID from DB - is empty during user creation
	 */
	private Long dbId;

	/**
	 * Time of creation
	 */
	private String createTime;

	/**
	 * Time of sending to server
	 */
	private String sendTime;

	/**
	 * Description of the report
	 */
	private String description;

	/**
	 * GPS location without elevation
	 */
	private GPSLocation location;

	/**
	 * What kind of report is this
	 */
	private ReportType reportType;

	/**
	 * Already send (true/false) - it can be deleted, BUT it is easier/faster to work with it
	 */
	private Boolean send;

	/**
	 * Path to image in the local store
	 */
	private String picturePath;

	/**
	 * Basic constructor for USER creation
	 * TODO probably dummy - createtime should be actual time and send time is always null at this moment
	 * @param createTime Time of creation
	 * @param sendTime Always null now
	 * @param description Definition of the problem
	 * @param location GPS location of the problem
	 * @param reportType Type of the report
	 */
	public Report(Date createTime, Date sendTime, String description, GPSLocation location, ReportType reportType)
	{
		this.setDbId(Long.valueOf(0)); // Simple 0 gives error
		this.setCreateTime(createTime);
		this.setSendTime(sendTime);
		this.setDescription(description);
		this.setLocation(location);
		this.setReportType(reportType);
		this.setPicturePath("");
	}

	/**
	 * Constructor for creation from database cursor
	 * @param dbId Database row ID
	 * @param createTime Time of report creation
	 * @param sendTime Time of sending, can be null
	 * @param description Definition of the problem
	 * @param location GPS location of the problem
	 * @param reportType type of the report
	 */
	private Report(Long dbId, String createTime, String sendTime, String description, GPSLocation location, ReportType reportType, String picturePath)
	{
		this.setDbId(dbId);
		this.setCreateTime(createTime);
		this.setSendTime(sendTime);
		this.setDescription(description);
		this.setLocation(location);
		this.setReportType(reportType);
		this.setPicturePath(picturePath);
	}


	/**
	 * Setter for databaseID. Must have Long.valueOf()
	 * Database counts from 1
	 * @param dbId ID
	 */
	private void setDbId(Long dbId)
	{
		if (dbId > 0) this.dbId = dbId;
		else this.dbId = Long.valueOf(0);
	}

	/**
	 * Simple getter
	 * @return Time of creation
	 */
	public String getCreateTime()
	{
		return createTime;
	}

	/**
	 * Simple setter which calls its overloaded method
	 * @param createTime Time of creation
	 */
	private void setCreateTime(Date createTime)
	{
		//TODO localized format
		this.setCreateTime(createTime.toString());
	}

	/**
	 * Simple setter
	 * @param createTime Time of creation
	 */
	private void setCreateTime(String createTime)
	{
		//TODO localized format
		this.createTime = createTime;
	}

	/**
	 * Simple getter
	 * @return Time of sending
	 */
	public String getSendTime()
	{
		return sendTime;
	}

	/**
	 * Setter which will also set this.send
	 * @param sendTime Time of sending
	 */
	public void setSendTime(String sendTime)
	{
		this.send = sendTime != null;
		if(this.send) this.sendTime = sendTime;
	}

	/**
	 * Setter which will also set this.send. It also calls its overloaded self
	 * @param sendTime Time of sending
	 */
	public void setSendTime(Date sendTime)
	{
		this.send = sendTime != null;
		if(this.send) this.sendTime = sendTime.toString();
	}

	/**
	 * Simple getter
	 * @return Description of the problem
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Simple setter
	 * @param description Description of the problem
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Simple getter
	 * @return GPS location without elevation
	 */
	public GPSLocation getLocation()
	{
		return location;
	}

	/**
	 * Simple setter
	 * @param location GPS location without elevation
	 */
	private void setLocation(GPSLocation location)
	{
		this.location = location;
	}

	/**
	 * Simple getter
	 * @return Type of the report as String
	 */
	public String getReportType()
	{
		return reportType.getReportType();
	}

	/**
	 * Simple setter
	 * @param reportType Type of the report
	 */
	private void setReportType(ReportType reportType)
	{
		this.reportType = reportType;
	}

	/**
	 * Return String which explain if the report has been send
	 * @return True/false
	 */
	public Boolean isSend()
	{
		return this.send;
	}

	/**
	 * Simple getter
	 * @return Database ID, zero when it's not in database
	 */
	public Long getDbId()
	{
		return dbId;
	}

	/**
	 * Static Report Builder (dummy class from scratch)
	 * @return Dummy report instance
	 */
	public static Report getDummyReport()
	{
		return new Report(new Date(), null, "lalala", GPSLocation.getDummyLocation(), new ReportType("Bordel"));
	}

	/**
	 * Static Report Builder (from database cursor)
	 * @param cursor Database connection which holds one report
	 * @return Report instance which creation is based on cursor
	 */
	public static Report getReportFromCursor(Cursor cursor)
	{
		return new Report(
				cursor.getLong(cursor.getColumnIndex(DBContentProvider._ID)),
				cursor.getString(cursor.getColumnIndex(DBContentProvider.COLUMN_NAME_CREATE_TIME)),
				cursor.getString(cursor.getColumnIndex(DBContentProvider.COLUMN_NAME_SEND_TIME)),
				cursor.getString(cursor.getColumnIndex(DBContentProvider.COLUMN_NAME_DESCRIPTION)),
				new GPSLocation(cursor.getString(cursor.getColumnIndex(DBContentProvider.COLUMN_NAME_LATITUDE)),
						cursor.getString(cursor.getColumnIndex(DBContentProvider.COLUMN_NAME_LONGITUDE))
				),
				new ReportType(cursor.getString(cursor.getColumnIndex(DBContentProvider.COLUMN_NAME_REPORT_TYPE))),
				cursor.getString(cursor.getColumnIndex(DBContentProvider.COLUMN_NAME_PICTURE_PATH))
		);
	}

	/**
	 * Creates ContentValues from this instance. Id does not add database ID
	 * @return ContentValues for DB operations
	 */
	public ContentValues toContentValues()
	{
		ContentValues contentValues = new ContentValues();
//TODO
		contentValues.put(DBConstants.COLUMN_NAME_CREATE_TIME, this.createTime);

		if(this.send) contentValues.put(DBConstants.COLUMN_NAME_SEND_TIME, this.sendTime);
		else contentValues.putNull(DBConstants.COLUMN_NAME_SEND_TIME);

		contentValues.put(DBConstants.COLUMN_NAME_SEND, this.send);
		contentValues.put(DBConstants.COLUMN_NAME_DESCRIPTION, this.description);
		contentValues.put(DBConstants.COLUMN_NAME_LATITUDE, this.location.getLatitude());
		contentValues.put(DBConstants.COLUMN_NAME_LONGITUDE, this.location.getLongitude());
		contentValues.put(DBConstants.COLUMN_NAME_REPORT_TYPE, this.reportType.getReportType());
		contentValues.put(DBConstants.COLUMN_NAME_PICTURE_PATH, this.picturePath);

		return contentValues;
	}

	/**
	 * Simple getter
	 * @return Path to local store with image
	 */
	public String getPicturePath()
	{
		return picturePath;
	}

	/**
	 * Simple setter
	 * @param picturePath New path
	 */
	public void setPicturePath(String picturePath)
	{
		this.picturePath = picturePath;
	}

	/**
	 * Check if picture exists
	 * @return True if image exists
	 */
	public boolean hasPicture()
	{
		return !this.picturePath.equals("");
	}
}