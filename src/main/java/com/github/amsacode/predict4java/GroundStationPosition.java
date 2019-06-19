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

import java.util.Arrays;

import com.github.davidmoten.guavamini.Preconditions;

/**
 * The location of the Satellite Ground Station. Instances of this class are
 * immutable and thus thread safe.
 * 
 * @author g4dpz
 */
public class GroundStationPosition {
	private static final int NUM_SECTORS = 36; //each sector is 10 degrees
	
    private final double latitude;
	private final double longitude;
	private final double heightAMSL;
	private final int[] horizonElevations;
	private final String name;

    /**
     * @param latitude
     *            the latitude of the ground station in degrees, North: positive
     * @param longitude
     *            the longitude of the ground station in degrees, East: positive
     * @param heightAMSL
     *            the height of the ground station above mean sea level, in metres
     * @param name
     *            the name of the ground station. If null passed then an empty
     *            string is used for the name
     * @param horizonElevations
     *            the elevations of the horizon in degrees from the ground station
     *            by 10 degree sectors. If null is passed then 0 is assumed for all
     *            sectors.
     */
    public GroundStationPosition(final double latitude, final double longitude, final double heightAMSL, String name,
            int[] horizonElevations) {
        Preconditions.checkArgument(horizonElevations == null || horizonElevations.length == NUM_SECTORS,
                "horizonElevations array must have length 36 corresponding to 10 degree sectors");
        this.latitude = latitude;
        this.longitude = longitude;
        this.heightAMSL = heightAMSL;
        this.name = name == null ? "" : name;
        // Note that a copy of horizon elevations is made to honour the thread-safety
        // claim of this class
        this.horizonElevations = horizonElevations == null ? new int[NUM_SECTORS]
                : Arrays.copyOf(horizonElevations, horizonElevations.length);
    }
	
	/**
     * @param latitude
     *            the latitude of the ground station in degrees, North: positive
     * @param longitude
     *            the longitude of the ground station in degrees, East: positive
     * @param heightAMSL
     *            the height of the ground station above mean sea level, in
     *            metres
     */
    public GroundStationPosition(final double latitude, final double longitude,
            final double heightAMSL) {
        this(latitude, longitude, heightAMSL, null, null);
    }
    
    /**
     * @param latitude
     *            the latitude of the ground station in degrees, North: positive
     * @param longitude
     *            the longitude of the ground station in degrees, East: positive
     * @param heightAMSL
     *            the height of the ground station above mean sea level, in metres
     * @param name
     *            the name of the ground station. If null passed then an empty
     *            string is used for the name
     */
    public GroundStationPosition(final double latitude, final double longitude, final double heightAMSL, String name) {
        this(latitude, longitude, heightAMSL, name, null);
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
	 * Returns the horizon elevation in degrees by 10 degree sector.
	 * 
	 * @return the horizonElevation in degrees.
	 */
	public final int getHorizonElevation(int sector) {
		return horizonElevations[sector];
	}

	public String getName() {
		return name;
	}

}
