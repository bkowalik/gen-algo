package info.edytor.bartek.genetics.genetic.util;

/**
 * Enables mutations
 */
public interface Mutable<T> {
    /**
     * Causes mutation
     */
    void mutate(T mutator);
}
