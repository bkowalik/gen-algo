package info.edytor.bartek.genetics.genetic.fitness;

import edu.uci.ics.jung.graph.Graph;
import info.edytor.bartek.genetics.genetic.util.Colony;
import info.edytor.bartek.genetics.graph.ColorableVertex;

import java.util.List;

/**
 * Strategy interface that allows user to switch strategy at minimum cost.
 */
public interface FitnessStrategy {

    int checkFitness(Graph<ColorableVertex, ?> graph, List<ColorableVertex> gVertices, Colony<ColorableVertex> colony);

    /**
     * Bare in mind! Default Fitness Strategy is pure test purpose.
     */
    public static class DefaultFS implements FitnessStrategy {
        @Override
        public int checkFitness(Graph<ColorableVertex, ?> graph, List<ColorableVertex> gVertices, Colony<ColorableVertex> colony) {
            throw new UnsupportedOperationException("Method unimplemented");
        }
    }
}
