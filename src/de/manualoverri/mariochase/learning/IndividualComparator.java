package de.manualoverri.mariochase.learning;

import java.util.Comparator;

/**
 * User: Trong
 * Date: 7/22/2014
 * Time: 12:09 AM
 */
public class IndividualComparator implements Comparator<Individual> {
    @Override
    public int compare(Individual o1, Individual o2) {
        if (o1.getFitnessScore() < o2.getFitnessScore()) {
            return -1;
        }

        if (o1.getFitnessScore() > o2.getFitnessScore()) {
            return 1;
        }

        return 0;
    }
}
