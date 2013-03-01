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
 */package uk.me.g4dpz.satellite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import uk.me.g4dpz.satellite.AbstractSatellite.Vector4;

public class Vector4Test {

    private static final double PRECISION = 0.00001;

    @Test
    public void testSubtract() {
        Vector4 v1 = new AbstractSatellite.Vector4(1, 2, 3, 4);
        Vector4 v2 = new AbstractSatellite.Vector4(0.1, 0.2, 0.3, 0.4);
        Vector4 v3 = v1.subtract(v2);
        assertEquals(0.9, v3.getW(), PRECISION);
        assertEquals(1.8, v3.getX(), PRECISION);
        assertEquals(2.7, v3.getY(), PRECISION);
        assertEquals(3.6, v3.getZ(), PRECISION);

    }

    @Test
    public void testToString() {
        Vector4 v1 = new AbstractSatellite.Vector4(1, 2, 3, 4);
        assertNotNull(v1.toString());
    }

}
