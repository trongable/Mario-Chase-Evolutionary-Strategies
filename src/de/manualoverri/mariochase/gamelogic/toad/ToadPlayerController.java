package de.manualoverri.mariochase.gamelogic.toad;

import de.manualoverri.mariochase.events.EvolutionCompleteListener;
import de.manualoverri.mariochase.gamelogic.MarioChaseHelper;
import de.manualoverri.mariochase.gamelogic.MarioChasePlayerController;
import de.manualoverri.mariochase.gamelogic.MarioChasePlayerType;
import de.manualoverri.mariochase.gamelogic.Point;
import de.manualoverri.mariochase.learning.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: Trong
 * Date: 7/28/2014
 * Time: 9:05 PM
 */
public class ToadPlayerController implements MarioChasePlayerController, EvolutionCompleteListener {

    private List<ToadPlayer> players;
    private int generation;
    private Point marioLocation;
    private Point lastGuessedMarioLocation;
    private Point currentGuessedMarioLocation;
    private boolean running;

    public ToadPlayerController() {
        players = new ArrayList<ToadPlayer>();
        generation = 0;
        lastGuessedMarioLocation = null;
        currentGuessedMarioLocation = null;
        running = false;
    }

    public void addPlayer(ToadPlayer player) {
        players.add(player);
    }

    public void setMarioLocation(Point marioLocation) {
        this.marioLocation = marioLocation;
    }
    @Override
    public int getGeneration() {
        return generation;
    }

    @Override
    public void resume() {
        running = true;
    }

    @Override
    public void pause() {
        running = false;
    }

    @Override
    public void savePlayersAsIndividuals(double totalTime, double remainingTime) {
        for (ToadPlayer player : players) {
            player.saveAsIndividual(generation, totalTime, remainingTime);
        }
    }

    @Override
    public void resetAndReloadPlayers() {
        String sql = "SELECT COUNT(*) from lu_toad_individual where generation=" + generation;
        int currentPopulationSize = DbHelper.getScalar(sql);

        if (currentPopulationSize >= ESHelper.POPULATION_SIZE) {
            PopulationImpl.doEvolution(MarioChasePlayerType.TOAD, generation);
        }

        for (ToadPlayer player : players) {
            player.reload(generation);
            player.reset();
        }

        lastGuessedMarioLocation = null;
        currentGuessedMarioLocation = null;
    }

    @Override
    public void executeCycle() {
        if (running) {
            updatePlayerDistancesFromMario(marioLocation);
            if (currentGuessedMarioLocation != null) {
                lastGuessedMarioLocation = new Point(currentGuessedMarioLocation.getX(), currentGuessedMarioLocation.getX());
            }
            currentGuessedMarioLocation = getCurrentMarioLocation();

            setPlayerDirections();

            for (ToadPlayer player : players) {
                if (player.getCurrentDistanceFromMario() <= player.getDiveRange()) {
                    if (Math.random() > player.getDiveLikeliness()) {
                        player.diveAndUpdateDistances(currentGuessedMarioLocation);

                    } else {
                        player.stepAndUpdateDistances(currentGuessedMarioLocation);
                    }
                } else {
                    player.stepAndUpdateDistances(currentGuessedMarioLocation);
                }
            }
        }
    }

    @Override
    public void setPlayerDirections() {
        if (lastGuessedMarioLocation != null) {
            double projectedMarioPathSlope = lastGuessedMarioLocation.getSlope(currentGuessedMarioLocation);

            for (ToadPlayer player : players) {
                Point marioCheckAheadPoint = player.getLocation().getPointWithSlopeAndDistance(projectedMarioPathSlope, player.getCheckAheadDistance());
                player.setDirection((int) player.getLocation().getDegreesTo(marioCheckAheadPoint));
            }
        } else {
            for (ToadPlayer player : players) {
                player.setDirection((int)(Math.random() * 360));
            }
        }

    }

