package de.manualoverri.mariochase.gamelogic.mario;

import de.manualoverri.mariochase.gamelogic.Point;

import java.awt.*;

/**
 * User: Trong
 * Date: 7/20/2014
 * Time: 3:00 PM
 */
public interface MarioPlayer {

    public Point getLocation();

    public void setLocation(Point location);

    public Color getColor();

    public int getDirection();

    public void setDirection(int degrees);

    public void step();

    public void saveAsIndividual(int generation, double totalTime, double remainingTime);

    public void rerandomize();

    public double getMaxWideningRate();

    public double getAverageWideningRate(double time);

    public int getVeryCloseCycles();

    public void reset();

    public void paintPlayer(Graphics g);
}
