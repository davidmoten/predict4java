package uk.me.g4dpz.satellite;

import org.junit.Test;

public class SatNotFoundExceptionTest {

    @Test
    public void testInstantiation() {
        new SatNotFoundException("boo");
    }

}
