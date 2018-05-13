package cz.topgis.topgis_reporting.database;

/**
 * Represents type of the report
 */
final class ReportType
{
	private String reportType;

	public ReportType(String reportType)
	{
		this.reportType = reportType;
	}

	public String getReportType()
	{
		return reportType;
	}
}