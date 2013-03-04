/**
    predict4java: An SDP4 / SGP4 library for satellite orbit predictions

    Copyright (C)  2004-2010  David A. B. Johnson, G4DPZ.

    This class is a Java port of one of the core elements of
    the Predict program, Copyright John A. Magliacane,
    KD2BD 1991-2003: http://www.qsl.net/kd2bd/predict.html

    Dr. T.S. Kelso is the author of the SGP4/SDP4 orbital models,
    originally written in Fortran and Pascal, and released into the
    public domain through his website (http://www.celestrak.com/). 
    Neoklis Kyriazis, 5B4AZ, later re-wrote Dr. Kelso's code in C,
    and released it under the GNU GPL in 2002.
    PREDICT's core is based on 5B4AZ's code translation efforts.

    Author: David A. B. Johnson, G4DPZ <dave@g4dpz.me.uk>

    Comments, questions and bugreports should be submitted via
    http://sourceforge.net/projects/websat/
    More details can be found at the project home page:

    http://websat.sourceforge.net

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, visit http://www.fsf.org/
 */
package uk.me.g4dpz.satellite;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Not thread safe!
 */
public abstract class AbstractSatellite implements Satellite {

	private static final double MINS_PER_DAY = 1.44E3;
	private static final double PI_OVER_TWO = Math.PI / 2.0;
	private static final double SECS_PER_DAY = 8.6400E4;
	private static final double FLATTENING_FACTOR = 3.35281066474748E-3;

	protected static final double CK4 = 6.209887E-7;
	protected static final double EARTH_GRAVITATIONAL_CONSTANT = 3.986008E5;
	protected static final double S = 1.012229;
	protected static final double QOMS2T = 1.880279E-09;
	protected static final double EARTH_ROTATIONS_PER_SIDERIAL_DAY = 1.00273790934;
	protected static final double EARTH_ROTATIONS_RADIANS_PER_SIDERIAL_DAY = EARTH_ROTATIONS_PER_SIDERIAL_DAY
			* TWO_PI;
	protected static final double RHO = 1.5696615E-1;
	protected static final double MFACTOR = 7.292115E-5;
	protected static final double SOLAR_RADIUS_KM = 6.96000E5;
	protected static final double ASTRONOMICAL_UNIT = 1.49597870691E8;

	protected static final double SPEED_OF_LIGHT = 2.99792458E8;

	protected static final double PERIGEE_156_KM = 156.0;

	/* WGS 84 Earth radius km */
	protected static final double EARTH_RADIUS = 6.378137E3;

	/* Solar radius - km (IAU 76) */
	protected static final double SOLAR_RADIUS = 6.96000E5;

	private double s4;
	private double qoms24;
	private double perigee;

	private final TLE tle;

	private double eclipseDepth;

	/**
	 * Position vector of the satellite. Used to store the position for later
	 * calculations.
	 */
	private final Vector4 position = new Vector4();
	/**
	 * Velocity vector of the satellite. Used to store the velocity for later
	 * calculations.
	 */
	private final Vector4 velocity = new Vector4();
	/** Date/time at which the position and velocity were calculated */
	private double julUTC;
	/** Satellite position. Used to store the SatPos for later calculations. */
	private SatPos satPos;

	/** The time at which we do all the calculations. */
	static final TimeZone TZ = TimeZone.getTimeZone("UTC:UTC");

	private final double julEpoch;

	public AbstractSatellite(final TLE tle) {
		this.tle = tle;
		julEpoch = AbstractSatellite.juliandDateOfEpoch(tle.getEpoch());
	}

	@Override
	public final synchronized TLE getTLE() {
		return tle;
	}

