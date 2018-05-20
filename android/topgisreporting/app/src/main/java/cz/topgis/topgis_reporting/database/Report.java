package cz.topgis.topgis_reporting.database;

import java.util.Date;

import cz.topgis.topgis_reporting.location.GPSLocation;

public class Report
{
	private Date createTime;
	private Date sendTime;
	private String description;
	private GPSLocation location;
	private ReportType reportType;
	private Boolean send;
	//picture TODO

	public Report(Date createTime, Date sendTime, String description, GPSLocation location, ReportType reportType)
	{
		this.setCreateTime(createTime);
		this.setSendTime(sendTime);
		this.setDescription(description);
		this.setLocation(location);
		this.setReportType(reportType);
		this.send = true;
	}

	public Date getCreateTime()
	{
		return createTime;
	}

	public void setCreateTime(Date createTime)
	{
		this.createTime = createTime;
	}

	public Date getSendTime()
	{
		return sendTime;
	}

	public void setSendTime(Date sendTime)
	{
		this.sendTime = sendTime;
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

	public void setLocation(GPSLocation location)
	{
		this.location = location;
	}

	public ReportType getReportType()
	{
		return reportType;
	}

	public void setReportType(ReportType reportType)
	{
		this.reportType = reportType;
	}

	public String isSend()
	{
		return this.send.toString();
	}

	public static Report getDummyReport()
	{
		return new Report(new Date(), null, "lalala", GPSLocation.getDummyLocation(), new ReportType("Bordel"));
	}
}