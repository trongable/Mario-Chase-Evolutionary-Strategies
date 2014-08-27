package de.manualoverri.mariochase.learning;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User: Trong
 * Date: 7/21/2014
 * Time: 10:04 PM
 */
public class ToadIndividual implements Individual {

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

    private double nexGaussian;
    private boolean hasNextGaussian;

    public static Individual getFromRow(ResultSet row) throws SQLException {
        ToadIndividual retval = new ToadIndividual();

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
        // Do nothing
    }

    @Override
    public double getFitnessScore() {
        return fitnessScore;
    }

    @Override
    public double evaulateFitness() {
        fitnessScore = (remainingTime * 1) +
                (remainingDistance * 1) +
                (averageDistance * 1) +
                (averageClosingRate * 1) +
                (maxClosingRate * 1) +
                (averageClosingRate * 1) +
                (veryCloseCycles * 1);

        return fitnessScore;
    }

    @Override
    public Individual mutateNondestructive(double mean, double variance) {
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
    }

    @Override
    /***
     * Using Wikipedia implementation for Marsaglia polar method
     */
    public double generateGaussianRandom(double mean, double variance) {
        double u, v, s;

        if (hasNextGaussian) {
            hasNextGaussian = false;
            return nexGaussian;
        }

        do {
            u = Math.random() * 2 - 1;
            v = Math.random() * 2 - 1;
            s = (u * u) + (v * v);
        } while (s >= 1 || s == 0);

        double muller = Math.sqrt(-2.0 * Math.log(s) / s);
        nexGaussian = v * muller;
        hasNextGaussian = true;

        return mean + variance * u * muller;
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