	/**
	 * Calculates the Julian Day of the Year.
	 * 
	 * The function Julian_Date_of_Year calculates the Julian Date of Day 0.0 of
	 * {year}. This function is used to calculate the Julian Date of any date by
	 * using Julian_Date_of_Year, DOY, and Fraction_of_Day.
	 * 
	 * Astronomical Formulae for Calculators, Jean Meeus, pages 23-25. Calculate
	 * Julian Date of 0.0 Jan aYear
	 * 
	 * @param theYear
	 *            the year
	 * @return the Julian day number
	 */
	protected static double julianDateOfYear(final double theYear) {

		final double aYear = theYear - 1;
		long i = (long) Math.floor(aYear / 100);
		final long a = i;
		i = a / 4;
		final long b = 2 - a + i;
		i = (long) Math.floor(365.25 * aYear);
		i += 30.6001 * 14;

		return i + 1720994.5 + b;
	}

	/**
	 * The function Julian_Date_of_Epoch returns the Julian Date of an epoch
	 * specified in the format used in the NORAD two-line element sets. It has
	 * been modified to support dates beyond the year 1999 assuming that
	 * two-digit years in the range 00-56 correspond to 2000-2056. Until the
	 * two-line element set format is changed, it is only valid for dates
	 * through 2056 December 31.
	 * 
	 * @param epoch
	 *            the Epoch
	 * @return The Julian date of the Epoch
	 */
	static double juliandDateOfEpoch(final double epoch) {

		/* Modification to support Y2K */
		/* Valid 1957 through 2056 */
		double year = Math.floor(epoch * 1E-3);
		final double day = (epoch * 1E-3 - year) * 1000.0;

		if (year < 57) {
			year = year + 2000;
		} else {
			year = year + 1900;
		}

		return AbstractSatellite.julianDateOfYear(year) + day;
	}

	/**
	 * Read the system clock and return the number of days since 31Dec79
	 * 00:00:00 UTC (daynum 0).
	 * 
	 * @param date
	 *            the date we wan to get the offset for
	 * @return the number of days offset
	 */
	private static double calcCurrentDaynum(final Date date) {

		final long now = date.getTime();

		final Calendar sgp4Epoch = Calendar.getInstance(TZ);
		sgp4Epoch.clear();
		sgp4Epoch.set(1979, 11, 31, 0, 0, 0);
		final long then = sgp4Epoch.getTimeInMillis();
		final long millis = now - then;
		return millis / 1000.0 / 60.0 / 60.0 / 24.0;
	}

	/**
	 * Returns the square of a double.
	 * 
	 * @param arg
	 *            the value for which to get the double
	 * @return the arg squared
	 */
	protected static double sqr(final double arg) {

		return arg * arg;
	}

	/**
	 * Calculates scalar magnitude of a vector4 argument.
	 * 
	 * @param v
	 *            the vector were measuring
	 * 
	 */
	protected static void magnitude(final Vector4 v) {
		v.setW(Math.sqrt(AbstractSatellite.sqr(v.getX())
				+ AbstractSatellite.sqr(v.getY())
				+ AbstractSatellite.sqr(v.getZ())));
	}

	/**
	 * Multiplies the vector v1 by the scalar k.
	 * 
	 * @param k
	 *            the multiplier
	 * @param v
	 *            the vector
	 */
	private static void scaleVector(final double k, final Vector4 v) {
		v.multiply(k);
		AbstractSatellite.magnitude(v);
	}

	/**
	 * Gets the modulus of a double value.
	 * 
	 * @param arg1
	 *            the value to be tested
	 * @param arg2
	 *            the divisor
	 * @return the remainder
	 */
	static double modulus(final double arg1, final double arg2) {
		/* Returns arg1 mod arg2 */

		double returnValue = arg1;

		final int i = (int) Math.floor(returnValue / arg2);
		returnValue -= i * arg2;

		if (returnValue < 0.0) {
			returnValue += arg2;
		}

		return returnValue;
	}

	private static double frac(final double arg) {
		/* Returns fractional part of double argument */
		return arg - Math.floor(arg);
	}

