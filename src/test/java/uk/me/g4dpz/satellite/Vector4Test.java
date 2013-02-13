package uk.me.g4dpz.satellite;

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
