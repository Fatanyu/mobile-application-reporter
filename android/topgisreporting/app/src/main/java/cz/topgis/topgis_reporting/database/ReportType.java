package cz.topgis.topgis_reporting.database;

/**
 * Represents type of the report
 */
final class ReportType
{
	//TODO database ID
	private String reportType;

	ReportType(String reportType)
	{
		this.reportType = reportType;
	}

	String getReportType()
	{
		return reportType;
	}
}