package de.manualoverri.mariochase.gamelogic;

import java.awt.*;
import java.util.Random;

/**
 * User: Trong
 * Date: 7/21/2014
 * Time: 9:52 PM
 */
public final class MarioChaseHelper {

    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 600;
    public static final int GAME_CENTER_X = GAME_WIDTH / 2;
    public static final int GAME_CENTER_Y = GAME_HEIGHT / 2;
    public static final int GAME_LENGTH_MS = (int) (90 * 1000);
    public static final int CYCLE_INTERVAL_MS = (int) (0.1 * 1000);
    public static final int STEP_SIZE = 5;
    public static final int PLAYER_SIZE = 10;
    public static final int TOUCH_DISTANCE = 8;

    // Mario default properties
    public static final int MARIO_STARTING_DIRECTION = 0;
    public static final Point MARIO_STARTING_LOCATION = new Point(GAME_CENTER_X, GAME_CENTER_Y);
    public static final Color MARIO_PLAYER_COLOR = Color.WHITE;
    public static final int MARIO_START_AHEAD_MS = (int) (-10 * 1000);

    // Toad default properties
    public static final int MAX_TOAD_COUNT = 4;
    public static final int[] TOAD_STARTING_DIRECTIONS = {180, 270, 0, 90};
    public static final int TOAD_STARTING_DISTANCE = 50;
    public static final Point[] TOAD_STARTING_LOCATIONS = {new Point(GAME_CENTER_X, GAME_CENTER_Y - TOAD_STARTING_DISTANCE),
            new Point(GAME_CENTER_X + TOAD_STARTING_DISTANCE, GAME_CENTER_Y),
            new Point(GAME_CENTER_X, GAME_CENTER_Y + TOAD_STARTING_DISTANCE),
            new Point(GAME_CENTER_X - TOAD_STARTING_DISTANCE, GAME_CENTER_Y)};
    public static final Color[] TOAD_PLAYER_COLORS = {Color.decode("0xCF4647"), Color.decode("0x49D6FC"), Color.decode("0x51B594"), Color.decode("0xFFDB63")};

    // Min and max values that Toad player properties can have
    public static final double MAX_CHECK_AHEAD_DISTANCE = 500;
    public static final double MIN_CHECK_AHEAD_DISTANCE = -500;
    public static final double MAX_DIVE_RANGE = 500;
    public static final double MIN_DIVE_RANGE = 0;
    public static final double MAX_DIVE_LIKELINESS = 1;
    public static final double MIN_DIVE_LIKELINESS = 0;

    public static Random random = new Random(System.currentTimeMillis());

    public static double randDouble(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    public MarioChaseHelper() {
        // Do nothing
    }

}
