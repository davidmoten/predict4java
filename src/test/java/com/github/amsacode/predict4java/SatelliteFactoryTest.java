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

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author David A. B. Johnson, g4dpz
 */
public final class SatelliteFactoryTest extends AbstractSatelliteTestBase {

    @Test
    public void testCreateLEOSatellite() {
        final TLE tle = new TLE(LEO_TLE);
        final Satellite satellite = SatelliteFactory.createSatellite(tle);
        assertThat(satellite).isInstanceOf(LEOSatellite.class);
    }

    @Test
    public void testCreateDeepSpaceSatellite() {
        final TLE tle = new TLE(DEEP_SPACE_TLE);
        final Satellite satellite = SatelliteFactory.createSatellite(tle);
        assertThat(satellite).isInstanceOf(DeepSpaceSatellite.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullTLE() {
        SatelliteFactory.createSatellite(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTLEWithWrongNumberOfRows() {
        final String[] theTLE = new String[0];
        final TLE tle = new TLE(theTLE);
        SatelliteFactory.createSatellite(tle);
    }

    @Test
    public void testPrivateConstructorForCoverage() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Constructor<SatelliteFactory> constructor = SatelliteFactory.class
                .getDeclaredConstructor();
        assertThat(constructor.isAccessible()).isFalse();
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
