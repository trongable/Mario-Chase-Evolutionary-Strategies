package de.manualoverri.mariochase.gamelogic.toad;

import de.manualoverri.mariochase.gamelogic.MarioChaseHelper;
import de.manualoverri.mariochase.gamelogic.PlayerCountException;
import de.manualoverri.mariochase.gamelogic.Point;
import de.manualoverri.mariochase.learning.DbHelper;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User: Trong
 * Date: 7/15/2014
 * Time: 11:21 PM
 */
public final class ToadPlayerImpl implements ToadPlayer {
    private static int toadCount = 0;
    private final static int DIVE_LOCK_CYCLES = 15;

    private int id;
    private double checkAheadDistance;
    private Point checkAheadPoint;
    private double diveRange;
    private double diveLikeliness;

    private int playerNumber;
    private int direction;
    private Point location;
    private Color color;
    private int currentDistanceFromMario;
    private double totalDistanceFromMario;
    private double lastDistanceFromMario;
    private double maxClosingRate;
    private double lastClosingRate;
    private double totalClosingRate;
    private int veryCloseCycles;

    private boolean diveLock;
    private int remainingDiveLockCycles;

    public static ToadPlayerImpl getInstance() throws PlayerCountException {
        if (toadCount < MarioChaseHelper.MAX_TOAD_COUNT) {
            return new ToadPlayerImpl();
        } else {
            throw new PlayerCountException("Can only have four instances of Toad per game");
        }
    }

    private ToadPlayerImpl() {
        checkAheadDistance = MarioChaseHelper.randDouble(MarioChaseHelper.MIN_CHECK_AHEAD_DISTANCE, MarioChaseHelper.MAX_CHECK_AHEAD_DISTANCE);
        diveRange = MarioChaseHelper.randDouble(MarioChaseHelper.MIN_DIVE_RANGE, MarioChaseHelper.MAX_DIVE_RANGE);
        diveLikeliness = MarioChaseHelper.randDouble(MarioChaseHelper.MIN_DIVE_LIKELINESS, MarioChaseHelper.MAX_DIVE_LIKELINESS);

        playerNumber = toadCount;
        direction = MarioChaseHelper.TOAD_STARTING_DIRECTIONS[playerNumber];
        location = new Point(MarioChaseHelper.TOAD_STARTING_LOCATIONS[playerNumber].getX(), MarioChaseHelper.TOAD_STARTING_LOCATIONS[playerNumber].getY());
        color = MarioChaseHelper.TOAD_PLAYER_COLORS[playerNumber];
        currentDistanceFromMario = MarioChaseHelper.TOAD_STARTING_DISTANCE;
        lastDistanceFromMario = MarioChaseHelper.TOAD_STARTING_DISTANCE; // TODO: check for bugs here
        totalDistanceFromMario = 0;
        lastClosingRate = 0;
        totalClosingRate = 0;

        diveLock = false;
        remainingDiveLockCycles = 0;

        toadCount++;
    }

    //region ToadPlayer Implementation

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Point getLocation() {
        return location;
    }

