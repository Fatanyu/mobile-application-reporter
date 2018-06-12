package cz.topgis.topgis_reporting.database;

/**
 * Represents type of the report
 */
public final class ReportType
{
	private String reportType;

	public ReportType(String reportType)
	{
		this.reportType = reportType;
	}

	String getReportType()
	{
		return reportType;
	}
}