	private static double thetaGJD(final double theJD) {
		/* Reference: The 1992 Astronomical Almanac, page B6. */

		final double ut = AbstractSatellite.frac(theJD + 0.5);
		final double aJD = theJD - ut;
		final double tu = (aJD - 2451545.0) / 36525.0;
		double gmst = 24110.54841 + tu
				* (8640184.812866 + tu * (0.093104 - tu * 6.2E-6));
		gmst = AbstractSatellite.modulus(gmst + SECS_PER_DAY
				* EARTH_ROTATIONS_PER_SIDERIAL_DAY * ut, SECS_PER_DAY);

		return TWO_PI * gmst / SECS_PER_DAY;
	}

	/**
	 * Calculates the dot product of two vectors.
	 * 
	 * @param v1
	 *            vector 1
	 * @param v2
	 *            vector 2
	 * @return the dot product
	 */
	private static double dot(final Vector4 v1, final Vector4 v2) {
		return v1.getX() * v2.getX() + v1.getY() * v2.getY() + v1.getZ()
				* v2.getZ();
	}

	/**
	 * Calculates the modulus of 2 * PI.
	 * 
	 * @param testValue
	 *            the value under test
	 * @return the modulus
	 */
	protected static double mod2PI(final double testValue) {
		/* Returns mod 2PI of argument */

		double retVal = testValue;
		final int i = (int) (retVal / TWO_PI);
		retVal -= i * TWO_PI;

		if (retVal < 0.0) {
			retVal += TWO_PI;
		}

		return retVal;
	}

	/**
	 * Calculate the geodetic position of an object given its ECI position pos
	 * and time. It is intended to be used to determine the ground track of a
	 * satellite. The calculations assume the earth to be an oblate spheroid as
	 * defined in WGS '72.
	 * 
	 * Reference: The 1992 Astronomical Almanac, page K12.
	 * 
	 * @param time
	 *            the time
	 */
	private void calculateLatLonAlt(final double time) {

		calculateLatLonAlt(time, satPos, position);
	}

	private void calculateLatLonAlt(final double time, SatPos satPos,
			Vector4 position) {
		satPos.setTheta(Math.atan2(position.getY(), position.getX()));
		satPos.setLongitude(AbstractSatellite.mod2PI(satPos.getTheta()
				- AbstractSatellite.thetaGJD(time)));
		final double r = Math.sqrt(AbstractSatellite.sqr(position.getX())
				+ AbstractSatellite.sqr(position.getY()));
		final double e2 = FLATTENING_FACTOR * (2.0 - FLATTENING_FACTOR);
		satPos.setLatitude(Math.atan2(position.getZ(), r));

		double phi;
		double c;
		int i = 0;
		boolean converged = false;

		do {
			phi = satPos.getLatitude();
			c = AbstractSatellite.invert(Math.sqrt(1.0 - e2
					* AbstractSatellite.sqr(Math.sin(phi))));
			satPos.setLatitude(Math.atan2(position.getZ() + EARTH_RADIUS_KM * c
					* e2 * Math.sin(phi), r));

			converged = Math.abs(satPos.getLatitude() - phi) < EPSILON;

		} while (i++ < 10 && !converged);

		satPos.setAltitude(r / Math.cos(satPos.getLatitude()) - EARTH_RADIUS_KM
				* c);

		double temp = satPos.getLatitude();

		if (temp > PI_OVER_TWO) {
			temp -= TWO_PI;
			satPos.setLatitude(temp);
		}
	}

	/**
	 * Converts the satellite'S position and velocity vectors from normalized
	 * values to km and km/sec.
	 * 
	 * @param pos
	 *            the position
	 * @param vel
	 *            the velocity
	 */
	private static void convertSatState(final Vector4 pos, final Vector4 vel) {
		/* Converts the satellite'S position and velocity */
		/* vectors from normalized values to km and km/sec */
		AbstractSatellite.scaleVector(EARTH_RADIUS_KM, pos);
		AbstractSatellite.scaleVector(EARTH_RADIUS_KM * MINS_PER_DAY
				/ SECS_PER_DAY, vel);
	}

