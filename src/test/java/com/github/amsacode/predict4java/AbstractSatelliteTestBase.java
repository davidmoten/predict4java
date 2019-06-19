/**
 predict4java: An SDP4 / SGP4 library for satellite orbit predictions

 Copyright (C)  2004-2010  David A. B. Johnson, G4DPZ.

 Author: David A. B. Johnson, G4DPZ <dave@g4dpz.me.uk>

 Comments, questions and bug reports should be submitted via
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

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @author David A. B. Johnson, g4dpz
 * 
 */
public abstract class AbstractSatelliteTestBase {

	protected AbstractSatelliteTestBase() {

	}

	static final GroundStationPosition GROUND_STATION = new GroundStationPosition(
			52.4670, -2.022, 200);

	protected static final SimpleDateFormat TZ_FORMAT;
	static {
		TZ_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		TZ_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	/** The time at which we do all the calculations. */
	static final TimeZone TZ = TimeZone.getTimeZone("UTC:UTC");

	/** Seconds per day. */
	static final long SECONDS_PER_DAY = 24 * 60 * 60;

	protected static final String[] LEO_TLE = {
			"AO-51 [+]",
			"1 28375U 04025K   09105.66391970  .00000003  00000-0  13761-4 0  3643",
			"2 28375 098.0551 118.9086 0084159 315.8041 043.6444 14.40638450251959" };

	protected static final String[] DEEP_SPACE_TLE = {
			"AO-40",
			"1 26609U 00072B   09105.66069202 -.00000356  00000-0  10000-3 0  2169",
			"2 26609 009.1977 023.4368 7962000 194.9139 106.0662 01.25584647 38840" };

	protected static final String[] GEOSYNC_TLE = {
			"EUTELSAT 2-F1",
			"1 20777U 90079B   09356.31446792  .00000081  00000-0  10000-3 0  9721",
			"2 20777   9.6834  57.1012 0004598 207.1414 152.7950  0.99346230 50950" };

	protected static final String[] MOLNIYA_TLE = {
			"MOLNIYA 1-80",
			"1 21118U 91012A   09357.87605320  .00001593  00000-0  10000-3 0  7339",
			"2 21118  61.8585 240.5458 7236516 255.2789  21.0579  2.00792202138149" };

	protected static final String[] WEATHER_TLE = {
			"TIROS N [P]",
			"1 11060U 78096A   09359.84164805 -.00000019  00000-0  13276-4 0  3673",
			"2 11060  98.9548 331.5509 0010393 187.3222 172.7804 14.17491792826101" };

	protected static final String[] DE_ORBIT_TLE = {
			"COSMOS 2421 DEB",
			"1 33139U 06026MX  09359.84164805  .10408321  74078-5  34039-2 0  6397",
			"2 33139 064.8768 254.5588 0010700 285.2081 074.8503 16.45000000 91112" };

	protected static final String LATITUDE = "52.4670";
	protected static final String LONGITUDE = "-2.022";
	protected static final int HEIGHT_AMSL = 200;

}
