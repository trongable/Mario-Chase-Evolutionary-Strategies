package de.manualoverri.mariochase.gamelogic.toad;


import de.manualoverri.mariochase.gamelogic.Point;

import java.awt.*;

/**
 * User: Trong
 * Date: 7/20/2014
 * Time: 2:59 PM
 */
public interface ToadPlayer {

    public Point getLocation();

    public void setLocation(Point location);

    public Color getColor();

    public int getDirection();

    public void setDirection(int degrees);

    public double getCheckAheadDistance();

    public void setCheckAheadDistance(double checkAheadDistance);

    public Point getCheckAheadPoint();

    public void updateCheckAheadPoint(Point marioLocation, double projectedMarioPathSlope);

    public double getDiveRange();

    public void setDiveRange(double diveRange);

    public double getDiveLikeliness();

    public void setDiveLikeliness(double diveLikeliness);

    public void stepAndUpdateDistances(Point marioLocation);

    public void diveAndUpdateDistances(Point marioLocation);

    public void updateDistances(Point marioLocation);

    public int getCurrentDistanceFromMario();

    public double getAverageDistanceFromMario(double totalTime);

    public double getMaxClosingRate();

    public double getAverageClosingRate(double totalTime);

    public int getVeryCloseCycles();

    public void saveAsIndividual(int generation, double totalTime, double remainingTime);

    public void reload(int generation);

    public void reset();

    public void markPlayerAsSelected();

    public void paintPlayer(Graphics g);

}