	@Override
	public synchronized SatPos getPosition(final GroundStationPosition gsPos,
			final Date date) {

		/* This is the stuff we need to do repetitively while tracking. */
		satPos = new SatPos();

		julUTC = AbstractSatellite.calcCurrentDaynum(date) + 2444238.5;

		/* Convert satellite'S epoch time to Julian */
		/* and calculate time since epoch in minutes */

		final double tsince = (julUTC - julEpoch) * MINS_PER_DAY;

		calculateSDP4orSGP4(tsince);

		/* Scale position and velocity vectors to km and km/sec */
		AbstractSatellite.convertSatState(position, velocity);

		/* Calculate velocity of satellite */

		AbstractSatellite.magnitude(velocity);

		final Vector4 squintVector = new Vector4();

		//
		// /** All angles in rads. Distance in km. Velocity in km/S **/
		// /* Calculate satellite Azi, Ele, Range and Range-rate */
		calculateObs(julUTC, position, velocity, gsPos, squintVector);
		//
		/* Calculate satellite Lat North, Lon East and Alt. */

		calculateLatLonAlt(julUTC);

		satPos.setTime(date);

		satPos.setEclipsed(isEclipsed());
		satPos.setEclipseDepth(eclipseDepth);

		return satPos;
	}

	private void calculateSDP4orSGP4(final double tsince) {
		if (tle.isDeepspace()) {
			((DeepSpaceSatellite) this).calculateSDP4(tsince);
		} else {
			((LEOSatellite) this).calculateSGP4(tsince);
		}
	}

	/**
	 * Calculate_User_PosVel() passes the user'S observer position and the time
	 * of interest and returns the ECI position and velocity of the observer.
	 * The velocity calculation assumes the observer position is stationary
	 * relative to the earth'S surface.
	 * 
	 * Reference: The 1992 Astronomical Almanac, page K11.
	 * 
	 * @param time
	 *            the time
	 * @param gsPos
	 *            the ground station position
	 * @param obsPos
	 *            the position of the observer
	 * @param obsVel
	 *            the velocity of the observer
	 */
	private static void calculateUserPosVel(final double time,
			final GroundStationPosition gsPos,
			AtomicReference<Double> gsPosTheta, final Vector4 obsPos,
			final Vector4 obsVel) {

		gsPosTheta.set(AbstractSatellite.mod2PI(AbstractSatellite
				.thetaGJD(time) + DEG2RAD * gsPos.getLongitude()));
		final double c = AbstractSatellite
				.invert(Math.sqrt(1.0
						+ FLATTENING_FACTOR
						* (FLATTENING_FACTOR - 2)
						* AbstractSatellite.sqr(Math.sin(DEG2RAD
								* gsPos.getLatitude()))));
		final double sq = AbstractSatellite.sqr(1.0 - FLATTENING_FACTOR) * c;
		final double achcp = (EARTH_RADIUS_KM * c + (gsPos.getHeightAMSL() / 1000.0))
				* Math.cos(DEG2RAD * gsPos.getLatitude());
		obsPos.setXYZ(achcp * Math.cos(gsPosTheta.get()),
				achcp * Math.sin(gsPosTheta.get()),
				(EARTH_RADIUS_KM * sq + (gsPos.getHeightAMSL() / 1000.0))
						* Math.sin(DEG2RAD * gsPos.getLatitude()));
		obsVel.setXYZ(-MFACTOR * obsPos.getY(), MFACTOR * obsPos.getX(), 0);
		AbstractSatellite.magnitude(obsPos);
		AbstractSatellite.magnitude(obsVel);
	}

