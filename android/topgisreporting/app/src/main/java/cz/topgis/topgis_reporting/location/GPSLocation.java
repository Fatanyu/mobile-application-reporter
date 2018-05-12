package cz.topgis.topgis_reporting.location;

import java.util.Objects;

/**
 * Represents coordinates on Earth as GPS longitude and latitude
 */
public final class GPSLocation
{
	/**
	 * Latitude in GPS format as text
	 */
	private final String latitude;
	/**
	 * Longitude in GPS format as text
	 */
	private final String longitude;

	/**
	 * Constant which represents unknown location
	 */
	static private final String GPS_NOT_SET = "Unknown location";

	/**
	 * Default constructor
	 * @param latitude Latitude GPS position as String
	 * @param longitude Longitude GPS position as String
	 */
	private GPSLocation(String latitude, String longitude)
	{
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * Default constructor
	 * @param latitude Latitude GPS position as Double
	 * @param longitude Longitude GPS position as Double
	 */
	GPSLocation(Double latitude, Double longitude)
	{
		this.latitude = latitude.toString();
		this.longitude = longitude.toString();
	}

	/**
	 * Simple getter
	 * @return latitude
	 */
	public String getLatitude()
	{
		return latitude;
	}

	/**
	 * Simple getter
	 * @return longitude
	 */
	public String getLongitude()
	{
		return longitude;
	}

	/**
	 * Generated equal. Two dummy objects are equal.
	 * @param o Object to compare.
	 * @return True if objects have same GPS coordinates or both are dummy.
	 */
	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GPSLocation that = (GPSLocation) o;
		return Objects.equals(latitude, that.latitude) &&
				Objects.equals(longitude, that.longitude);
	}

	/**
	 * Check if object is dummy GPS version
	 * @return True if this object is dummy.
	 */
	public boolean isDummy()
	{
		return this.equals(GPSLocation.getDummyLocation());
	}

	/**
	 * Create dummy instance of this class
	 * @return Dummy GPS location
	 */
	public static GPSLocation getDummyLocation()
	{
		return new GPSLocation(GPSLocation.GPS_NOT_SET, GPSLocation.GPS_NOT_SET);
	}
}
