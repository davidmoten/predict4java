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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author David A. B. Johnson, g4dpz
 * 
 */
public class SatelliteFactoryTest extends AbstractSatelliteTestBase {

    private static final String SHOULD_HAVE_THROWN_ILLEGAL_ARGUMENT_EXCEPTION =
            "Should have thrown IllegalArgument Exception";

    public SatelliteFactoryTest() {
    }

    @Test
    public void testCreateLEOSatellite() {

        final TLE tle = new TLE(LEO_TLE);

        final Satellite satellite = SatelliteFactory.createSatellite(tle);

        Assert.assertTrue(satellite instanceof LEOSatellite);
    }

    @Test
    public void testCreateDeepSpaceSatellite() {

        final TLE tle = new TLE(DEEP_SPACE_TLE);

        final Satellite satellite = SatelliteFactory.createSatellite(tle);

        Assert.assertTrue(satellite instanceof DeepSpaceSatellite);
    }

    @Test
    public void testNullTLE() {
        try {
            SatelliteFactory.createSatellite(null);
            Assert.fail(SHOULD_HAVE_THROWN_ILLEGAL_ARGUMENT_EXCEPTION);
        }
        catch (final IllegalArgumentException iae) {
            // we expected this
        }
    }

    @Test
    public void testTLEWithWrongNumberOfRows() {
        try {
            final String[] theTLE = new String[0];

            final TLE tle = new TLE(theTLE);

            SatelliteFactory.createSatellite(tle);

            Assert.fail(SHOULD_HAVE_THROWN_ILLEGAL_ARGUMENT_EXCEPTION);
        }
        catch (final IllegalArgumentException iae) {
            // we expected this
        }
    }
}
