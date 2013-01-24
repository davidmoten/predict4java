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
package uk.me.g4dpz.satellite;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author David A. B. Johnson, g4dpz
 * 
 */
public class LEOSatelliteTest extends AbstractSatelliteTestBase {

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

    public LEOSatelliteTest() {

    }

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link uk.me.g4dpz.satellite.LEOSatellite#LEOSatellite(uk.me.g4dpz.satellite.TLE)}.
     */
    @Test
    public final void testLEOSatellite() {

        timeNow = new DateTime("2009-04-17T06:57:32Z");

        final TLE tle = new TLE(LEO_TLE);

        Assert.assertFalse(tle.isDeepspace());

        final Satellite satellite = SatelliteFactory.createSatellite(tle);

        final SatPos satellitePosition = satellite.getPosition(GROUND_STATION, timeNow.toDate());

        Assert.assertEquals("3.2421950", String.format(FORMAT_9_7F, satellitePosition.getAzimuth()));
        Assert.assertEquals("0.1511580", String.format(FORMAT_9_7F, satellitePosition.getElevation()));
        Assert.assertEquals("6.2069835", String.format(FORMAT_9_7F, satellitePosition.getLongitude()));
        Assert.assertEquals("0.5648232", String.format(FORMAT_9_7F, satellitePosition.getLatitude()));
        Assert.assertEquals("818.1375014", String.format(FORMAT_10_7F, satellitePosition.getAltitude()));
        Assert.assertEquals("3.4337605", String.format(FORMAT_9_7F, satellitePosition.getPhase()));
        Assert.assertEquals("2506", String.format(FORMAT_4_0F, satellitePosition.getRange()));
        Assert.assertEquals("6.4832408", String.format(FORMAT_9_7F, satellitePosition.getRangeRate()));
        Assert.assertEquals("-0.9501914", String.format(FORMAT_9_7F, satellitePosition.getTheta()));
        Assert.assertEquals("-0.7307717", String.format(FORMAT_9_7F, satellitePosition.getEclipseDepth()));
        Assert.assertFalse(satellitePosition.isEclipsed());
        Assert.assertTrue(satellite.willBeSeen(GROUND_STATION));
        
        double[][] rangeCircle = satellitePosition.getRangeCircle();
        Assert.assertEquals("  59.9  355.6", String.format("%6.1f %6.1f", rangeCircle[0][0], rangeCircle[0][1]));
        Assert.assertEquals("  28.8  323.8", String.format("%6.1f %6.1f", rangeCircle[89][0], rangeCircle[89][1]));
        Assert.assertEquals("   4.8  355.2", String.format("%6.1f %6.1f", rangeCircle[179][0], rangeCircle[179][1]));
        Assert.assertEquals("  27.9   27.2", String.format("%6.1f %6.1f", rangeCircle[269][0], rangeCircle[269][1]));
    }

    @Test
    public final void testWeatherSatellite() {

        timeNow = new DateTime(BASE_TIME);

        final TLE tle = new TLE(WEATHER_TLE);

        Assert.assertFalse(tle.isDeepspace());

        final Satellite satellite = SatelliteFactory.createSatellite(tle);

        final SatPos satellitePosition = satellite.getPosition(GROUND_STATION, timeNow.toDate());

        Assert.assertEquals(AZIMUTH_VALUE, String.format(FORMAT_9_7F, satellitePosition.getAzimuth()));
        Assert.assertEquals(ELEVATION_VALUE, String.format(FORMAT_9_7F, satellitePosition.getElevation()));
        Assert.assertEquals(LONGITUDE_VALUE, String.format(FORMAT_9_7F, satellitePosition.getLongitude()));
        Assert.assertEquals(LATITUDE_VALUE, String.format(FORMAT_9_7F, satellitePosition.getLatitude()));
        Assert.assertEquals(ALTITUDE_VALUE, String.format(FORMAT_10_7F, satellitePosition.getAltitude()));
        Assert.assertEquals(PHASE_VALUE, String.format(FORMAT_9_7F, satellitePosition.getPhase()));
        Assert.assertEquals(RANGE_VALUE, String.format(FORMAT_4_0F, Math.floor(satellitePosition.getRange())));
        Assert.assertEquals(RANGE_RATE_VALUE, String.format(FORMAT_9_7F, satellitePosition.getRangeRate()));
        Assert.assertEquals(THETA_VALUE, String.format(FORMAT_9_7F, satellitePosition.getTheta()));
        Assert.assertEquals(ECLIPSE_DEPTH, String.format(FORMAT_9_7F, satellitePosition.getEclipseDepth()));
        Assert.assertFalse(satellitePosition.isEclipsed());
        Assert.assertTrue(satellite.willBeSeen(GROUND_STATION));
        
    }

    @Test
    public final void testIvoAlgorithm() {

        timeNow = new DateTime(BASE_TIME);

        final TLE tle = new TLE(WEATHER_TLE);

        Assert.assertFalse(tle.isDeepspace());

        final Satellite satellite = SatelliteFactory.createSatellite(tle);

        satellite.calculateSatelliteVectors(timeNow.toDate());

        SatPos satellitePosition = satellite.calculateSatelliteGroundTrack();

        Assert.assertEquals(LONGITUDE_VALUE, String.format(FORMAT_9_7F, satellitePosition.getLongitude()));
        Assert.assertEquals(LATITUDE_VALUE, String.format(FORMAT_9_7F, satellitePosition.getLatitude()));
        Assert.assertEquals(ALTITUDE_VALUE, String.format(FORMAT_10_7F, satellitePosition.getAltitude()));
        Assert.assertEquals(PHASE_VALUE, String.format(FORMAT_9_7F, satellitePosition.getPhase()));
        Assert.assertEquals(THETA_VALUE, String.format(FORMAT_9_7F, satellitePosition.getTheta()));
        Assert.assertTrue(satellite.willBeSeen(GROUND_STATION));

        satellitePosition = satellite.calculateSatPosForGroundStation(GROUND_STATION);

        Assert.assertEquals(AZIMUTH_VALUE, String.format(FORMAT_9_7F, satellitePosition.getAzimuth()));
        Assert.assertEquals(ELEVATION_VALUE, String.format(FORMAT_9_7F, satellitePosition.getElevation()));
        Assert.assertEquals(RANGE_VALUE, String.format(FORMAT_4_0F, Math.floor(satellitePosition.getRange())));
        Assert.assertEquals(RANGE_RATE_VALUE, String.format(FORMAT_9_7F, satellitePosition.getRangeRate()));
        Assert.assertEquals(ECLIPSE_DEPTH, String.format(FORMAT_9_7F, satellitePosition.getEclipseDepth()));
        Assert.assertFalse(satellitePosition.isEclipsed());

    }

    @Test
    public final void testDeOrbitSatellite() {

        timeNow = new DateTime(BASE_TIME);

        final TLE tle = new TLE(DE_ORBIT_TLE);

        Assert.assertFalse(tle.isDeepspace());

        final Satellite satellite = SatelliteFactory.createSatellite(tle);

        satellite.calculateSatelliteVectors(timeNow.toDate());

        final SatPos satellitePosition = satellite.calculateSatelliteGroundTrack();

        Assert.assertEquals("57.2854215", String.format(FORMAT_10_7F, satellitePosition.getAltitude()));

    }
}
