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
package com.github.amsacode.predict4java;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author g4dpz
 * 
 */
public class SatPos {
	private static final String NL = "\n";
	private static final String DEG_CR = " deg.\n";

	/* WGS 84 Earth radius km */
	private static final double EARTH_RADIUS_KM = 6.378137E3;
	private static final double R0 = 6378.16;

	// the internal representation will be in radians
	private double azimuth;
	private double elevation;
	private double latitude;
	private double longitude;

	private Date time;
	private double range;
	private double rangeRate;
	private double phase;
	private double altitude;
	private double theta;

	private double eclipseDepth;
	private boolean eclipsed;

	private boolean aboveHorizon;

	/**
	 * Default constructor.
	 */
	public SatPos() {

	}

	/**
	 * Constructs a Satellite Position.
	 * 
	 * @param azimuth
	 *            the Azimuth
	 * @param elevation
	 *            the Elevation
	 * @param theTime
	 *            the Time
	 */
	public SatPos(final double azimuth, final double elevation,
			final Date theTime) {
		this.azimuth = azimuth;
		this.elevation = elevation;
		this.time = new Date(theTime.getTime());
	}

	/**
	 * @return the azimuth
	 */
	public double getAzimuth() {
		return azimuth;
	}

	/**
	 * @return the elevation
	 */
	public double getElevation() {
		return elevation;
	}

	/**
	 * @return time for the SatPos
	 */
	public Date getTime() {
		return new Date(time.getTime());
	}

	/**
	 * @return the range
	 */
	public final double getRange() {
		return range;
	}

	/**
	 * @param range
	 *            the range to set
	 */
	public final void setRange(final double range) {
		this.range = range;
	}

	/**
	 * @return the rangeRate
	 */
	public final double getRangeRate() {
		return rangeRate;
	}

	/**
	 * @param rangeRate
	 *            the rangeRate to set
	 */
	public final void setRangeRate(final double rangeRate) {
		this.rangeRate = rangeRate;
	}

	/**
	 * @return the phase
	 */
	public final double getPhase() {
		return phase;
	}

	/**
	 * @param phase
	 *            the phase to set
	 */
	public final void setPhase(final double phase) {
		this.phase = phase;
	}