	/**
	 * The procedures Calculate_Obs and Calculate_RADec calculate thetopocentric
	 * coordinates of the object with ECI position, {pos}, and velocity, {vel},
	 * from location {geodetic} at {time}. The {obs_set} returned for
	 * Calculate_Obs consists of azimuth, elevation, range, and range rate (in
	 * that order) with units of radians, radians, kilometers, and
	 * kilometers/second, respectively. The WGS '72 geoid is used and the effect
	 * of atmospheric refraction (under standard temperature and pressure) is
	 * incorporated into the elevation calculation; the effect of atmospheric
	 * refraction on range and range rate has not yet been quantified.
	 * 
	 * The {obs_set} for Calculate_RADec consists of right ascension and
	 * declination (in that order) in radians. Again, calculations are based
	 * ontopocentric position using the WGS '72 geoid and incorporating
	 * atmospheric refraction.
	 * 
	 * @param julianUTC
	 *            Julian date of UTC
	 * @param positionVector
	 *            the position vector
	 * @param velocityVector
	 *            the velocity vector
	 * @param gsPos
	 *            the ground tstation position
	 * @param squintVector
	 *            the squint vector
	 * @param satellitePosition
	 *            the satellite position
	 * 
	 */
	private void calculateObs(final double julianUTC,
			final Vector4 positionVector, final Vector4 velocityVector,
			final GroundStationPosition gsPos, final Vector4 squintVector) {

		final Vector4 obsPos = new Vector4();
		final Vector4 obsVel = new Vector4();
		final Vector4 range = new Vector4();
		final Vector4 rgvel = new Vector4();

		AtomicReference<Double> gsPosTheta = new AtomicReference<Double>();
		AbstractSatellite.calculateUserPosVel(julianUTC, gsPos, gsPosTheta,
				obsPos, obsVel);

		range.setXYZ(positionVector.getX() - obsPos.getX(),
				positionVector.getY() - obsPos.getY(), positionVector.getZ()
						- obsPos.getZ());

		/* Save these values globally for calculating squint angles later... */

		squintVector.setXYZ(range.getX(), range.getY(), range.getZ());

		rgvel.setXYZ(velocityVector.getX() - obsVel.getX(),
				velocityVector.getY() - obsVel.getY(), velocityVector.getZ()
						- obsVel.getZ());

		AbstractSatellite.magnitude(range);

		final double sinLat = Math.sin(DEG2RAD * gsPos.getLatitude());
		final double cosLat = Math.cos(DEG2RAD * gsPos.getLatitude());
		final double sinTheta = Math.sin(gsPosTheta.get());
		final double cosTheta = Math.cos(gsPosTheta.get());
		final double topS = sinLat * cosTheta * range.getX() + sinLat
				* sinTheta * range.getY() - cosLat * range.getZ();
		final double topE = -sinTheta * range.getX() + cosTheta * range.getY();
		final double topZ = cosLat * cosTheta * range.getX() + cosLat
				* sinTheta * range.getY() + sinLat * range.getZ();
		double azim = Math.atan(-topE / topS);

		if (topS > 0.0) {
			azim = azim + Math.PI;
		}

		if (azim < 0.0) {
			azim = azim + TWO_PI;
		}

		satPos.setAzimuth(azim);
		satPos.setElevation(Math.asin(topZ / range.getW()));
		satPos.setRange(range.getW());
		satPos.setRangeRate(AbstractSatellite.dot(range, rgvel) / range.getW());

		final int sector = (int) (satPos.getAzimuth() / TWO_PI * 360.0 / 10.0);

		double elevation = (satPos.getElevation() / Satellite.TWO_PI) * 360.0;

		if (elevation > 90) {
			elevation = 180 - elevation;
		}

		satPos.setAboveHorizon((elevation - gsPos.getHorizonElevations()[sector]) > EPSILON);
	}

	@Override
	public boolean willBeSeen(final GroundStationPosition qth) {

		if (tle.getMeanmo() < 1e-8) {
			return false;
		} else {
			double lin = tle.getIncl();

			if (lin >= 90.0) {
				lin = 180.0 - lin;
			}

			final double sma = 331.25 * Math.exp(Math.log(1440.0 / tle
					.getMeanmo()) * (2.0 / 3.0));
			final double apogee = sma * (1.0 + tle.getEccn()) - EARTH_RADIUS_KM;

			return (Math.acos(EARTH_RADIUS_KM / (apogee + EARTH_RADIUS_KM)) + (lin * DEG2RAD)) > Math
					.abs(qth.getLatitude() * DEG2RAD);
		}

	}

