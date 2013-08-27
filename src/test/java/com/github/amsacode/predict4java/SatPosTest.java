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

import static com.github.amsacode.predict4java.TestingUtil.eq;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.github.amsacode.predict4java.Position;
import com.github.amsacode.predict4java.SatPos;

/**
 * @author David A. B. Johnson, g4dpz
 * 
 */
public final class SatPosTest {

	private static final double PRECISION = 0.00001;
	private static final double ROUNDING_PRECISION = 0.5;

	@Test
	public void testSatPos() {
		// Assert.assertTrue(TestUtil.verifyMutable(new SatPos(),
		// "./src/uk/me/g4dpz/satellite/SatPos.java", 0));
	}

	@Test
	public void footprintCalculatedCorrectly() {
		final SatPos pos = new SatPos();
		pos.setLatitude(0);
		pos.setLongitude(0);
		pos.setAltitude(1000);
		List<Position> rangeCircle = pos.getRangeCircle();
		assertTrue(eq(rangeCircle.get(0), 30, 0, ROUNDING_PRECISION));
		assertTrue(eq(rangeCircle.get(89), 1, 330, ROUNDING_PRECISION));
		assertTrue(eq(rangeCircle.get(179), -30, 359, ROUNDING_PRECISION));
		assertTrue(eq(rangeCircle.get(269), -1, 30, ROUNDING_PRECISION));

		pos.setLatitude(10.0 / 360.0 * 2.0 * Math.PI);
		pos.setLongitude(10.0 / 360.0 * 2.0 * Math.PI);
		pos.setAltitude(1000);
		rangeCircle = pos.getRangeCircle();
		assertTrue(eq(rangeCircle.get(0), 40, 10, ROUNDING_PRECISION));
		assertTrue(eq(rangeCircle.get(89), 9, 339, ROUNDING_PRECISION));
		assertTrue(eq(rangeCircle.get(179), -20, 9, ROUNDING_PRECISION));
		assertTrue(eq(rangeCircle.get(269), 8, 41, ROUNDING_PRECISION));

	}

	@Test
	public void testSatPosConstructor() {
		Date now = new Date();
		SatPos pos = new SatPos(1, 2, now);
		assertEquals(1.0, pos.getAzimuth(), PRECISION);
		assertEquals(2.0, pos.getElevation(), PRECISION);
		assertEquals(now.getTime(), pos.getTime().getTime());
	}
}
