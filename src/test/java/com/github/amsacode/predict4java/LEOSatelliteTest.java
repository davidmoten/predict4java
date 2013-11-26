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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import com.github.amsacode.predict4java.Position;
import com.github.amsacode.predict4java.SatPos;
import com.github.amsacode.predict4java.Satellite;
import com.github.amsacode.predict4java.SatelliteFactory;
import com.github.amsacode.predict4java.TLE;

/**
 * @author David A. B. Johnson, g4dpz
 * 
 */
public final class LEOSatelliteTest extends AbstractSatelliteTestBase {

	private static final String ECLIPSE_DEPTH = "-0.2353420";

	private static final String THETA_VALUE = "-1.8011516";

	private static final String RANGE_RATE_VALUE = "-3.0094317";

	private static final String RANGE_VALUE = "5433";

	private static final String PHASE_VALUE = "4.5526109";

	private static final String ALTITUDE_VALUE = "848.4319560";

	private static final String LATITUDE_VALUE = "1.4098576";

	private static final String LONGITUDE_VALUE = "2.8305378";

	private static final String ELEVATION_VALUE = "-0.2617647";

	private static final String AZIMUTH_VALUE = "0.0602822";

	private static final String FORMAT_4_0F = "%4.0f";

	private static final String FORMAT_10_7F = "%10.7f";

	private static final String FORMAT_9_7F = "%9.7f";

	private static final String BASE_TIME = "2009-12-26T00:00:00Z";

	private DateTime timeNow;

	/**
	 * Test method for
	 * {@link com.github.amsacode.predict4java.LEOSatellite#LEOSatellite(com.github.amsacode.predict4java.TLE)}
	 * .
	 */
	@Test
	public void testLEOSatellite() {
        timeNow = new DateTime("2009-04-17T06:57:32Z");

        final TLE tle = new TLE(LEO_TLE);

        assertThat(tle.isDeepspace()).isFalse();

        final Satellite satellite = SatelliteFactory.createSatellite(tle);

        final SatPos satellitePosition = satellite.getPosition(GROUND_STATION,
                timeNow.toDate());

        assertThat(String.format(FORMAT_9_7F, satellitePosition.getAzimuth())).isEqualTo("3.2421950");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getElevation())).isEqualTo("0.1511580");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getLongitude())).isEqualTo("6.2069835");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getLatitude())).isEqualTo("0.5648232");
        assertThat(String.format(FORMAT_10_7F, satellitePosition.getAltitude())).isEqualTo("818.1375014");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getPhase())).isEqualTo("3.4337605");
        assertThat(String.format(FORMAT_4_0F, satellitePosition.getRange())).isEqualTo("2506");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getRangeRate())).isEqualTo("6.4832408");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getTheta())).isEqualTo("-0.9501914");
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getEclipseDepth())).isEqualTo("-0.7307717");
        assertThat(satellitePosition.isEclipsed()).isFalse();
        assertThat(satellite.willBeSeen(GROUND_STATION)).isTrue();

        List<Position> rangeCircle = satellitePosition.getRangeCircle();
        assertThat(String.format("%6.1f %6.1f", rangeCircle
                .get(0).getLat(), rangeCircle.get(0).getLon())).isEqualTo("  59.9  355.6");
        assertThat(String.format("%6.1f %6.1f", rangeCircle
                .get(89).getLat(), rangeCircle.get(89).getLon())).isEqualTo("  28.8  323.7");
        assertThat(String.format("%6.1f %6.1f", rangeCircle
                .get(179).getLat(), rangeCircle.get(179).getLon())).isEqualTo("   4.8  355.2");
        assertThat(String.format("%6.1f %6.1f", rangeCircle
                .get(269).getLat(), rangeCircle.get(269).getLon())).isEqualTo("  27.9   27.2");
    }

	@Test
	public void testWeatherSatellite() {
        timeNow = new DateTime(BASE_TIME);

        final TLE tle = new TLE(WEATHER_TLE);

        assertThat(tle.isDeepspace()).isFalse();

        final Satellite satellite = SatelliteFactory.createSatellite(tle);

        final SatPos satellitePosition = satellite.getPosition(GROUND_STATION,
                timeNow.toDate());

        assertThat(String.format(FORMAT_9_7F, satellitePosition.getAzimuth())).isEqualTo(AZIMUTH_VALUE);
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getElevation())).isEqualTo(ELEVATION_VALUE);
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getLongitude())).isEqualTo(LONGITUDE_VALUE);
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getLatitude())).isEqualTo(LATITUDE_VALUE);
        assertThat(String.format(FORMAT_10_7F, satellitePosition.getAltitude())).isEqualTo(ALTITUDE_VALUE);
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getPhase())).isEqualTo(PHASE_VALUE);
        assertThat(String.format(FORMAT_4_0F,
                Math.floor(satellitePosition.getRange()))).isEqualTo(RANGE_VALUE);
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getRangeRate())).isEqualTo(RANGE_RATE_VALUE);
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getTheta())).isEqualTo(THETA_VALUE);
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getEclipseDepth())).isEqualTo(ECLIPSE_DEPTH);
        assertThat(satellitePosition.isEclipsed()).isFalse();
        assertThat(satellite.willBeSeen(GROUND_STATION)).isTrue();

    }

	@Test
	public void testIvoAlgorithm() {
        timeNow = new DateTime(BASE_TIME);

        final TLE tle = new TLE(WEATHER_TLE);

        assertThat(tle.isDeepspace()).isFalse();

        final Satellite satellite = SatelliteFactory.createSatellite(tle);

        satellite.calculateSatelliteVectors(timeNow.toDate());

        SatPos satellitePosition = satellite.calculateSatelliteGroundTrack();

        assertThat(String.format(FORMAT_9_7F, satellitePosition.getLongitude())).isEqualTo(LONGITUDE_VALUE);
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getLatitude())).isEqualTo(LATITUDE_VALUE);
        assertThat(String.format(FORMAT_10_7F, satellitePosition.getAltitude())).isEqualTo(ALTITUDE_VALUE);
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getPhase())).isEqualTo(PHASE_VALUE);
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getTheta())).isEqualTo(THETA_VALUE);
        assertThat(satellite.willBeSeen(GROUND_STATION)).isTrue();

        satellitePosition = satellite
                .calculateSatPosForGroundStation(GROUND_STATION);

        assertThat(String.format(FORMAT_9_7F, satellitePosition.getAzimuth())).isEqualTo(AZIMUTH_VALUE);
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getElevation())).isEqualTo(ELEVATION_VALUE);
        assertThat(String.format(FORMAT_4_0F,
                Math.floor(satellitePosition.getRange()))).isEqualTo(RANGE_VALUE);
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getRangeRate())).isEqualTo(RANGE_RATE_VALUE);
        assertThat(String.format(FORMAT_9_7F, satellitePosition.getEclipseDepth())).isEqualTo(ECLIPSE_DEPTH);
        assertThat(satellitePosition.isEclipsed()).isFalse();

    }

	@Test
	public void testDeOrbitSatellite() {
        timeNow = new DateTime(BASE_TIME);

        final TLE tle = new TLE(DE_ORBIT_TLE);

        assertThat(tle.isDeepspace()).isFalse();

        final Satellite satellite = SatelliteFactory.createSatellite(tle);

        satellite.calculateSatelliteVectors(timeNow.toDate());

        final SatPos satellitePosition = satellite
                .calculateSatelliteGroundTrack();

        assertThat(String.format(FORMAT_10_7F, satellitePosition.getAltitude())).isEqualTo("57.2854215");

    }

}
