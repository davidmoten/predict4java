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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

/**
 * @author David A. B. Johnson, g4dpz
 */
public final class TLETest extends AbstractSatelliteTestBase {

    private static final String VALUE_0_0000 = "0.0000";
    private static final String VALUE_0_0084159 = "0.0084159";
    private static final String FORMAT_6_4F = "%6.4f";
    private static final String TLELINE_3 = "2 28375  98.0821 101.6821 0084935  88.2048 272.8868 14.40599338194363";
    private static final String FORMAT_9_7F = "%9.7f";
    private static final String FORMAT_10_7F = "%10.7f";
    private static final String FORMAT_11_7F = "%11.7f";
    private static final String AO_51_NAME = "AO-51 [+]";

    @Test
    public void testTLEReadLEO() {
        final TLE tle = new TLE(LEO_TLE);
        checkData(tle);
    }

    @Test
    public void testCopyConstructor() {
        final TLE tle = new TLE(LEO_TLE);
        final TLE tleCopy = new TLE(tle);
        checkData(tleCopy);
    }

    @Test
    public void testTLEReadDeepSpace() {
        final String[] theTLE = {
                "AO-40",
                "1 26609U 00072B   00326.22269097 -.00000581  00000-0  00000+0 0    29",
                "2 26609   6.4279 245.5626 7344055 179.5891 182.1915  2.03421959   104"};
        final TLE tle = new TLE(theTLE);
        assertTrue("Satellite should have been DeepSpace", tle.isDeepspace());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForNullDataInTLE() {
        final String[] theTLE = {AO_51_NAME, null, TLELINE_3};
        new TLE(theTLE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForBlankDataInTLE() {
        final String[] theTLE = {AO_51_NAME, "", TLELINE_3};
        new TLE(theTLE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForNoDataInTLE() {
        final String[] theTLE = new String[0];
        new TLE(theTLE);
    }

    @Test
    public void testLoadFromResource() throws IOException {
        InputStream is = TLETest.class.getResourceAsStream("/LEO.txt");
        final List<TLE> tles = TLE.importSat(is);
        assertThat(1 == tles.size()).isTrue();
        checkData(tles.get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullArrayPassedToTLEConstructorThrowsIllegalArgumentException() {
        new TLE((String[]) null);
    }

    private void checkData(final TLE tle) {
        assertThat(tle.getName()).isEqualTo(AO_51_NAME);
        assertThat(tle.toString()).isEqualTo(AO_51_NAME);
        assertThat(tle.getCatnum()).isEqualTo(28375);
        assertThat(tle.getSetnum()).isEqualTo(364);
        assertThat(tle.getYear()).isEqualTo(9);
        assertThat(String.format(FORMAT_11_7F, tle.getRefepoch())).isEqualTo("105.6639197");
        assertThat(String.format(FORMAT_10_7F, tle.getIncl())).isEqualTo("98.0551000");
        assertThat(String.format(FORMAT_11_7F, tle.getRaan())).isEqualTo("118.9086000");
        assertThat(String.format(FORMAT_9_7F, tle.getEccn())).isEqualTo(VALUE_0_0084159);
        assertThat(String.format(FORMAT_10_7F, tle.getArgper())).isEqualTo("315.8041000");
        assertThat(String.format(FORMAT_11_7F, tle.getMeanan())).isEqualTo(" 43.6444000");
        assertThat(String.format(FORMAT_10_7F, tle.getMeanmo())).isEqualTo("14.4063845");
        assertThat(String.format(FORMAT_6_4F, tle.getDrag())).isEqualTo(VALUE_0_0000);
        assertThat(String.format(FORMAT_6_4F, tle.getNddot6())).isEqualTo(VALUE_0_0000);
        assertThat(String.format(FORMAT_9_7F, tle.getBstar())).isEqualTo("0.0000138");
        assertThat(tle.getOrbitnum()).isEqualTo(25195);
        assertThat(String.format("%12.7f", tle.getEpoch())).isEqualTo("9105.6639197");
        assertThat(String.format(FORMAT_9_7F, tle.getXndt2o())).isEqualTo("0.0000000");
        assertThat(String.format(FORMAT_9_7F, tle.getXincl())).isEqualTo("1.7113843");
        assertThat(String.format(FORMAT_9_7F, tle.getXnodeo())).isEqualTo("2.0753466");
        assertThat(String.format(FORMAT_9_7F, tle.getEo())).isEqualTo(VALUE_0_0084159);
        assertThat(String.format(FORMAT_9_7F, tle.getOmegao())).isEqualTo("5.5118213");
        assertThat(String.format(FORMAT_9_7F, tle.getXmo())).isEqualTo("0.7617385");
        assertThat(String.format("%8.6f", tle.getXno())).isEqualTo("0.062860");
        assertThat(tle.isDeepspace()).isFalse();
    }

}