	/**
	 * @return the latitude
	 */
	public final double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public final void setLatitude(final double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public final double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public final void setLongitude(final double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the altitude in km
	 */
	public final double getAltitude() {
		return altitude;
	}

	/**
	 * @param altitude
	 *            the altitude to set
	 */
	public final void setAltitude(final double altitude) {
		this.altitude = altitude;
	}

	/**
	 * @return the theta
	 */
	public final double getTheta() {
		return theta;
	}

	/**
	 * @param theta
	 *            the theta to set
	 */
	public final void setTheta(final double theta) {
		this.theta = theta;
	}

	/**
	 * @param azimuth
	 *            the azimuth to set
	 */
	public final void setAzimuth(final double azimuth) {
		this.azimuth = azimuth;
	}

	/**
	 * @param elevation
	 *            the elevation to set
	 */
	public final void setElevation(final double elevation) {
		this.elevation = elevation;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public final void setTime(final Date time) {
		this.time = new Date(time.getTime());
	}

	/**
	 * @return the aboveHorizon
	 */
	public final boolean isAboveHorizon() {
		return aboveHorizon;
	}

	/**
	 * @param aboveHorizon
	 *            the aboveHorizon to set
	 */
	public final void setAboveHorizon(final boolean aboveHorizon) {
		this.aboveHorizon = aboveHorizon;
	}

	/**
	 * @return the eclipseDepth
	 */
	protected final double getEclipseDepth() {
		return eclipseDepth;
	}

	/**
	 * @param eclipseDepth
	 *            the eclipseDepth to set
	 */
	protected final void setEclipseDepth(final double eclipseDepth) {
		this.eclipseDepth = eclipseDepth;
	}

	/**
	 * @return the eclipsed
	 */
	protected final boolean isEclipsed() {
		return eclipsed;
	}

	/**
	 * @param eclipsed
	 *            the eclipsed to set
	 */
	protected final void setEclipsed(final boolean eclipsed) {
		this.eclipsed = eclipsed;
	}

	/**
	 * @return a pretty printed version of the Satellite Position
	 */
	@Override
	public String toString() {
		return "Azimuth:    " + azimuth / (Math.PI * 2.0) * 360 + DEG_CR
				+ "Elevation:  " + elevation / (Math.PI * 2.0) * 360 + DEG_CR
				+ "Latitude:   " + latitude / (Math.PI * 2.0) * 360 + DEG_CR
				+ "Longitude:  " + longitude / (Math.PI * 2.0) * 360 + DEG_CR

				+ "Date:       " + time + NL + "Range:        " + range
				+ " km.\n" + "Range rate:   " + rangeRate + " m/S.\n"
				+ "Phase:        " + phase + " /(256)\n" + "Altitude:     "
				+ altitude + " km\n" + "Theta:        " + theta + " rad/sec\n"
				+ "Eclipsed:     " + eclipsed + NL + "Eclipse depth:"
				+ eclipseDepth + " radians\n";
	}

	public String toShortString() {
		String returnString = "";

		final NumberFormat numberFormat = NumberFormat.getNumberInstance();

		numberFormat.setMaximumFractionDigits(0);
		returnString = returnString + "Elevation: "
				+ numberFormat.format(elevation / (Math.PI * 2.0) * 360)
				+ DEG_CR + "Azimuth: "
				+ numberFormat.format(azimuth / (Math.PI * 2.0) * 360) + DEG_CR;

		numberFormat.setMaximumFractionDigits(2);
		returnString = returnString + "Latitude: "
				+ numberFormat.format(latitude / (Math.PI * 2.0) * 360)
				+ DEG_CR + "Longitude: "
				+ numberFormat.format(longitude / (Math.PI * 2.0) * 360)
				+ DEG_CR;

		numberFormat.setMaximumFractionDigits(0);
		returnString = returnString + "Range: " + numberFormat.format(range)
				+ " Km";

		return returnString;

	}

	/**
	 * Calculates the footprint range circle using the given increment. TODO
	 * where is first point, give heading.
	 * 
	 * @param incrementDegrees
	 * @return
	 */
	public final List<Position> getRangeCircle(double incrementDegrees) {

		return calculateRangeCirclePoints(this, incrementDegrees);

	}

	/**
	 * Calculates the footprint range circle using an increment of 1.0 degrees.
	 * 
	 * @param pos
	 * @return a list of {@link Position}
	 */
	public final List<Position> getRangeCircle() {
		return getRangeCircle(1.0);
	}

	/**
	 * Calculates the footprint range circle using the given increment.
	 * 
	 * @param pos
	 * @return a list of {@link Position}
	 */
	private static List<Position> calculateRangeCirclePoints(final SatPos pos,
			double incrementDegrees) {

		final double radiusKm = pos.getRangeCircleRadiusKm();

		final double latitude = pos.latitude;
		final double longitude = pos.longitude;
		final double beta = radiusKm / R0;
		List<Position> result = new ArrayList<Position>();
		for (int azi = 0; azi < 360; azi += incrementDegrees) {
			final double azimuth = (azi / 360.0) * 2.0 * Math.PI;
			double rangelat = Math.asin(Math.sin(latitude) * Math.cos(beta)
					+ Math.cos(azimuth) * Math.sin(beta) * Math.cos(latitude));
			final double num = Math.cos(beta)
					- (Math.sin(latitude) * Math.sin(rangelat));
			final double den = Math.cos(latitude) * Math.cos(rangelat);
			double rangelong;

			if (azi == 0 && (beta > ((Math.PI / 2.0) - latitude))) {
				rangelong = longitude + Math.PI;
			} else if (azi == 180 && (beta > ((Math.PI / 2.0) - latitude))) {
				rangelong = longitude + Math.PI;
			} else if (Math.abs(num / den) > 1.0) {
				rangelong = longitude;
			} else {
				if ((180 - azi) >= 0) {
					rangelong = longitude - Math.acos(num / den);
				} else {
					rangelong = longitude + Math.acos(num / den);
				}
			}

			while (rangelong < 0.0) {
				rangelong += Math.PI * 2.0;
			}

			while (rangelong > Math.PI * 2.0) {
				rangelong -= Math.PI * 2.0;
			}

			rangelat = (rangelat / (2.0 * Math.PI)) * 360.0;
			rangelong = (rangelong / (2.0 * Math.PI)) * 360.0;

			// if (rangelong < 180.0) {
			// rangelong = -rangelong;
			// }
			// else if (rangelong > 180.0) {
			// rangelong = 360.0 - rangelong;
			// }
			//
			// if (rangelat < 90.0) {
			// rangelat = -rangelat;
			// }
			// else if (rangelat > 90.0) {
			// rangelat = 180.0 - rangelat;
			// }

			result.add(new Position(rangelat, rangelong));

		}

		return result;
	}

	public double getRangeCircleRadiusKm() {
		return 0.5 * (12756.33 * Math.acos(EARTH_RADIUS_KM
				/ (EARTH_RADIUS_KM + altitude)));
	}

}
