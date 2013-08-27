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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.github.amsacode.predict4java.SatPos;
import com.github.amsacode.predict4java.Satellite;
import com.github.amsacode.predict4java.SatelliteFactory;
import com.github.amsacode.predict4java.TLE;

/**
 * @author David A. B. Johnson, g4dpz
 * 
 */
public final class DeepSpaceSatelliteTest extends AbstractSatelliteTestBase {

	private static final String DATE_2009_12_26T00_00_00Z = "2009-12-26T00:00:00Z";
	private static final String FORMAT_9_3F = "%9.3f";
	private static final String FORMAT_10_7F = "%10.7f";
	private static final String FORMAT_9_7F = "%9.7f";
	private DateTime timeNow;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		timeNow = new DateTime("2009-04-17T10:10:52Z");
	}

	@Test
	public void testDeepSpaceSatellite() {

		final TLE tle = new TLE(DEEP_SPACE_TLE);

		assertTrue(tle.isDeepspace());

		final Satellite satellite = SatelliteFactory.createSatellite(tle);

		final SatPos satellitePosition = satellite.getPosition(GROUND_STATION,
				timeNow.toDate());

		assertEquals("2.2579325",
				String.format(FORMAT_9_7F, satellitePosition.getAzimuth()));
		assertEquals("0.4144053",
				String.format(FORMAT_9_7F, satellitePosition.getElevation()));
		assertEquals("0.7091175",
				String.format(FORMAT_9_7F, satellitePosition.getLongitude()));
		assertEquals("0.0442970",
				String.format(FORMAT_9_7F, satellitePosition.getLatitude()));
		assertEquals("58847.2042542",
				String.format(FORMAT_10_7F, satellitePosition.getAltitude()));
		assertEquals("3.2039351",
				String.format(FORMAT_9_7F, satellitePosition.getPhase()));
		assertEquals("62390.2433539",
				String.format(FORMAT_9_7F, satellitePosition.getRange()));
		assertEquals("-0.2187132",
				String.format(FORMAT_9_7F, satellitePosition.getRangeRate()));
		assertEquals("0.6810134",
				String.format(FORMAT_9_7F, satellitePosition.getTheta()));
		assertEquals("-2.7759541",
				String.format(FORMAT_9_7F, satellitePosition.getEclipseDepth()));
		assertFalse(satellitePosition.isEclipsed());
		assertTrue(satellite.willBeSeen(GROUND_STATION));
		assertTrue(satellitePosition.isAboveHorizon());

	}


	@Test
	public void testToStringMethod() {
		final TLE tle = new TLE(DEEP_SPACE_TLE);
		final Satellite satellite = SatelliteFactory.createSatellite(tle);
		final SatPos satellitePosition = satellite.getPosition(GROUND_STATION,
				timeNow.toDate());
		assertNotNull(satellitePosition.toString());
	}

	@Test
	public void testToShortStringMethod() {
		final TLE tle = new TLE(DEEP_SPACE_TLE);
		final Satellite satellite = SatelliteFactory.createSatellite(tle);
		final SatPos satellitePosition = satellite.getPosition(GROUND_STATION,
				timeNow.toDate());
		assertNotNull(satellitePosition.toShortString());
	}

	@Test
	public void testGeoSynchSatellite() {

		timeNow = new DateTime(DATE_2009_12_26T00_00_00Z);

		final TLE tle = new TLE(GEOSYNC_TLE);

		assertTrue(tle.isDeepspace());

		final Satellite satellite = SatelliteFactory.createSatellite(tle);

		final SatPos satellitePosition = satellite.getPosition(GROUND_STATION,
				timeNow.toDate());

		assertTrue(tle.isDeepspace());
		assertEquals("5.7530820",
				String.format(FORMAT_9_7F, satellitePosition.getAzimuth()));
		assertEquals("-0.8368869",
				String.format(FORMAT_9_7F, satellitePosition.getElevation()));
		assertEquals("3.4946919",
				String.format(FORMAT_9_7F, satellitePosition.getLongitude()));
		assertEquals("-0.1440008",
				String.format(FORMAT_9_7F, satellitePosition.getLatitude()));
		assertEquals("36031.8182912",
				String.format(FORMAT_10_7F, satellitePosition.getAltitude()));
		assertEquals("0.5377382",
				String.format(FORMAT_9_7F, satellitePosition.getPhase()));
		assertEquals("46934.3153284",
				String.format(FORMAT_9_7F, satellitePosition.getRange()));
		assertEquals("0.0271561",
				String.format(FORMAT_9_7F, satellitePosition.getRangeRate()));
		assertEquals("-1.1369975",
				String.format(FORMAT_9_7F, satellitePosition.getTheta()));
		assertEquals("-2.5674344",
				String.format(FORMAT_9_7F, satellitePosition.getEclipseDepth()));
		assertFalse(satellitePosition.isEclipsed());
		assertTrue(satellite.willBeSeen(GROUND_STATION));
	}

	@Test
	public void testMolniyaSatellite() {

		timeNow = new DateTime(DATE_2009_12_26T00_00_00Z);

		final TLE tle = new TLE(MOLNIYA_TLE);

		final Satellite satellite = SatelliteFactory.createSatellite(tle);

		final SatPos satellitePosition = satellite.getPosition(GROUND_STATION,
				timeNow.toDate());

		assertTrue(tle.isDeepspace());
		assertEquals("6.2095948",
				String.format(FORMAT_9_7F, satellitePosition.getAzimuth()));
		assertEquals("0.0572862",
				String.format(FORMAT_9_7F, satellitePosition.getElevation()));
		assertEquals("3.2171857",
				String.format(FORMAT_9_7F, satellitePosition.getLongitude()));
		assertEquals("0.8635892",
				String.format(FORMAT_9_7F, satellitePosition.getLatitude()));
		assertEquals("35280.747",
				String.format(FORMAT_9_3F, satellitePosition.getAltitude()));
		assertEquals("2.0315668",
				String.format(FORMAT_9_7F, satellitePosition.getPhase()));
		assertEquals("40814.880",
				String.format(FORMAT_9_3F, satellitePosition.getRange()));
		assertEquals("0.9164450",
				String.format(FORMAT_9_7F, satellitePosition.getRangeRate()));
		assertEquals("-1.4145037",
				String.format(FORMAT_9_7F, satellitePosition.getTheta()));
		assertEquals("-1.7199331",
				String.format(FORMAT_9_7F, satellitePosition.getEclipseDepth()));
		assertFalse(satellitePosition.isEclipsed());
		assertTrue(satellite.willBeSeen(GROUND_STATION));
	}
}
