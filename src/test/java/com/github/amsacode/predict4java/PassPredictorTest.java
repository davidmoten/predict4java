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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import com.github.amsacode.predict4java.PassPredictor;
import com.github.amsacode.predict4java.SatNotFoundException;
import com.github.amsacode.predict4java.SatPassTime;
import com.github.amsacode.predict4java.SatPos;
import com.github.amsacode.predict4java.TLE;

/**
 * @author David A. B. Johnson, g4dpz
 * 
 */
public final class PassPredictorTest extends AbstractSatelliteTestBase {

	private static final String DATE_2009_01_05T04_30_00Z = "2009-01-05T04:30:00Z";
	private static final String DATE_2009_01_05T04_32_15_0000 = "2009-01-05T04:32:15+0000";
	private static final String DATE_2009_01_05T04_28_10_0000 = "2009-01-05T04:28:10+0000";
	private static final String DATE_2009_01_05T07_00_00Z = "2009-01-05T07:00:00Z";
	private static final String NORTH = "north";
	private static final String STRING_PAIR = "%s, %s";
	private static final String NONE = "none";
	private static final String SAT_NOT_FOUND_EXCEPTION_WAS_THROWN = "SatNotFoundException was thrown";
	private static final String INVALID_TLE_EXCEPTION_WAS_NOT_THROWN = "InvalidTleException was not thrown";