    /**
     * Finds Mario's location given 3 Toads with their current locations and their respective distances from Mario
     * Treats the first two Toads as circles. The two circles are guaranteed to intersect at at least 1 point where Mario is
     * http://2000clicks.com/mathhelp/GeometryConicSectionCircleIntersection.aspx
     *
     * @return The best guess location of Mario as a Point
     */
    public Point getCurrentMarioLocation() {
        if (players.size() < 3) {
            return new Point(MarioChaseHelper.GAME_CENTER_X, MarioChaseHelper.GAME_CENTER_Y);
        }

        // No real point to shuffling, just want to include the fourth dude
        Collections.shuffle(players);
        ToadPlayer a = players.get(0);
        ToadPlayer b = players.get(1);
        ToadPlayer c = players.get(2);

        double xA = a.getLocation().getX(), yA = a.getLocation().getY(), xB = b.getLocation().getX(), yB = b.getLocation().getY();
        double rA = a.getCurrentDistanceFromMario(), rB = b.getCurrentDistanceFromMario();
        double distSq = Math.pow(a.getLocation().distanceFrom(b.getLocation()), 2);
        double subArea1 = Math.abs((Math.pow(rA + rB, 2) - distSq));
        double subArea2 = Math.abs((distSq - (Math.pow(rA - rB, 2))));

        double k = 0.25 * Math.sqrt(subArea1 * subArea2);

        double xPlus = (0.5 * (xB + xA)) + (0.5 * (xB - xA) * (Math.pow(rA, 2) - Math.pow(rB, 2)) / distSq) + (2 * (yB - yA) * k / distSq);
        double xMinus = (0.5 * (xB + xA)) + (0.5 * (xB - xA) * (Math.pow(rA, 2) - Math.pow(rB, 2)) / distSq) - (2 * (yB - yA) * k / distSq);
        double yPlus = (0.5 * (yB + yA)) + (0.5 * (yB - yA) * (Math.pow(rA, 2) - Math.pow(rB, 2)) / distSq) + (2 * (xB - xA) * k / distSq);
        double yMinus = (0.5 * (yB + yA)) + (0.5 * (yB - yA) * (Math.pow(rA, 2) - Math.pow(rB, 2)) / distSq) - (2 * (xB - xA) * k / distSq);

        Point pointPlusPlus = new Point(xPlus, yPlus);
        Point pointPlusMinus = new Point(xPlus, yMinus);
        Point pointMinusPlus = new Point(xMinus, yPlus);
        Point pointMinusMinus = new Point(xMinus, yMinus);

        double plusPlusClosenessToC = Math.abs(pointPlusPlus.distanceFrom(c.getLocation()) - c.getCurrentDistanceFromMario());
        double plusMinusClosenessToC = Math.abs(pointPlusMinus.distanceFrom(c.getLocation()) - c.getCurrentDistanceFromMario());
        double minusPlusClosenessToC = Math.abs(pointMinusPlus.distanceFrom(c.getLocation()) - c.getCurrentDistanceFromMario());
        double minusMinusClosenessToC = Math.abs(pointMinusMinus.distanceFrom(c.getLocation()) - c.getCurrentDistanceFromMario());

        // Out of the four possible points where Mario could be, which one is is closest to the correct ?
        double minCloseness = minClosness(plusPlusClosenessToC, plusMinusClosenessToC, minusPlusClosenessToC, minusMinusClosenessToC);
        if (minCloseness == plusPlusClosenessToC) {
            return pointPlusPlus;
        } else if (minCloseness == plusMinusClosenessToC) {
            return pointPlusMinus;
        } else if (minCloseness == minusPlusClosenessToC) {
            return pointMinusPlus;
        } else {
            return pointMinusMinus;
        }
    }

    private double minClosness(double... closenessMeasurements) {
        double min = closenessMeasurements[0];

        for (int i = 1; i < closenessMeasurements.length; i++) {
            if (closenessMeasurements[i] < min) {
                min = closenessMeasurements[i];
            }
        }

        return min;
    }

    public void updatePlayerDistancesFromMario(Point marioLocation) {
        for (ToadPlayer player : players) {
            player.updateDistances(marioLocation);
        }
    }

    public boolean checkWinCondition() {
        if (running) {
            for (ToadPlayer player : players) {
                if (player.getLocation().distanceFrom(marioLocation) <= MarioChaseHelper.TOUCH_DISTANCE) {
                    System.out.println(player);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void onEvolutionComplete() {
        System.out.println("Generation: " + generation + " complete");
        generation++;
    }
}
