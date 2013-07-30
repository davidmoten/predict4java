package uk.me.g4dpz.satellite;

/**
 * Immutable class created to avoid returning ugly 2d arrays of lat long points
 * from api methods.
 * 
 * @author Dave Moten
 * 
 */
public class Position {

	private final double lat;
	private final double lon;

	public Position(double lat, double lon) {
		super();
		this.lat = lat;
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	@Override
	public String toString() {
		return "Position [lat=" + lat + ", lon=" + lon + "]";
	}

}
