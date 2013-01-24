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
public final class GroundStationPositionTest {

    private static final double HEIGHT_AMSL = 3.0;
    private static final double LONGITUDE = 2.0;
    private static final double LATITUDE = 1.0;
    private static final double THETA = 4.0;

    /**
     * Default Constructor.
     */
    public GroundStationPositionTest() {
    }

    @Test
    public void testDefaultConstructorAndSetters() {

        final GroundStationPosition groundStationPosition = new GroundStationPosition();

        final int[] elevations = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1,
                2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2,
                3, 4, 5};
        groundStationPosition.setHorizonElevations(elevations);
        groundStationPosition.setTheta(4.0);

        final int[] oElevations = groundStationPosition.getHorizonElevations();

        Assert.assertEquals(elevations.length, oElevations.length);

        for (int i = 0; i < elevations.length; i++) {
            Assert.assertEquals(elevations[i], oElevations[i]);
        }

        Assert.assertTrue(Math.abs(THETA - groundStationPosition.getTheta()) < 0.000001);
    }

    @Test
    public void testConstructionUsingAttributes() {

        final GroundStationPosition groundStationPosition = new GroundStationPosition(LATITUDE, LONGITUDE, HEIGHT_AMSL);
        Assert.assertTrue(Math.abs(LATITUDE - groundStationPosition.getLatitude()) < 0.000001);
        Assert.assertTrue(Math.abs(LONGITUDE - groundStationPosition.getLongitude()) < 0.000001);
        Assert.assertTrue(Math.abs(HEIGHT_AMSL - groundStationPosition.getHeightAMSL()) < 0.000001);

    }

    @Test
    public void testSettingWrongNumberOfElevationsCausesException() {

        final GroundStationPosition groundStationPosition = new GroundStationPosition();

        final int[] elevations = new int[] {0, 1};

        try {
            groundStationPosition.setHorizonElevations(elevations);
            Assert.fail("IllegalArgumentException expected");
        }
        catch (final IllegalArgumentException iae) {
            Assert.assertEquals(String.format("Expected 36 Horizon Elevations, got: %d", elevations.length),
                    iae.getMessage());
        }

    }
}
