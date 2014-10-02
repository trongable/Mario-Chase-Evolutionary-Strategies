package de.manualoverri.mariochase.learning;

import de.manualoverri.mariochase.events.EvolutionCompleteListener;

import java.util.List;

/**
 * User: Trong
 * Date: 7/20/2014
 * Time: 2:03 PM
 */
public interface Population {
    public List<Individual> selectRandomIndividuals(int n, boolean withReplacement);

    public void evaluateFitnesses();

    public Individual crossoverOnePoint(Individual a, Individual b);

    public Individual crossoverTwoPoint(Individual a, Individual b);

    public Individual crossoverUniform(Individual a, Individual b);

    public void evolveMuLambda(int numParents, int numChildren, double mean, double variance);

    public void evolveMuPlusLambda(int numParents, int numChildren, double mean, double mutationRate);

    public void evolveGeneticAlgorithm();

    public void saveIndividualsAsPlayersInDb();

    public void addListener(EvolutionCompleteListener listener);

}