	/**
	 * @return the s4
	 */
	protected double getS4() {
		return s4;
	}

	/**
	 * @return the qoms24
	 */
	protected double getQoms24() {
		return qoms24;
	}

	/**
	 * Checks and adjusts the calculation if the perigee is less tan 156KM.
	 */
	private void checkPerigee() {
		s4 = S;
		qoms24 = QOMS2T;

		if (perigee < PERIGEE_156_KM) {
			if (perigee <= 98.0) {
				s4 = 20.0;
			} else {
				s4 = perigee - 78.0;
			}

			qoms24 = Math.pow((120 - s4) / EARTH_RADIUS_KM, 4);
			s4 = s4 / EARTH_RADIUS_KM + 1.0;
		}
	}

	/**
	 * Sets and checks the perigee making adjustments to s4 and qoms24 if necessary.
	 * @param perigee
	 *            the perigee to set
	 */
	protected void setPerigee(final double perigee) {
		this.perigee = perigee;
		checkPerigee();
	}

	static class Vector4 {

		/** the w part of the vector. ` */
		private double w;
		/** the x part of the vector. ` */
		private double x;
		/** the y part of the vector. ` */
		private double y;
		/** the z part of the vector. ` */
		private double z;

		/** default constructor. */
		Vector4() {
			this.w = 0.0;
			this.x = 0.0;
			this.y = 0.0;
			this.z = 0.0;
		}

		/**
		 * @param w
		 *            the w value
		 * @param x
		 *            the x value
		 * @param y
		 *            the y value
		 * @param z
		 *            the z value
		 */
		Vector4(final double w, final double x, final double y, final double z) {
			this.w = w;
			this.x = x;
			this.y = y;
			this.z = z;
		}

		/**
		 * Gets the string representation of the object.
		 * 
		 * @return the string representation of the object
		 */
		@Override
		public final String toString() {
			return "w: " + w + ", x: " + x + ", y: " + y + ", z: " + z;
		}

		/**
		 * @return the w
		 */
		public final double getW() {
			return w;
		}

		/**
		 * @param w
		 *            the w to set
		 */
		public final void setW(final double w) {
			this.w = w;
		}

		/**
		 * @return the x
		 */
		public final double getX() {
			return x;
		}

		/**
		 * @param x
		 *            the x to set
		 */
		public final void setX(final double x) {
			this.x = x;
		}

		/**
		 * @return the y
		 */
		public final double getY() {
			return y;
		}

		/**
		 * @param y
		 *            the y to set
		 */
		public final void setY(final double y) {
			this.y = y;
		}

		/**
		 * @return the z
		 */
		public final double getZ() {
			return z;
		}

		/**
		 * @param z
		 *            the z to set
		 */
		public final void setZ(final double z) {
			this.z = z;
		}

		public final void multiply(final double multiplier) {
			this.x *= multiplier;
			this.y *= multiplier;
			this.z *= multiplier;
		}

		public final void setXYZ(final double xValue, final double yValue,
				final double zValue) {
			this.x = xValue;
			this.y = yValue;
			this.z = zValue;
		}

		public Vector4 subtract(final Vector4 vector) {
			return new Vector4(this.w - vector.w, this.x - vector.x, this.y
					- vector.y, this.z - vector.z);
		}

		public static final Vector4 scalarMultiply(final Vector4 vector,
				final double multiplier) {

			return new Vector4(vector.w * Math.abs(multiplier), vector.x
					* multiplier, vector.y * multiplier, vector.z * multiplier);
		}

		/**
		 * Calculates the angle between vectors v1 and v2.
		 */
		public static final double angle(final Vector4 v1, final Vector4 v2) {
			AbstractSatellite.magnitude(v1);
			AbstractSatellite.magnitude(v2);
			return Math.acos(AbstractSatellite.dot(v1, v2) / (v1.w * v2.w));
		}

