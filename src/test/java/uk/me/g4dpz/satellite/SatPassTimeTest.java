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

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

/**
 * @author David A. B. Johnson, g4dpz
 * 
 */
public final class SatPassTimeTest {

	private static final int PERIOD_MS = 1000;

	@Test
	public void testSatPassTime() {
		// Assert.assertTrue(TestUtil.verifyMutable(new SatPassTime(),
		// "./src/uk/me/g4dpz/satellite/SatPassTime.java", 0));
	}

	@Test
	public void testConstructor() {
		Date start = new Date();
		Date end = new Date(start.getTime() + PERIOD_MS);
		SatPassTime s = new SatPassTime(start, end, "passed", 1, 2, 3.0);
		assertEquals(start.getTime() + PERIOD_MS / 2, s.getTCA().getTime());
	}
}
