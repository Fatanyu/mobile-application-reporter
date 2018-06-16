package cz.topgis.topgis_reporting.database;

/**
 * Represents type of the report
 */
public final class ReportType
{
	/**
	 * Type
	 */
	private String reportType;

	/**
	 * Basic Constructor
	 * @param reportType New type
	 */
	public ReportType(String reportType)
	{
		this.reportType = reportType;
	}

	/**
	 * Simple getter
	 * @return Report type
	 */
	String getReportType()
	{
		return reportType;
	}
}