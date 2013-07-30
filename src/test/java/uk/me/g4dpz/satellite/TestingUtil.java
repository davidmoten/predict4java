package uk.me.g4dpz.satellite;

public class TestingUtil {

	public static boolean eq(Position position, double lat, double lon,
			double precision) {
		return Math.abs(position.getLat() - lat) <= precision
				&& Math.abs(position.getLon() - lon) <= precision;
	}
}
