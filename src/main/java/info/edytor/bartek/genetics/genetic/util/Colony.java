package info.edytor.bartek.genetics.genetic.util;

import java.util.ArrayList;
import java.util.Collection;


public class Colony<E> extends ArrayList<E> implements Fitnessable, Comparable<Colony<E>> {
    private boolean mutated;
    private int fitness;

    public Colony(int initialCapacity) {
        super(initialCapacity);
    }

    public Colony() {
        super();
    }

    public Colony(Collection<? extends E> c) {
        super(c);
    }

    public int getFitness() {
        return fitness;
    }

    public void mutate() {
        if(mutated) throw new IllegalStateException("Cannot mutate twice");
        mutated = true;
    }

    public boolean isMutated() {
        return mutated;
    }

    public void setFitness(int fitness) {
        if(fitness < 0) throw new IllegalArgumentException("Fitness cannot be negative");
        this.fitness = fitness;
    }

    @Override
    public int compareTo(Colony<E> es) {
        return fitness - es.fitness;
    }
}