    @Override
    public void setLocation(Point location) {
        this.location = location;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public int getDirection() {
        return 0;
    }

    @Override
    public void setDirection(int degrees) {
        direction = degrees % 360;
    }

    @Override
    public double getCheckAheadDistance() {
        return checkAheadDistance;
    }

    @Override
    public void setCheckAheadDistance(double checkAheadDistance) {
        if (checkAheadDistance >= MarioChaseHelper.MIN_CHECK_AHEAD_DISTANCE && checkAheadDistance <= MarioChaseHelper.MAX_CHECK_AHEAD_DISTANCE) {
            this.checkAheadDistance = checkAheadDistance;
        } else {
            throw new IllegalArgumentException(String.format("Check ahead distance must be between %f and %f, check ahead distance=%f",
                    MarioChaseHelper.MIN_CHECK_AHEAD_DISTANCE, MarioChaseHelper.MAX_CHECK_AHEAD_DISTANCE, checkAheadDistance));
        }
    }

    @Override
    public Point getCheckAheadPoint() {
        return checkAheadPoint;
    }

    @Override
    public void updateCheckAheadPoint(Point marioLocation, double projectedMarioPathSlope) {
        // The check ahead point is where we'll go towards based on Mario's current location and projected path
        // It may either be ahead or behind him
        checkAheadPoint = marioLocation.getPointWithSlopeAndDistance(projectedMarioPathSlope, this.checkAheadDistance);
    }

    @Override
    public double getDiveRange() {
        return diveRange;
    }

    @Override
    public void setDiveRange(double diveRange) {
        if (diveRange >= MarioChaseHelper.MIN_DIVE_RANGE && diveRange <= MarioChaseHelper.MAX_DIVE_RANGE) {
            this.diveRange = diveRange;
        } else {
            throw new IllegalArgumentException(String.format("Dive range must be between %f and %f, dive range=%f",
                    MarioChaseHelper.MIN_DIVE_RANGE, MarioChaseHelper.MAX_DIVE_RANGE, diveRange));
        }
    }

    @Override
    public double getDiveLikeliness() {
        return diveLikeliness;
    }

    @Override
    public void setDiveLikeliness(double diveLikeliness) {
        if (diveLikeliness >= MarioChaseHelper.MIN_DIVE_LIKELINESS && diveLikeliness <= MarioChaseHelper.MAX_DIVE_LIKELINESS) {
            this.diveLikeliness = diveLikeliness;
        } else {
            throw new IllegalArgumentException(String.format("Dive likeliness must be between %f and %f, dive likeliness=%f",
                    MarioChaseHelper.MIN_DIVE_LIKELINESS, MarioChaseHelper.MAX_DIVE_LIKELINESS, diveLikeliness));
        }
    }

    private void step() {
        // 0 <= direction < 360
        if (!diveLock) {
            int xOffset = (int) (Math.cos(Math.toRadians(direction)) * MarioChaseHelper.STEP_SIZE);
            int yOffset = (int) (Math.sin(Math.toRadians(direction)) * MarioChaseHelper.STEP_SIZE);

            if (location.getX() + xOffset >= 0 && location.getX() + xOffset <= MarioChaseHelper.GAME_WIDTH) {
                location.addToX(xOffset);
            }

            if (location.getY() + yOffset >= 0 && location.getY() + yOffset <= MarioChaseHelper.GAME_HEIGHT) {
                location.addToY(yOffset);
            }
        } else {
            remainingDiveLockCycles--;
            if (remainingDiveLockCycles == 0) {
                diveLock = false;
            }
        }
    }

    @Override
    public void stepAndUpdateDistances(Point marioLocation) {
        step();
        updateDistances(marioLocation);
    }

    private void dive() {
        // When we dive, we get stuck on for a few cycles where we can't dive or step
        if (!diveLock) {
            int diveStepMultiplier = 3;

            for (int i = 0; i < diveStepMultiplier; i++) {
                step();
            }

            diveLock = true;
            remainingDiveLockCycles = DIVE_LOCK_CYCLES;
        } else {
            remainingDiveLockCycles--;
            if (remainingDiveLockCycles == 0) {
                diveLock = false;
            }
        }
    }

    @Override
    public void diveAndUpdateDistances(Point marioLocation) {
        dive();
        updateDistances(marioLocation);
    }

    @Override
    public void updateDistances(Point marioLocation) {
        lastDistanceFromMario = currentDistanceFromMario;
        currentDistanceFromMario = (int) this.location.distanceFrom(marioLocation);
        totalDistanceFromMario += currentDistanceFromMario;

        lastClosingRate = lastDistanceFromMario - currentDistanceFromMario;
        totalClosingRate += lastClosingRate;

        if (lastClosingRate > maxClosingRate) {
            maxClosingRate = lastClosingRate;
        }

        double minVeryCloseDistance = 20;
        if (currentDistanceFromMario < minVeryCloseDistance) {
            veryCloseCycles++;
        }
    }

    @Override
    public int getCurrentDistanceFromMario() {
        return currentDistanceFromMario;
    }

    @Override
    public double getAverageDistanceFromMario(double totalTime) {
        if (totalTime == 0) {
            return 0;
        }

        // We sample at every 10th of a second
        return totalDistanceFromMario / (totalTime * 10);
    }

    @Override
    public double getMaxClosingRate() {
        return maxClosingRate;
    }

    @Override
    public double getAverageClosingRate(double totalTime) {
        if (totalTime == 0) {
            return 0;
        }

        // We sample at every 10th of a second
        return totalClosingRate / (totalTime * 10);
    }

    @Override
    public int getVeryCloseCycles() {
        return veryCloseCycles;
    }

    @Override
    public void saveAsIndividual(int generation, double totalTime, double remainingTime) {
        if (currentDistanceFromMario < MarioChaseHelper.TOUCH_DISTANCE) {
            remainingTime = 0;
        }

        System.out.println(String.format("G: %d, CAD: %f, DR: %f, DL: %f, RT: %f, CDFM: %d, ADFM: %f, MCR: %f, ACR: %f, VCC: %d",
                generation,
                checkAheadDistance,
                diveRange,
                diveLikeliness,
                remainingTime,
                currentDistanceFromMario,
                getAverageDistanceFromMario(totalTime),
                maxClosingRate,
                getAverageClosingRate(totalTime),
                veryCloseCycles));

        String sql = String.format("INSERT INTO lu_toad_individual" +
                        " (generation, check_ahead_distance, dive_range, dive_likeliness, remaining_time, remaining_distance," +
                        " average_distance, max_closing_rate, average_closing_rate, very_close_cycles)" +
                        " VALUES (%d, %f, %f, %f, %f, %d, %f, %f, %f, %d)",
                generation,
                checkAheadDistance,
                diveRange,
                diveLikeliness,
                remainingTime,
                currentDistanceFromMario,
                getAverageDistanceFromMario(totalTime),
                maxClosingRate,
                getAverageClosingRate(totalTime),
                veryCloseCycles);

        DbHelper.executeUpdate(sql);
    }

    @Override
    public void reload(int generation) {
        // Grab the next available Toad player (either completely random or generated from ES)
        if (generation == 0) {
            checkAheadDistance = MarioChaseHelper.randDouble(MarioChaseHelper.MIN_CHECK_AHEAD_DISTANCE, MarioChaseHelper.MAX_CHECK_AHEAD_DISTANCE);
            diveRange = MarioChaseHelper.randDouble(MarioChaseHelper.MIN_DIVE_RANGE, MarioChaseHelper.MAX_DIVE_RANGE);
            diveLikeliness = MarioChaseHelper.randDouble(MarioChaseHelper.MIN_DIVE_LIKELINESS, MarioChaseHelper.MAX_DIVE_LIKELINESS);
        } else {
            ResultSet toadRow = DbHelper.executeQuery("select * from lu_toad_player where selected=0 and generation=" + generation + " LIMIT 1");
            try {
                if (toadRow.next()) {
                    id = toadRow.getInt("id");
                    checkAheadDistance = toadRow.getDouble("check_ahead_distance");
                    diveRange = toadRow.getDouble("dive_range");
                    diveLikeliness = toadRow.getDouble("dive_likeliness");
                    markPlayerAsSelected();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void reset() {
        direction = MarioChaseHelper.TOAD_STARTING_DIRECTIONS[playerNumber];
        location = new Point(MarioChaseHelper.TOAD_STARTING_LOCATIONS[playerNumber].getX(), MarioChaseHelper.TOAD_STARTING_LOCATIONS[playerNumber].getY());
        currentDistanceFromMario = MarioChaseHelper.TOAD_STARTING_DISTANCE;
        lastDistanceFromMario = MarioChaseHelper.TOAD_STARTING_DISTANCE;
        totalDistanceFromMario = 0;
        lastClosingRate = 0;
        totalClosingRate = 0;
        diveLock = false;
        remainingDiveLockCycles = 0;
        checkAheadPoint = new Point(this.getLocation().getX(), this.getLocation().getY());
    }

    @Override
    public void markPlayerAsSelected() {
        DbHelper.executeUpdate("update lu_toad_player set selected=1 where id=" + id);
    }

    @Override
    public void paintPlayer(Graphics g) {
        // Paint the current distance from Mario above the player and the player properties text
        g.setColor(color);
        g.drawString(String.format("%d", currentDistanceFromMario), (int) location.getX() - 2, (int) location.getY() - 2);
        g.drawString(toString(), 10, 80 + (playerNumber * 20));

        // Paint the player and the check ahead point
        g.fillOval((int) location.getX(), (int) location.getY(), MarioChaseHelper.PLAYER_SIZE, MarioChaseHelper.PLAYER_SIZE);
        if (checkAheadPoint != null) {
            g.drawOval((int) checkAheadPoint.getX(), (int) checkAheadPoint.getY(), MarioChaseHelper.PLAYER_SIZE, MarioChaseHelper.PLAYER_SIZE);
        }

        // Paint the boarder around the player
        g.setColor(Color.WHITE);
        g.drawOval((int) location.getX(), (int) location.getY(), MarioChaseHelper.PLAYER_SIZE, MarioChaseHelper.PLAYER_SIZE);
    }

    public String toString() {
        return String.format("CAD: %.2f -- DR: %.2f -- DL: %.2f", checkAheadDistance, diveRange, diveLikeliness);
    }

    //endregion

}
