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

import org.junit.Test;

public class AbstractSatelliteTest {

    private static final double PRECISION = 0.00001;

    @Test
    public void testJulianDateFromEpochConversionGivenYearIn20thCentury() {
        assertEquals(AbstractSatellite.julianDateOfYear(1990), AbstractSatellite.juliandDateOfEpoch(90000), PRECISION);
    }

    @Test
    public void testModulusGivenNegativeFirstArg() {
        assertEquals(9.4, AbstractSatellite.modulus(-10.6, 10), PRECISION);
    }

    @Test
    public void testModulusGivenNegativeSecondArg() {
        // werd behaviour for modulus if you ask me
        assertEquals(-17.0, AbstractSatellite.modulus(23, -10), PRECISION);
    }
}
