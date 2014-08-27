package de.manualoverri.mariochase.learning;

import de.manualoverri.mariochase.gamelogic.MarioChasePlayerType;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: Trong
 * Date: 7/20/2014
 * Time: 2:58 PM
 */
public class PopulationImpl implements Population {
    private List<Individual> population;
    private List<EvolutionCompleteListener> listeners;
    private MarioChasePlayerType playerType;

    public static Population getFromPlayerTypeAndGeneration(MarioChasePlayerType playerType, int generation) {
        ResultSet rs;
        if (playerType == MarioChasePlayerType.MARIO) {
            rs = DbHelper.executeQuery(String.format("select * from lu_mario_individual where generation=%d", generation));
        } else {
            rs = DbHelper.executeQuery(String.format("select * from lu_toad_individual where generation=%d", generation));
        }

        List<Individual> population = new ArrayList<Individual>(ESHelper.POPULATION_SIZE);
        try {
            while (rs.next()) {
                if (playerType == MarioChasePlayerType.MARIO) {
                    // do nothing... yet
                } else {
                    population.add(ToadIndividual.getFromRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new PopulationImpl(playerType, population);
    }

    private PopulationImpl(MarioChasePlayerType playerType, List<Individual> population) {
        this.playerType = playerType;
        this.population = population;
        listeners = new ArrayList<EvolutionCompleteListener>();
    }

    @Override
    public List<Individual> selectRandomIndividuals(int n, boolean withReplacement) {
        if (n < 0) {
            throw new IllegalArgumentException("Attempted to select a negative amount of individuals");
        }

        if (n > population.size()) {
            throw new IllegalArgumentException("Attempted to select more individuals than available, population size=" + population.size() + ", n=" + n);
        }

        // TODO: Implement withReplacement check
        List<Individual> result = new ArrayList<Individual>(n + 1);
        for (int i = 0; i < n; i++) {
            result.add(population.get((int) (Math.random() * n)));
        }

        return result;
    }

    @Override
    public void evaluateFitnesses() {
        for (Individual i : population) {
            i.evaulateFitness();
        }
    }

    @Override
    public Individual crossoverOnePoint(Individual a, Individual b) {
        return null;
    }

    @Override
    public Individual crossoverTwoPoint(Individual a, Individual b) {
        return null;
    }

    @Override
    public Individual crossoverUniform(Individual a, Individual b) {
        return null;
    }

    @Override
    public void evolveMuLambda(int numParents, int numChildren, double mean, double variance) {
        throw new NotImplementedException();
    }

    @Override
    public void evolveMuPlusLambda(int numParents, int numChildren, double mean, double variance) {
        if (numParents < 0 || numChildren < 0) {
            throw new IllegalArgumentException("Attempted to select a negative number of individuals");
        }

        if (numParents > population.size() || numChildren > population.size()) {
            throw new IllegalArgumentException("Attempted to select more individuals than available, population size=" + population.size());
        }

        /*
        if (numChildren + numParents != population.size()) {
            throw new IllegalArgumentException(", c=" + numChildren + ", p=" + numParents);
        }
        */

        if (numChildren % numParents != 0) {
            throw new IllegalArgumentException("The number of children must be evenly divisible by the number of parents, c=" + numChildren + ", p=" + numParents);
        }

        evaluateFitnesses();
        Collections.sort(population, new IndividualComparator());

        List<Individual> nextGenerationParents = new ArrayList<Individual>(numParents + numChildren);
        List<Individual> nextGenerationPopulation = new ArrayList<Individual>(numParents + numChildren);
        for (int i = 0; i < numParents; i++) {
            nextGenerationParents.add(population.get(i));
            nextGenerationPopulation.add(population.get(i));
        }

        for (Individual individual : nextGenerationParents) {
            for (int i = 0; i < numChildren / numParents; i++) {
                nextGenerationPopulation.add(individual.mutateNondestructive(mean, variance));
            }
        }

        population = nextGenerationPopulation;
    }

    @Override
    public void evolveGeneticAlgorithm() {
        throw new NotImplementedException();
    }

    @Override
    public void saveIndividualsAsPlayersInDb() {
        for (Individual individual : population) {
            individual.saveAsPlayerInDb();
        }

        notifyEvolutionComplete();
    }

    @Override
    public void addListener(EvolutionCompleteListener listener) {
        listeners.add(listener);
    }

    private void notifyEvolutionComplete() {
        for (EvolutionCompleteListener listener : listeners) {
            listener.onEvolutionComplete();
        }
    }
}
