package de.manualoverri.mariochase.gamelogic.mario;

import de.manualoverri.mariochase.gamelogic.*;

import java.awt.*;

/**
 * User: Trong
 * Date: 7/30/2014
 * Time: 10:23 PM
 */
public class MarioPlayerImpl implements MarioPlayer {

    private static int marioCount = 0;
    public final static int MAX_MAX_COUNT = 1;

    private int playerId;
    private int direction;
    private de.manualoverri.mariochase.gamelogic.Point location;
    private Color color;
    private boolean saved;

    public static MarioPlayerImpl getInstance() throws PlayerCountException {
        if (marioCount < MAX_MAX_COUNT) {
            return new MarioPlayerImpl();
        } else {
            throw new PlayerCountException("Can only have one instance of Mario per game");
        }
    }

    private MarioPlayerImpl() {
        direction = MarioChaseHelper.MARIO_STARTING_DIRECTION;
        location = MarioChaseHelper.MARIO_STARTING_LOCATION;
        color = MarioChaseHelper.MARIO_PLAYER_COLOR;
        saved = false;
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

    }

    @Override
    public void paintPlayer(Graphics g) {
        g.setColor(color);
        g.fillOval((int) location.getX(), (int) location.getY(), MarioChaseHelper.PLAYER_SIZE, MarioChaseHelper.PLAYER_SIZE);
        //g.setColor(Color.decode("0xCF4647"));
        //g.drawOval((int) location.getX(), (int) location.getY(), MarioChaseHelper.PLAYER_SIZE, MarioChaseHelper.PLAYER_SIZE);
    }
}
