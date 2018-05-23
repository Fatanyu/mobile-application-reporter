package cz.topgis.topgis_reporting.database;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Date;

import cz.topgis.topgis_reporting.location.GPSLocation;

public class Report
{
	public static final String REPORT_IDENTIFIER = "reportId";
	private Long dbId;
	private String createTime;
	private String sendTime;
	private String description;
	private GPSLocation location;
	private ReportType reportType;
	private Boolean send;
	//picture TODO

	public Report(Date createTime, Date sendTime, String description, GPSLocation location, ReportType reportType)
	{
		this.setDbId(Long.valueOf(0));
		this.setCreateTime(createTime);
		this.setSendTime(sendTime);
		this.setDescription(description);
		this.setLocation(location);
		this.setReportType(reportType);
	}

	public Report(Long dbId, String createTime, String sendTime, String description, GPSLocation location, ReportType reportType)
	{
		this.setDbId(dbId);
		this.setCreateTime(createTime);
		this.setSendTime(sendTime);
		this.setDescription(description);
		this.setLocation(location);
		this.setReportType(reportType);
	}

	private void setDbId(Long dbId)
	{
		if (dbId > 0) this.dbId = dbId;
		else this.dbId = Long.valueOf(0);
	}

	public String getCreateTime()
	{
		return createTime;
	}

	private void setCreateTime(Date createTime)
	{
		this.setCreateTime(createTime.toString());
	}
	private void setCreateTime(String createTime)
	{
		this.createTime = createTime;
	}

	public String getSendTime()
	{
		return sendTime;
	}

	public void setSendTime(String sendTime)
	{
		this.send = sendTime != null;
		if(this.send) this.sendTime = sendTime;
	}

	public void setSendTime(Date sendTime)
	{
		this.send = sendTime != null;
		if(this.send) this.sendTime = sendTime.toString();
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public GPSLocation getLocation()
	{
		return location;
	}

	private void setLocation(GPSLocation location)
	{
		this.location = location;
	}

	public ReportType getReportType()
	{
		return reportType;
	}

	private void setReportType(ReportType reportType)
	{
		this.reportType = reportType;
	}

	public String isSend()
	{
		return this.send.toString();
	}

	public Long getDbId()
	{
		return dbId;
	}

	public static Report getDummyReport()
	{
		return new Report(new Date(), null, "lalala", GPSLocation.getDummyLocation(), new ReportType("Bordel"));
	}

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
				new ReportType(cursor.getString(cursor.getColumnIndex(DBContentProvider.COLUMN_NAME_REPORT_TYPE)))
		);
	}

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

		return contentValues;
	}
}