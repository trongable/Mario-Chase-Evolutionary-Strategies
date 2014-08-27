package de.manualoverri.mariochase.unittest;

import de.manualoverri.mariochase.gamelogic.Point;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PointTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testEqualsWithPrecision() throws Exception {
        double precision = 5;

        Point marioLocation = new Point(0, 0);
        assertTrue(marioLocation.equalsWithPrecision(new Point(0, 0.000001), precision));
        assertTrue(marioLocation.equalsWithPrecision(new Point(0.000001, 0), precision));
        assertTrue(marioLocation.equalsWithPrecision(new Point(0.000001, 0.000001), precision));
        assertFalse(marioLocation.equalsWithPrecision(new Point(0, 0.00001), precision));
        assertFalse(marioLocation.equalsWithPrecision(new Point(0.00001, 0), precision));
        assertFalse(marioLocation.equalsWithPrecision(new Point(0.00001, 0.00001), precision));

    }
}