package de.manualoverri.mariochase.learning;

import de.manualoverri.mariochase.gamelogic.PlayerType;

/**
 * User: Trong
 * Date: 7/20/2014
 * Time: 2:06 PM
 */
public interface Individual {

    public double getFitnessScore();

    public double evaulateFitness();

    public Individual mutateNondestructive(double mean, double variance);

    public void mutateDestructive(double mean, double variance);

    public double generateGaussianRandom(double mean, double variance);

    public double convolveValue(double value, double min, double max, double mean, double variance);

    public void saveAsPlayerInDb();

}
