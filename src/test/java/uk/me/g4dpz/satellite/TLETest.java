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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author David A. B. Johnson, g4dpz
 * 
 */
public final class TLETest extends AbstractSatelliteTestBase {

    private static final String VALUE_0_0000 = "0.0000";
    private static final String VALUE_0_0084159 = "0.0084159";
    private static final String FORMAT_6_4F = "%6.4f";
    private static final String ILLEGALARGUMENTEXCEPTION_SHOULDHAVEBEEN_THROWN =
            "IllegalArgumentException should have been thrown";
    private static final String TLELINE_3 = "2 28375  98.0821 101.6821 0084935  88.2048 272.8868 14.40599338194363";
    private static final String FORMAT_9_7F = "%9.7f";
    private static final String FORMAT_10_7F = "%10.7f";
    private static final String FORMAT_11_7F = "%11.7f";
    private static final String AO_51_NAME = "AO-51 [+]";

    /**
     * Default Constructor.
     */
    public TLETest() {
    }

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

        Assert.assertTrue("Satellite should have been DeepSpace", tle.isDeepspace());
    }

    @Test
    public void testForNullDataInTLE() {
        try {
            final String[] theTLE = {AO_51_NAME, null,
                    TLELINE_3};

            new TLE(theTLE);
            Assert.fail(ILLEGALARGUMENTEXCEPTION_SHOULDHAVEBEEN_THROWN);
        }
        catch (final IllegalArgumentException iae) {
            // This is what we expected
        }
    }

    @Test
    public void testForBlankDataInTLE() {
        try {
            final String[] theTLE = {AO_51_NAME, "",
                    TLELINE_3};

            new TLE(theTLE);
            Assert.fail(ILLEGALARGUMENTEXCEPTION_SHOULDHAVEBEEN_THROWN);
        }
        catch (final IllegalArgumentException iae) {
            // This is what we expected
        }
    }

    @Test
    public void testForNoDataInTLE() {
        try {
            final String[] theTLE = new String[0];

            new TLE(theTLE);
            Assert.fail(ILLEGALARGUMENTEXCEPTION_SHOULDHAVEBEEN_THROWN);
        }
        catch (final IllegalArgumentException iae) {
            // This is what we expected
        }
    }

    @Test
    public void testLoadFromResource() {

        InputStream is;
        try {
            is = TLETest.class.getResourceAsStream("/LEO.txt");

            final List<TLE> tles = TLE.importSat(is);

            Assert.assertTrue(1 == tles.size());

            checkData(tles.get(0));
        }
        catch (final IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    private void checkData(final TLE tle) {

        Assert.assertEquals(AO_51_NAME, tle.getName());
        Assert.assertEquals(AO_51_NAME, tle.toString());
        Assert.assertEquals(28375, tle.getCatnum());
        Assert.assertEquals(364, tle.getSetnum());
        Assert.assertEquals(9, tle.getYear());
        Assert.assertEquals("105.6639197", String.format(FORMAT_11_7F, tle.getRefepoch()));
        Assert.assertEquals("98.0551000", String.format(FORMAT_10_7F, tle.getIncl()));
        Assert.assertEquals("118.9086000", String.format(FORMAT_11_7F, tle.getRaan()));
        Assert.assertEquals(VALUE_0_0084159, String.format(FORMAT_9_7F, tle.getEccn()));
        Assert.assertEquals("315.8041000", String.format(FORMAT_10_7F, tle.getArgper()));
        Assert.assertEquals(" 43.6444000", String.format(FORMAT_11_7F, tle.getMeanan()));
        Assert.assertEquals("14.4063845", String.format(FORMAT_10_7F, tle.getMeanmo()));
        Assert.assertEquals(VALUE_0_0000, String.format(FORMAT_6_4F, tle.getDrag()));
        Assert.assertEquals(VALUE_0_0000, String.format(FORMAT_6_4F, tle.getNddot6()));
        Assert.assertEquals("0.0000138", String.format(FORMAT_9_7F, tle.getBstar()));
        Assert.assertEquals(25195, tle.getOrbitnum());
        Assert.assertEquals("9105.6639197", String.format("%12.7f", tle.getEpoch()));
        Assert.assertEquals("0.0000000", String.format(FORMAT_9_7F, tle.getXndt2o()));
        Assert.assertEquals("1.7113843", String.format(FORMAT_9_7F, tle.getXincl()));
        Assert.assertEquals("2.0753466", String.format(FORMAT_9_7F, tle.getXnodeo()));
        Assert.assertEquals(VALUE_0_0084159, String.format(FORMAT_9_7F, tle.getEo()));
        Assert.assertEquals("5.5118213", String.format(FORMAT_9_7F, tle.getOmegao()));
        Assert.assertEquals("0.7617385", String.format(FORMAT_9_7F, tle.getXmo()));
        Assert.assertEquals("0.062860", String.format("%8.6f", tle.getXno()));
        Assert.assertFalse(tle.isDeepspace());
    }
}
