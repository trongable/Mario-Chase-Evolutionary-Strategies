package de.manualoverri.mariochase.unittest;

import de.manualoverri.mariochase.gamelogic.*;
import de.manualoverri.mariochase.gamelogic.toad.ToadPlayer;
import de.manualoverri.mariochase.gamelogic.toad.ToadPlayerController;
import de.manualoverri.mariochase.gamelogic.toad.ToadPlayerImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ToadPlayerControllerTest {
    ToadPlayerController toadPlayerController;
    ToadPlayer t1, t2, t3, t4;

    @Before
    public void setUp() throws Exception {
        toadPlayerController = new ToadPlayerController();
        t1 = ToadPlayerImpl.getInstance();
        t2 = ToadPlayerImpl.getInstance();
        t3 = ToadPlayerImpl.getInstance();
        t4 = ToadPlayerImpl.getInstance();
        toadPlayerController.addPlayer(t1);
        toadPlayerController.addPlayer(t2);
        toadPlayerController.addPlayer(t3);
        toadPlayerController.addPlayer(t4);
    }

    @Test
    public void testAddPlayer() throws Exception {

    }

    @Test
    public void testStart() throws Exception {

    }

    @Test
    public void testPause() throws Exception {

    }

    @Test
    public void testSave() throws Exception {

    }

    @Test
    public void testReset() throws Exception {

    }

    @Test
    public void testExecuteCycle() throws Exception {

    }

    @Test
    public void testSetPlayerDirections() throws Exception {

    }

    @Test
    public void testGetCurrentMarioLocation() throws Exception {
        double precision = 5;
        Point marioLocation = new Point(8, 10);

        // Standard situation
        t1.setLocation(new Point(-5, 4));
        t2.setLocation(new Point(17, 45));
        t3.setLocation(new Point(-8, -10));
        t4.setLocation(new Point(25, -12));

        t1.updateDistances(marioLocation);
        t2.updateDistances(marioLocation);
        t3.updateDistances(marioLocation);
        t4.updateDistances(marioLocation);
        assertTrue(marioLocation.equalsWithPrecision(toadPlayerController.getCurrentMarioLocation(), precision));

        marioLocation = new Point(0, 0);
        t1.setLocation(new Point(100, 0));
        t2.setLocation(new Point(-100, 0));
        t3.setLocation(new Point(10, 10));
        t4.setLocation(new Point(0, 100));

        t1.updateDistances(marioLocation);
        t2.updateDistances(marioLocation);
        t3.updateDistances(marioLocation);
        t4.updateDistances(marioLocation);
        assertTrue(marioLocation.equalsWithPrecision(toadPlayerController.getCurrentMarioLocation(), precision));

        // Mario X, Y = 0
        marioLocation = new Point(0, 0);
        t1.updateDistances(marioLocation);
        t2.updateDistances(marioLocation);
        t3.updateDistances(marioLocation);
        t4.updateDistances(marioLocation);
        assertTrue(marioLocation.equalsWithPrecision(toadPlayerController.getCurrentMarioLocation(), precision));

        marioLocation = new Point(0, 0);
        t1.setLocation(new Point(0, 0));
        t1.updateDistances(marioLocation);
        t2.updateDistances(marioLocation);
        t3.updateDistances(marioLocation);
        t4.updateDistances(marioLocation);
        assertTrue(marioLocation.equalsWithPrecision(toadPlayerController.getCurrentMarioLocation(), precision));

        marioLocation = new Point(0, 0);
        t1.setLocation(new Point(0, 0));
        t1.updateDistances(marioLocation);
        t2.setLocation(new Point(0, 0));
        t2.updateDistances(marioLocation);
        t3.setLocation(new Point(0, 0));
        t3.updateDistances(marioLocation);
        t4.setLocation(new Point(0, 0));
        t4.updateDistances(marioLocation);
        assertTrue(marioLocation.equalsWithPrecision(toadPlayerController.getCurrentMarioLocation(), precision));
    }
}