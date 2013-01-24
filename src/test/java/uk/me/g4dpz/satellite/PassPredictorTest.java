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

import java.util.List;

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
public class PassPredictorTest extends AbstractSatelliteTestBase {

    private static final String DATE_2009_01_05T04_30_00Z = "2009-01-05T04:30:00Z";
    private static final String DATE_2009_01_05T04_32_15_0000 = "2009-01-05T04:32:15+0000";
    private static final String DATE_2009_01_05T04_28_10_0000 = "2009-01-05T04:28:10+0000";
    private static final String DATE_2009_01_05T07_00_00Z = "2009-01-05T07:00:00Z";
    private static final String NORTH = "north";
    private static final String STRING_PAIR = "%s, %s";
    private static final String NONE = "none";
    private static final String INVALID_TLE_EXCEPTION_WAS_THROWN = "InvalidTleException was thrown";
    private static final String SAT_NOT_FOUND_EXCEPTION_WAS_THROWN = "SatNotFoundException was thrown";
    private static final String INVALID_TLE_EXCEPTION_WAS_NOT_THROWN = "InvalidTleException was not thrown";

    public PassPredictorTest() {
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

    @Test
    public void testIllegalArgumentsInConstructor() {

        try {
            new PassPredictor(null, null);
            Assert.fail(INVALID_TLE_EXCEPTION_WAS_NOT_THROWN);
        }
        catch (final IllegalArgumentException e) {
            // we expected this
        }
        catch (final InvalidTleException e) {
            Assert.fail(e.getMessage());
        }
        catch (final SatNotFoundException e) {
            Assert.fail(e.getMessage());
        }

        try {
            new PassPredictor(new TLE(LEO_TLE), null);
            Assert.fail(INVALID_TLE_EXCEPTION_WAS_NOT_THROWN);
        }
        catch (final IllegalArgumentException e) {
            // we expected this
        }
        catch (final InvalidTleException e) {
            Assert.fail(e.getMessage());
        }
        catch (final SatNotFoundException e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test method for {@link uk.me.g4dpz.satellite.PassPredictor#nextSatPass()}.
     */
    @Test
    public final void testNextSatPass() {

        final TLE tle = new TLE(LEO_TLE);

        Assert.assertTrue(!tle.isDeepspace());

        try {
            final PassPredictor passPredictor = new PassPredictor(tle, GROUND_STATION);
            final DateTime cal = new DateTime("2009-01-05T00:00:00Z");
            SatPassTime passTime = passPredictor.nextSatPass(cal.toDate());
            Assert.assertEquals(DATE_2009_01_05T04_28_10_0000, TZ_FORMAT.format(passTime.getStartTime()));
            Assert.assertEquals(DATE_2009_01_05T04_32_15_0000, TZ_FORMAT.format(passTime.getEndTime()));
            Assert.assertEquals("2009-01-05T04:30:10+0000", TZ_FORMAT.format(passTime.getTCA()));
            Assert.assertEquals(NONE, passTime.getPolePassed());
            Assert.assertEquals(52, passTime.getAosAzimuth());
            Assert.assertEquals(84, passTime.getLosAzimuth());
            Assert.assertEquals("0.9", String.format("%3.1f", passTime.getMaxEl()));
            Assert.assertEquals(Long.valueOf(436802379L),
                    passPredictor.getDownlinkFreq(436800000L, passTime.getStartTime()));
            Assert.assertEquals(Long.valueOf(145800719L),
                    passPredictor.getUplinkFreq(145800000L, passTime.getEndTime()));

            passTime = passPredictor.nextSatPass(passTime.getStartTime());
            Assert.assertEquals("2009-01-05T06:04:00+0000", TZ_FORMAT.format(passTime.getStartTime()));
            Assert.assertEquals("2009-01-05T06:18:00+0000", TZ_FORMAT.format(passTime.getEndTime()));
            Assert.assertEquals(NONE, passTime.getPolePassed());
            Assert.assertEquals(22, passTime.getAosAzimuth());
            Assert.assertEquals(158, passTime.getLosAzimuth());
            Assert.assertEquals(24.42, passTime.getMaxEl(), 0.02);

            passTime = passPredictor.nextSatPass(passTime.getStartTime());
            Assert.assertEquals("2009-01-05T07:42:45+0000", TZ_FORMAT.format(passTime.getStartTime()));
            Assert.assertEquals("2009-01-05T07:57:50+0000", TZ_FORMAT.format(passTime.getEndTime()));
            Assert.assertEquals(NORTH, passTime.getPolePassed());
            Assert.assertEquals(11, passTime.getAosAzimuth());
            Assert.assertEquals(207, passTime.getLosAzimuth());
            Assert.assertEquals("62.19", String.format("%5.2f", passTime.getMaxEl()));

            passTime = passPredictor.nextSatPass(passTime.getStartTime());
            Assert.assertEquals("2009-01-05T09:22:05+0000", TZ_FORMAT.format(passTime.getStartTime()));
            Assert.assertEquals("2009-01-05T09:34:20+0000", TZ_FORMAT.format(passTime.getEndTime()));
            Assert.assertEquals(NORTH, passTime.getPolePassed());
            Assert.assertEquals(4, passTime.getAosAzimuth());
            Assert.assertEquals(256, passTime.getLosAzimuth());
            Assert.assertEquals(14.3, passTime.getMaxEl(), 0.02);

            passTime = passPredictor.nextSatPass(passTime.getStartTime());
            Assert.assertEquals("2009-01-05T11:02:05+0000", TZ_FORMAT.format(passTime.getStartTime()));
            Assert.assertEquals("2009-01-05T11:07:35+0000", TZ_FORMAT.format(passTime.getEndTime()));
            Assert.assertEquals(NONE, passTime.getPolePassed());
            Assert.assertEquals(355, passTime.getAosAzimuth());
            Assert.assertEquals(312, passTime.getLosAzimuth());
            Assert.assertEquals(1.8, passTime.getMaxEl(), 0.05);
        }
        catch (final InvalidTleException e) {
            Assert.fail(INVALID_TLE_EXCEPTION_WAS_THROWN);
        }
        catch (final SatNotFoundException snfe) {
            Assert.fail(SAT_NOT_FOUND_EXCEPTION_WAS_THROWN);
        }
    }

    /**
     * Test method for {@link uk.me.g4dpz.satellite.PassPredictor#nextSatPass()}.
     */
    @Test
    public final void testNextSatPassWithWindBack() {

        final TLE tle = new TLE(LEO_TLE);

        Assert.assertTrue(!tle.isDeepspace());

        try {
            final PassPredictor passPredictor = new PassPredictor(tle, GROUND_STATION);
            final DateTime cal = new DateTime(DATE_2009_01_05T04_30_00Z);
            final SatPassTime passTime = passPredictor.nextSatPass(cal.toDate(), true);
            Assert.assertEquals(DATE_2009_01_05T04_28_10_0000, TZ_FORMAT.format(passTime.getStartTime()));
            Assert.assertEquals(DATE_2009_01_05T04_32_15_0000, TZ_FORMAT.format(passTime.getEndTime()));
            Assert.assertEquals(NONE, passTime.getPolePassed());
            Assert.assertEquals(52, passTime.getAosAzimuth());
            Assert.assertEquals(84, passTime.getLosAzimuth());
            Assert.assertEquals(0.9, passTime.getMaxEl(), 0.05);
            Assert.assertEquals(Long.valueOf(436802379L),
                    passPredictor.getDownlinkFreq(436800000L, passTime.getStartTime()));
            Assert.assertEquals(Long.valueOf(145800719L),
                    passPredictor.getUplinkFreq(145800000L, passTime.getEndTime()));
        }
        catch (final InvalidTleException e) {
            Assert.fail(INVALID_TLE_EXCEPTION_WAS_THROWN);
        }
        catch (final SatNotFoundException snfe) {
            Assert.fail(SAT_NOT_FOUND_EXCEPTION_WAS_THROWN);
        }
    }

    @Test
    public void correctToStringResult() {
        final TLE tle = new TLE(LEO_TLE);

        Assert.assertTrue(!tle.isDeepspace());

        try {
            final PassPredictor passPredictor = new PassPredictor(tle, GROUND_STATION);
            final DateTime cal = new DateTime(DATE_2009_01_05T04_30_00Z);
            final SatPassTime passTime = passPredictor.nextSatPass(cal.toDate(), true);

            Assert.assertEquals("Date: January 5, 2009\n"
                        + "Start Time: 4:28 AM\n"
                        + "Duration:  4.1 min.\n"
                        + "AOS Azimuth: 52 deg.\n"
                        + "Max Elevation:  0.9 deg.\n"
                        + "LOS Azimuth: 84 deg.", passTime.toString());
        }
        catch (final InvalidTleException e) {
            Assert.fail(INVALID_TLE_EXCEPTION_WAS_THROWN);
        }
        catch (final SatNotFoundException snfe) {
            Assert.fail(SAT_NOT_FOUND_EXCEPTION_WAS_THROWN);
        }
    }

    /**
     * test to determine if the antenna would track through a pole during a pass
     */
    @Test
    public final void poleIsPassed() {
        final TLE tle = new TLE(LEO_TLE);

        Assert.assertTrue(!tle.isDeepspace());

        try {
            final PassPredictor passPredictor = new PassPredictor(tle, GROUND_STATION);
            DateTime cal = new DateTime(DATE_2009_01_05T07_00_00Z);

            boolean northFound = false;
            boolean southFound = false;

            for (int minute = 0; minute < 60 * 24 * 7; minute++) {
                final long startTime = cal.toDate().getTime();
                if (northFound && southFound) {
                    break;
                }
                final SatPassTime passTime = passPredictor.nextSatPass(cal.toDate());
                final long endTime = passTime.getEndTime().getTime();
                final String polePassed = passTime.getPolePassed();
                if (!polePassed.equals(NONE)) {
                    if (!northFound && polePassed.equals(NORTH)) {
                        Assert.assertEquals("2009-01-05T07:42:45+0000, north", String.format(STRING_PAIR,
                                TZ_FORMAT.format(passTime.getStartTime()), polePassed));
                        northFound = true;

                        minute += (int)((endTime - startTime) / 60000);
                    }
                    else if (!southFound && polePassed.equals("south")) {
                        Assert.assertEquals("2009-01-06T07:03:20+0000, south", String.format(STRING_PAIR,
                                TZ_FORMAT.format(passTime.getStartTime()), polePassed));
                        southFound = true;

                        minute += (int)((endTime - startTime) / 60000);
                    }
                }

                cal = cal.plusMinutes(minute);
            }
        }
        catch (final InvalidTleException e) {
            Assert.fail(INVALID_TLE_EXCEPTION_WAS_THROWN);
        }
        catch (final SatNotFoundException snfe) {
            Assert.fail(SAT_NOT_FOUND_EXCEPTION_WAS_THROWN);
        }
    }

    @Test
    public void testGetPassList() throws InvalidTleException, SatNotFoundException {

        final TLE tle = new TLE(LEO_TLE);

        Assert.assertTrue(!tle.isDeepspace());

        final PassPredictor passPredictor = new PassPredictor(tle, GROUND_STATION);
        final DateTime start = new DateTime(DATE_2009_01_05T07_00_00Z);

        final List<SatPassTime> passed = passPredictor.getPasses(start.toDate(), 24, false);
        Assert.assertEquals(10, passed.size());
    }

    @Test
    public void testGetPassListWithWindBack() throws InvalidTleException, SatNotFoundException {

        final TLE tle = new TLE(LEO_TLE);

        Assert.assertTrue(!tle.isDeepspace());

        final PassPredictor passPredictor = new PassPredictor(tle, GROUND_STATION);
        final DateTime start = new DateTime(DATE_2009_01_05T07_00_00Z);

        final List<SatPassTime> passes = passPredictor.getPasses(start.toDate(), 24, true);
        Assert.assertEquals(10, passes.size());
        Assert.assertEquals(1039, passPredictor.getIterationCount());
    }

    @Test
    public void testGetSatelliteTrack() throws Exception {

        final TLE tle = new TLE(LEO_TLE);

        Assert.assertTrue(!tle.isDeepspace());

        final PassPredictor passPredictor = new PassPredictor(tle, GROUND_STATION);
        final DateTime referenceDate = new DateTime(DATE_2009_01_05T07_00_00Z);
        final int incrementSeconds = 30;
        final int minutesBefore = 50;
        final int minutesAfter = 50;
        final List<SatPos> positions =
                passPredictor.getPositions(referenceDate.toDate(), incrementSeconds,
                        minutesBefore, minutesAfter);
        Assert.assertEquals(200, positions.size());

    }

}
