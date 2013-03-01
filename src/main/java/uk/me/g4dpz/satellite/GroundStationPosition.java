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

/**
 * The location of the Satellite Ground Station. Instances of this class are
 * immutable and thus thread safe.
 * 
 * @author g4dpz
 */
public class GroundStationPosition {
	private double latitude;
	private double longitude;
	private double heightAMSL;
	private final int[] horizonElevations = new int[36];
	private final String name;

	/**
	 * @param latitude
	 *            the latitue of the ground station in degrees, North: positive
	 * @param longitude
	 *            the longitude of the ground station in degrees, East: positive
	 * @param heightAMSL
	 *            the height of te ground station above mean sea level, in
	 *            metres
	 */
	public GroundStationPosition(final double latitude, final double longitude,
			final double heightAMSL) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.heightAMSL = heightAMSL;
		this.name = "";
	}

	/**
	 * @param latitude
	 *            the latitue of the ground station in degrees, North: positive
	 * @param longitude
	 *            the longitude of the ground station in degrees, East: positive
	 * @param heightAMSL
	 *            the height of te ground station above mean sea level, in
	 *            metres
	 */
	public GroundStationPosition(final double latitude, final double longitude,
			final double heightAMSL, String name) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.heightAMSL = heightAMSL;
		this.name = name;
	}

	/**
	 * @return latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @return longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @return elevation
	 */
	public double getHeightAMSL() {
		return heightAMSL;
	}

	/**
	 * Returns a copy of the horizon elevations.
	 * 
	 * @return the horizonElevations
	 */
	public final int[] getHorizonElevations() {
		final int[] horizonElevationsCopy = new int[horizonElevations.length];

		System.arraycopy(horizonElevationsCopy, 0, horizonElevations, 0,
				horizonElevations.length);

		return horizonElevationsCopy;
	}

	public String getName() {
		return name;
	}

}