		/**
		 * Subtracts vector v2 from v1.
		 */
		public static final Vector4 subtract(final Vector4 v1, final Vector4 v2) {

			final Vector4 v3 = new Vector4();
			v3.x = v1.x - v2.x;
			v3.y = v1.y - v2.y;
			v3.z = v1.z - v2.z;
			AbstractSatellite.magnitude(v3);
			return v3;
		}
	}

	/**
	 * Solves Keplers' Equation.
	 * 
	 * @param temp
	 *            an array of temporary values we pass around as part of the
	 *            orbit calculation.
	 * @param axn
	 * @param ayn
	 * @param capu
	 */
	protected static void converge(final double[] temp, final double axn,
			final double ayn, final double capu) {

		boolean converged = false;
		int i = 0;

		do {
			temp[7] = Math.sin(temp[2]);
			temp[8] = Math.cos(temp[2]);
			temp[3] = axn * temp[7];
			temp[4] = ayn * temp[8];
			temp[5] = axn * temp[8];
			temp[6] = ayn * temp[7];
			final double epw = (capu - temp[4] + temp[3] - temp[2])
					/ (1.0 - temp[5] - temp[6]) + temp[2];

			if (Math.abs(epw - temp[2]) <= EPSILON) {
				converged = true;
			} else {
				temp[2] = epw;
			}

		} while (i++ < 10 && !converged);
	}

	@Override
	public synchronized void calculateSatelliteVectors(final Date date) {
		// Re-initialize, object can contain data from previous calculations
		satPos = new SatPos();

		// Date/time for which the satellite position and velocity are
		// calculated
		julUTC = AbstractSatellite.calcCurrentDaynum(date) + 2444238.5;

		// Calculate time since epoch in minutes

		final double tsince = (julUTC - julEpoch) * MINS_PER_DAY;

		// Calculations of satellite position, no ground stations involved here
		// yet
		calculateSDP4orSGP4(tsince);
		
		// Scale position and velocity vectors to km and km/s
		AbstractSatellite.convertSatState(position, velocity);

		// Calculate the magnitude of the velocity of satellite
		AbstractSatellite.magnitude(velocity);

		satPos.setEclipsed(isEclipsed());
		satPos.setEclipseDepth(eclipseDepth);

		satPos.setTime(date);
	}

	@Override
	public synchronized SatPos calculateSatelliteGroundTrack() {
		calculateLatLonAlt(julUTC);

		return this.satPos;
	}

	@Override
	public synchronized SatPos calculateSatPosForGroundStation(
			final GroundStationPosition gsPos) {
		final Vector4 squintVector = new Vector4();
		// All angles in rads. Distance in km. Velocity in km/s
		// Calculate satellite Azi, Ele, Range and Range-rate
		calculateObs(julUTC, position, velocity, gsPos, squintVector);

		return this.satPos;
	}

	/**
	 * Determines if the satellite is in sunlight.
	 */
	private boolean isEclipsed() {

		final Vector4 sunVector = calculateSunVector();

		/* Calculates stellite's eclipse status and depth */

		/* Determine partial eclipse */

		final double sdEarth = Math.asin(EARTH_RADIUS / position.w);
		final Vector4 rho = Vector4.subtract(sunVector, position);
		final double sdSun = Math.asin(SOLAR_RADIUS / rho.w);
		final Vector4 earth = Vector4.scalarMultiply(position, -1);
		final double delta = Vector4.angle(sunVector, earth);
		eclipseDepth = sdEarth - sdSun - delta;

		if (sdEarth < sdSun) {
			return false;
		} else {
			return eclipseDepth >= 0;
		}
	}

