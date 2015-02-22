package de.manualoverri.mariochase.learning;

import de.manualoverri.mariochase.gamelogic.MarioChaseHelper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User: Trong
 * Date: 7/21/2014
 * Time: 10:04 PM
 */
public class ToadIndividual implements Individual {

    private int id;
    private int generation;
    private double checkAheadDistance;
    private double diveRange;
    private double diveLikeliness;
    private double remainingTime;
    private double remainingDistance;
    private double averageDistance;
    private double maxClosingRate;
    private double averageClosingRate;
    private int veryCloseCycles;
    private double fitnessScore;

    public static Individual getFromRow(ResultSet row) throws SQLException {
        ToadIndividual retval = new ToadIndividual();

        retval.id = row.getInt("id");
        retval.generation = row.getInt("generation");
        retval.checkAheadDistance = row.getDouble("check_ahead_distance");
        retval.diveRange = row.getDouble("dive_range");
        retval.diveLikeliness = row.getDouble("dive_likeliness");
        retval.remainingTime = row.getDouble("remaining_time");
        retval.remainingDistance = row.getDouble("remaining_distance");
        retval.averageDistance = row.getDouble("average_distance");
        retval.maxClosingRate = row.getDouble("max_closing_rate");
        retval.averageClosingRate = row.getDouble("average_closing_rate");
        retval.veryCloseCycles = row.getInt("very_close_cycles");

        return retval;
    }

    private ToadIndividual() {
        // do nothing
    }

    @Override
    public double getFitnessScore() {
        return fitnessScore;
    }

    @Override
    public double evaluateFitness() {
        fitnessScore = (remainingTime * 0.01) +
                (remainingDistance * 0.01) +
                (averageDistance * 1) +
                (maxClosingRate * 1) +
                (averageClosingRate * 100) +
                (veryCloseCycles * 0.1);

        String sql = "UPDATE lu_toad_individual SET fitness=" + fitnessScore + " where id=" + id;
        DbHelper.executeUpdate(sql);

        return fitnessScore;
    }

    @Override
    public Individual mutateNondestructive(double mean, double variance) {
        // Clone before doing a destructive mutation
        ToadIndividual copy = new ToadIndividual();

        copy.generation = this.generation;
        copy.checkAheadDistance = this.checkAheadDistance;
        copy.diveRange = this.diveRange;
        copy.diveLikeliness = this.diveLikeliness;
        copy.mutateDestructive(mean, variance);

        return copy;
    }

    @Override
    public void mutateDestructive(double mean, double variance) {
        // Algorithm 11
        // Mutate the current individual to have slightly different properties
        double oldCAD = checkAheadDistance;
        checkAheadDistance = convolveValue(checkAheadDistance, MarioChaseHelper.MIN_CHECK_AHEAD_DISTANCE, MarioChaseHelper.MAX_CHECK_AHEAD_DISTANCE, mean, variance);
        diveRange = convolveValue(diveRange, MarioChaseHelper.MIN_DIVE_RANGE, MarioChaseHelper.MAX_DIVE_RANGE, mean, variance);
        diveLikeliness = convolveValue(diveLikeliness, MarioChaseHelper.MIN_DIVE_LIKELINESS, MarioChaseHelper.MAX_DIVE_LIKELINESS, mean, variance);
    }

    @Override
    public double generateGaussianRandom(double mean, double variance) {
        if (variance < 0) {
            throw new IllegalArgumentException("Variance should be > 0, variance=" + variance);
        }

        return mean + (Math.sqrt(variance) * MarioChaseHelper.random.nextGaussian());
    }

    @Override
    public double convolveValue(double value, double min, double max, double mean, double variance) {
        // Do Gaussian convolution to slightly change the individual's properties
        double newValue = value + generateGaussianRandom(mean, variance);

        while (newValue < min || newValue > max) {
            newValue = value + generateGaussianRandom(mean, variance);
        }

        return newValue;
    }

    @Override
    public void saveAsPlayerInDb() {
        String sql = String.format("INSERT INTO lu_toad_player (generation, check_ahead_distance, dive_range, dive_likeliness)" +
                        " values (%d, %f, %f, %f)",
                generation + 1,
                checkAheadDistance,
                diveRange,
                diveLikeliness);
        DbHelper.executeUpdate(sql);
    }
}
