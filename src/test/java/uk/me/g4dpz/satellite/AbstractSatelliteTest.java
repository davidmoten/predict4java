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