	private Vector4 calculateSunVector() {

		final double mjd = julUTC - 2415020.0;
		final double year = 1900 + mjd / 365.25;
		final double solTime = (mjd + deltaEt(year) / SECS_PER_DAY) / 36525.0;

		final double m = radians(AbstractSatellite.modulus(
				358.47583
						+ AbstractSatellite.modulus(35999.04975 * solTime,
								360.0) - (0.000150 + 0.0000033 * solTime)
						* AbstractSatellite.sqr(solTime), 360.0));
		final double l = radians(AbstractSatellite.modulus(279.69668
				+ AbstractSatellite.modulus(36000.76892 * solTime, 360.0)
				+ 0.0003025 * AbstractSatellite.sqr(solTime), 360.0));
		final double e = 0.01675104 - (0.0000418 + 0.000000126 * solTime)
				* solTime;
		final double c = radians((1.919460 - (0.004789 + 0.000014 * solTime)
				* solTime)
				* Math.sin(m)
				+ (0.020094 - 0.000100 * solTime)
				* Math.sin(2 * m) + 0.000293 * Math.sin(3 * m));
		final double o = radians(AbstractSatellite.modulus(
				259.18 - 1934.142 * solTime, 360.0));
		final double lsa = AbstractSatellite.modulus(l + c
				- radians(0.00569 - 0.00479 * Math.sin(o)), TWO_PI);
		final double nu = AbstractSatellite.modulus(m + c, TWO_PI);
		double r = 1.0000002 * (1.0 - AbstractSatellite.sqr(e))
				/ (1.0 + e * Math.cos(nu));
		final double eps = radians(23.452294
				- (0.0130125 + (0.00000164 - 0.000000503 * solTime) * solTime)
				* solTime + 0.00256 * Math.cos(o));
		r = ASTRONOMICAL_UNIT * r;

		return new Vector4(r, r * Math.cos(lsa), r * Math.sin(lsa)
				* Math.cos(eps), r * Math.sin(lsa) * Math.sin(eps));
	}

	/**
	 * The function Delta_ET has been added to allow calculations on the
	 * position of the sun. It provides the difference between UT (approximately
	 * the same as UTC) and ET (now referred to as TDT) This function is based
	 * on a least squares fit of data from 1950 to 1991 and will need to be
	 * updated periodically.
	 * 
	 * Values determined using data from 1950-1991 in the 1990 Astronomical
	 * Almanac. See DELTA_ET.WQ1 for details.
	 */
	private double deltaEt(final double year) {

		return 26.465 + 0.747622 * (year - 1950) + 1.886913
				* Math.sin(TWO_PI * (year - 1975) / 33);
	}

	/**
	 * Returns angle in radians from argument in degrees.
	 */
	private double radians(final double degrees) {
		return degrees * DEG2RAD;
	}

	protected void calculatePhase(final double xlt, final double xnode,
			final double omgadf) {
		/* Phase in radians */
		double phaseValue = xlt - xnode - omgadf + TWO_PI;

		if (phaseValue < 0.0) {
			phaseValue += TWO_PI;
		}

		satPos.setPhase(AbstractSatellite.mod2PI(phaseValue));
	}

	protected void calculatePositionAndVelocity(final double rk,
			final double uk, final double xnodek, final double xinck,
			final double rdotk, final double rfdotk) {
		/* Orientation vectors */
		final double sinuk = Math.sin(uk);
		final double cosuk = Math.cos(uk);
		final double sinik = Math.sin(xinck);
		final double cosik = Math.cos(xinck);
		final double sinnok = Math.sin(xnodek);
		final double cosnok = Math.cos(xnodek);
		final double xmx = -sinnok * cosik;
		final double xmy = cosnok * cosik;
		final double ux = xmx * sinuk + cosnok * cosuk;
		final double uy = xmy * sinuk + sinnok * cosuk;
		final double uz = sinik * sinuk;
		final double vx = xmx * cosuk - cosnok * sinuk;
		final double vy = xmy * cosuk - sinnok * sinuk;
		final double vz = sinik * cosuk;

		/* Position and velocity */
		position.setXYZ(ux, uy, uz);
		position.multiply(rk);
		velocity.setX(rdotk * ux + rfdotk * vx);
		velocity.setY(rdotk * uy + rfdotk * vy);
		velocity.setZ(rdotk * uz + rfdotk * vz);
	}

	protected static double invert(final double value) {
		return 1.0 / value;
	}

	/**
	 * @return the eclipseDepth
	 */
	public final double getEclipseDepth() {
		return eclipseDepth;
	}
}