	@Test
	public void testIllegalArgumentsInConstructor() {
		try {
			new PassPredictor(null, null);
			fail(INVALID_TLE_EXCEPTION_WAS_NOT_THROWN);
		} catch (final IllegalArgumentException e) {
			// we expected this
		} catch (final SatNotFoundException e) {
			fail(e.getMessage());
		}

		try {
			new PassPredictor(new TLE(LEO_TLE), null);
			fail(INVALID_TLE_EXCEPTION_WAS_NOT_THROWN);
		} catch (final IllegalArgumentException e) {
			// we expected this
		} catch (final SatNotFoundException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link com.github.amsacode.predict4java.PassPredictor#nextSatPass()}
	 * .
	 */
	@Test
	public void testNextSatPass() {

        final TLE tle = new TLE(LEO_TLE);

        assertThat(!tle.isDeepspace()).isTrue();

        try {
            final PassPredictor passPredictor = new PassPredictor(tle,
                    GROUND_STATION);
            final DateTime cal = new DateTime("2009-01-05T00:00:00Z");
            SatPassTime passTime = passPredictor.nextSatPass(cal.toDate());
            assertThat(TZ_FORMAT.format(passTime.getStartTime())).isEqualTo(DATE_2009_01_05T04_28_10_0000);
            assertThat(TZ_FORMAT.format(passTime.getEndTime())).isEqualTo(DATE_2009_01_05T04_32_15_0000);
            assertThat(TZ_FORMAT.format(passTime.getTCA())).isEqualTo("2009-01-05T04:30:10+0000");
            assertThat(passTime.getPolePassed()).isEqualTo(NONE);
            assertThat(passTime.getAosAzimuth()).isEqualTo(52);
            assertThat(passTime.getLosAzimuth()).isEqualTo(84);
            assertThat(String.format("%3.1f", passTime.getMaxEl())).isEqualTo("0.9");
            assertThat(passPredictor.getDownlinkFreq(436800000L,
                    passTime.getStartTime())).isEqualTo(Long.valueOf(436802379L));
            assertThat(passPredictor.getUplinkFreq(145800000L,
                    passTime.getEndTime())).isEqualTo(Long.valueOf(145800719L));

            passTime = passPredictor.nextSatPass(passTime.getStartTime());
            assertThat(TZ_FORMAT.format(passTime.getStartTime())).isEqualTo("2009-01-05T06:04:00+0000");
            assertThat(TZ_FORMAT.format(passTime.getEndTime())).isEqualTo("2009-01-05T06:18:00+0000");
            assertThat(passTime.getPolePassed()).isEqualTo(NONE);
            assertThat(passTime.getAosAzimuth()).isEqualTo(22);
            assertThat(passTime.getLosAzimuth()).isEqualTo(158);
            assertEquals(24.42, passTime.getMaxEl(), 0.02);

            passTime = passPredictor.nextSatPass(passTime.getStartTime());
            assertThat(TZ_FORMAT.format(passTime.getStartTime())).isEqualTo("2009-01-05T07:42:45+0000");
            assertThat(TZ_FORMAT.format(passTime.getEndTime())).isEqualTo("2009-01-05T07:57:50+0000");
            assertThat(passTime.getPolePassed()).isEqualTo(NORTH);
            assertThat(passTime.getAosAzimuth()).isEqualTo(11);
            assertThat(passTime.getLosAzimuth()).isEqualTo(207);
            assertThat(String.format("%5.2f", passTime.getMaxEl())).isEqualTo("62.19");

            passTime = passPredictor.nextSatPass(passTime.getStartTime());
            assertThat(TZ_FORMAT.format(passTime.getStartTime())).isEqualTo("2009-01-05T09:22:05+0000");
            assertThat(TZ_FORMAT.format(passTime.getEndTime())).isEqualTo("2009-01-05T09:34:20+0000");
            assertThat(passTime.getPolePassed()).isEqualTo(NORTH);
            assertThat(passTime.getAosAzimuth()).isEqualTo(4);
            assertThat(passTime.getLosAzimuth()).isEqualTo(256);
            assertEquals(14.3, passTime.getMaxEl(), 0.02);

            passTime = passPredictor.nextSatPass(passTime.getStartTime());
            assertThat(TZ_FORMAT.format(passTime.getStartTime())).isEqualTo("2009-01-05T11:02:05+0000");
            assertThat(TZ_FORMAT.format(passTime.getEndTime())).isEqualTo("2009-01-05T11:07:35+0000");
            assertThat(passTime.getPolePassed()).isEqualTo(NONE);
            assertThat(passTime.getAosAzimuth()).isEqualTo(355);
            assertThat(passTime.getLosAzimuth()).isEqualTo(312);
            assertEquals(1.8, passTime.getMaxEl(), 0.05);
        } catch (final SatNotFoundException snfe) {
            fail(SAT_NOT_FOUND_EXCEPTION_WAS_THROWN);
        }
    }

	/**
	 * Test method for {@link com.github.amsacode.predict4java.PassPredictor#nextSatPass()}
	 * .
	 */
	@Test
	public void testNextSatPassWithWindBack() {

        final TLE tle = new TLE(LEO_TLE);

        assertThat(!tle.isDeepspace()).isTrue();

        try {
            final PassPredictor passPredictor = new PassPredictor(tle,
                    GROUND_STATION);
            final DateTime cal = new DateTime(DATE_2009_01_05T04_30_00Z);
            final SatPassTime passTime = passPredictor.nextSatPass(
                    cal.toDate(), true);
            assertThat(TZ_FORMAT.format(passTime.getStartTime())).isEqualTo(DATE_2009_01_05T04_28_10_0000);
            assertThat(TZ_FORMAT.format(passTime.getEndTime())).isEqualTo(DATE_2009_01_05T04_32_15_0000);
            assertThat(passTime.getPolePassed()).isEqualTo(NONE);
            assertThat(passTime.getAosAzimuth()).isEqualTo(52);
            assertThat(passTime.getLosAzimuth()).isEqualTo(84);
            assertEquals(0.9, passTime.getMaxEl(), 0.05);
            assertThat(passPredictor.getDownlinkFreq(436800000L,
                    passTime.getStartTime())).isEqualTo(Long.valueOf(436802379L));
            assertThat(passPredictor.getUplinkFreq(145800000L,
                    passTime.getEndTime())).isEqualTo(Long.valueOf(145800719L));
        } catch (final SatNotFoundException snfe) {
            fail(SAT_NOT_FOUND_EXCEPTION_WAS_THROWN);
        }
    }

	@Test
	public void correctToStringResult() {
        final TLE tle = new TLE(LEO_TLE);

        assertThat(!tle.isDeepspace()).isTrue();

        try {
            final PassPredictor passPredictor = new PassPredictor(tle,
                    GROUND_STATION);
            final DateTime cal = new DateTime(DATE_2009_01_05T04_30_00Z);
            final SatPassTime passTime = passPredictor.nextSatPass(
                    cal.toDate(), true);

            assertThat(passTime.toString()).isEqualTo("Date: January 5, 2009\n" + "Start Time: 4:28 AM\n"
                    + "Duration:  4.1 min.\n" + "AOS Azimuth: 52 deg.\n"
                    + "Max Elevation:  0.9 deg.\n" + "LOS Azimuth: 84 deg.");
        } catch (final SatNotFoundException snfe) {
            fail(SAT_NOT_FOUND_EXCEPTION_WAS_THROWN);
        }
    }

	/**
	 * test to determine if the antenna would track through a pole during a pass
	 */
	@Test
	public void poleIsPassed() {
        final TLE tle = new TLE(LEO_TLE);

        assertThat(!tle.isDeepspace()).isTrue();

        try {
            final PassPredictor passPredictor = new PassPredictor(tle,
                    GROUND_STATION);
            DateTime cal = new DateTime(DATE_2009_01_05T07_00_00Z);

            boolean northFound = false;
            boolean southFound = false;

            for (int minute = 0; minute < 60 * 24 * 7; minute++) {
                final long startTime = cal.toDate().getTime();
                if (northFound && southFound) {
                    break;
                }
                final SatPassTime passTime = passPredictor.nextSatPass(cal
                        .toDate());
                final long endTime = passTime.getEndTime().getTime();
                final String polePassed = passTime.getPolePassed();
                if (!polePassed.equals(NONE)) {
                    if (!northFound && polePassed.equals(NORTH)) {
                        assertThat(String.format(STRING_PAIR, TZ_FORMAT
                                .format(passTime.getStartTime()),
                                polePassed)).isEqualTo("2009-01-05T07:42:45+0000, north");
                        northFound = true;

                        minute += (int) ((endTime - startTime) / 60000);
                    } else if (!southFound && polePassed.equals("south")) {
                        assertThat(String.format(STRING_PAIR, TZ_FORMAT
                                .format(passTime.getStartTime()),
                                polePassed)).isEqualTo("2009-01-06T07:03:20+0000, south");
                        southFound = true;

                        minute += (int) ((endTime - startTime) / 60000);
                    }
                }

                cal = cal.plusMinutes(minute);
            }
        } catch (final SatNotFoundException snfe) {
            fail(SAT_NOT_FOUND_EXCEPTION_WAS_THROWN);
        }
    }

	@Test
	public void testGetPassList() throws SatNotFoundException {

        final TLE tle = new TLE(LEO_TLE);

        assertThat(!tle.isDeepspace()).isTrue();

        final PassPredictor passPredictor = new PassPredictor(tle,
                GROUND_STATION);
        final DateTime start = new DateTime(DATE_2009_01_05T07_00_00Z);

        final List<SatPassTime> passed = passPredictor.getPasses(
                start.toDate(), 24, false);
        assertThat(passed.size()).isEqualTo(10);
    }

	@Test
	public void testGetPassListWithWindBack() throws SatNotFoundException {

        final TLE tle = new TLE(LEO_TLE);

        assertThat(!tle.isDeepspace()).isTrue();

        final PassPredictor passPredictor = new PassPredictor(tle,
                GROUND_STATION);
        final DateTime start = new DateTime(DATE_2009_01_05T07_00_00Z);

        final List<SatPassTime> passes = passPredictor.getPasses(
                start.toDate(), 24, true);
        assertThat(passes.size()).isEqualTo(10);
        assertThat(passPredictor.getIterationCount()).isEqualTo(1039);
    }

	@Test
	public void testGetSatelliteTrack() throws Exception {

        final TLE tle = new TLE(LEO_TLE);

        assertThat(!tle.isDeepspace()).isTrue();

        final PassPredictor passPredictor = new PassPredictor(tle,
                GROUND_STATION);
        final DateTime referenceDate = new DateTime(DATE_2009_01_05T07_00_00Z);
        final int incrementSeconds = 30;
        final int minutesBefore = 50;
        final int minutesAfter = 50;
        final List<SatPos> positions = passPredictor.getPositions(
                referenceDate.toDate(), incrementSeconds, minutesBefore,
                minutesAfter);
        assertThat(positions.size()).isEqualTo(200);

    }

}
