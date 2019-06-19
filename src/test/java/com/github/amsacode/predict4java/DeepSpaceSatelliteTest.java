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

import static org.assertj.core.api.Assertions.assertThat;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

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
        assertThat(tle.isDeepspace()).isTrue();

        final Satellite satellite = SatelliteFactory.createSatellite(tle);

        final SatPos satellitePosition = satellite.getPosition(GROUND_STATION,
                timeNow.toDate());

        assertThat(String.format(FORMAT_9_7F, satellitePosition.getAzimuth())).isEqualTo("2.2579325");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getElevation())).isEqualTo("0.4144053");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getLongitude())).isEqualTo("0.7091175");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getLatitude())).isEqualTo("0.0442970");
        assertThat(String.format(FORMAT_10_7F, satellitePosition.getAltitude())).isEqualTo("58847.2042542");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getPhase())).isEqualTo("3.2039351");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getRange())).isEqualTo("62390.2433539");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getRangeRate())).isEqualTo("-0.2187132");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getTheta())).isEqualTo("0.6810134");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getEclipseDepth())).isEqualTo("-2.7759541");
        assertThat(satellitePosition.isEclipsed()).isFalse();
        assertThat(satellite.willBeSeen(GROUND_STATION)).isTrue();
        assertThat(satellitePosition.isAboveHorizon()).isTrue();

    }


	@Test
	public void testToStringMethod() {
        final TLE tle = new TLE(DEEP_SPACE_TLE);
        final Satellite satellite = SatelliteFactory.createSatellite(tle);
        final SatPos satellitePosition = satellite.getPosition(GROUND_STATION,
                timeNow.toDate());
        assertThat(satellitePosition.toString()).isNotNull();
    }

	@Test
	public void testToShortStringMethod() {
        final TLE tle = new TLE(DEEP_SPACE_TLE);
        final Satellite satellite = SatelliteFactory.createSatellite(tle);
        final SatPos satellitePosition = satellite.getPosition(GROUND_STATION,
                timeNow.toDate());
        assertThat(satellitePosition.toShortString()).isNotNull();
    }

	@Test
	public void testGeoSynchSatellite() {
        timeNow = new DateTime(DATE_2009_12_26T00_00_00Z);

        final TLE tle = new TLE(GEOSYNC_TLE);

        assertThat(tle.isDeepspace()).isTrue();

        final Satellite satellite = SatelliteFactory.createSatellite(tle);

        final SatPos satellitePosition = satellite.getPosition(GROUND_STATION,
                timeNow.toDate());

        assertThat(tle.isDeepspace()).isTrue();
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getAzimuth())).isEqualTo("5.7530820");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getElevation())).isEqualTo("-0.8368869");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getLongitude())).isEqualTo("3.4946919");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getLatitude())).isEqualTo("-0.1440008");
        assertThat(String.format(FORMAT_10_7F, satellitePosition.getAltitude())).isEqualTo("36031.8182912");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getPhase())).isEqualTo("0.5377382");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getRange())).isEqualTo("46934.3153284");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getRangeRate())).isEqualTo("0.0271561");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getTheta())).isEqualTo("-1.1369975");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getEclipseDepth())).isEqualTo("-2.5674344");
        assertThat(satellitePosition.isEclipsed()).isFalse();
        assertThat(satellite.willBeSeen(GROUND_STATION)).isTrue();
    }

	@Test
	public void testMolniyaSatellite() {
        timeNow = new DateTime(DATE_2009_12_26T00_00_00Z);

        final TLE tle = new TLE(MOLNIYA_TLE);

        final Satellite satellite = SatelliteFactory.createSatellite(tle);

        final SatPos satellitePosition = satellite.getPosition(GROUND_STATION,
                timeNow.toDate());

        assertThat(tle.isDeepspace()).isTrue();
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getAzimuth())).isEqualTo("6.2095948");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getElevation())).isEqualTo("0.0572862");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getLongitude())).isEqualTo("3.2171857");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getLatitude())).isEqualTo("0.8635892");
        assertThat(String.format(FORMAT_9_3F, satellitePosition.getAltitude())).isEqualTo("35280.747");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getPhase())).isEqualTo("2.0315668");
        assertThat(String.format(FORMAT_9_3F, satellitePosition.getRange())).isEqualTo("40814.880");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getRangeRate())).isEqualTo("0.9164450");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getTheta())).isEqualTo("-1.4145037");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getEclipseDepth())).isEqualTo("-1.7199331");
        assertThat(satellitePosition.isEclipsed()).isFalse();
        assertThat(satellite.willBeSeen(GROUND_STATION)).isTrue();
    }

}
