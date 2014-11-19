package de.manualoverri.mariochase.gamelogic.mario;

import de.manualoverri.mariochase.gamelogic.MarioChaseHelper;
import de.manualoverri.mariochase.gamelogic.PlayerCountException;
import de.manualoverri.mariochase.gamelogic.Point;

import java.awt.*;

/**
 * User: Trong
 * Date: 7/30/2014
 * Time: 10:23 PM
 */
public class MarioPlayerImpl implements MarioPlayer {

    private static int marioCount = 0;
    public final static int MAX_MAX_COUNT = 1;

    private int direction;
    private de.manualoverri.mariochase.gamelogic.Point location;
    private Color color;

    public static MarioPlayerImpl getInstance() throws PlayerCountException {
        if (marioCount < MAX_MAX_COUNT) {
            return new MarioPlayerImpl();
        } else {
            throw new PlayerCountException("Can only have one instance of Mario per game");
        }
    }

    private MarioPlayerImpl() {
        direction = MarioChaseHelper.MARIO_STARTING_DIRECTION;
        location = new Point(MarioChaseHelper.MARIO_STARTING_LOCATION.getX(), MarioChaseHelper.MARIO_STARTING_LOCATION.getY());
        color = MarioChaseHelper.MARIO_PLAYER_COLOR;
    }

    @Override
    public de.manualoverri.mariochase.gamelogic.Point getLocation() {
        return location;
    }

    @Override
    public void setLocation(de.manualoverri.mariochase.gamelogic.Point location) {
        this.location = location;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public int getDirection() {
        return direction;
    }

    @Override
    public void setDirection(int degrees) {
        direction = degrees % 360;
    }

    @Override
    public void step() {
        // Step in the direction we are facing
        int xOffset = (int) (Math.cos(Math.toRadians(direction)) * MarioChaseHelper.STEP_SIZE);
        int yOffset = (int) (Math.sin(Math.toRadians(direction)) * MarioChaseHelper.STEP_SIZE);

        if (location.getX() + xOffset >= 0 && location.getX() + xOffset <= MarioChaseHelper.GAME_WIDTH) {
            location.addToX(xOffset);
        }

        if (location.getY() + yOffset >= 0 && location.getY() + yOffset <= MarioChaseHelper.GAME_HEIGHT) {
            location.addToY(yOffset);
        }
    }

    @Override
    public void saveAsIndividual(int generation, double totalTime, double remainingTime) {
        // TODO: Add brains to Mario
    }

    @Override
    public void rerandomize() {

    }

    @Override
    public double getMaxWideningRate() {
        return 0;
    }

    @Override
    public double getAverageWideningRate(double time) {
        return 0;
    }

    @Override
    public int getVeryCloseCycles() {
        return 0;
    }

    @Override
    public void reset() {
        direction = MarioChaseHelper.MARIO_STARTING_DIRECTION;
        location = new Point(MarioChaseHelper.MARIO_STARTING_LOCATION.getX(), MarioChaseHelper.MARIO_STARTING_LOCATION.getY());
    }

    @Override
    public void paintPlayer(Graphics g) {
        g.setColor(color);
        g.fillOval((int) location.getX(), (int) location.getY(), MarioChaseHelper.PLAYER_SIZE, MarioChaseHelper.PLAYER_SIZE);
        g.drawOval((int) location.getX(), (int) location.getY(), MarioChaseHelper.PLAYER_SIZE, MarioChaseHelper.PLAYER_SIZE);
    }
}
