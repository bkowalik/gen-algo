package info.edytor.bartek.genetics.genetic.fitness;

import edu.uci.ics.jung.graph.Graph;
import info.edytor.bartek.genetics.genetic.util.Colony;
import info.edytor.bartek.genetics.graph.ColorableVertex;

import java.util.*;

/**
 * Fitness strategy that check if all neighbor of vertex has different color.
 * Vertex that has been checked are stored in Set which allows to avoid duplications.
 */
public class NeighborFitness implements FitnessStrategy {
    @Override
    public int checkFitness(Graph<ColorableVertex, ?> graph, List<ColorableVertex> gVertices, Colony<ColorableVertex> colony) {
        Collections.sort(colony, new Comparator<ColorableVertex>() {
            @Override
            public int compare(ColorableVertex o1, ColorableVertex o2) {
                return o1.getId() - o2.getId();
            }
        });

        for(int i = 0; i < gVertices.size(); i++) {
            if(gVertices.get(i).getId() != colony.get(i).getId()) throw new IllegalStateException("DUPA");
            gVertices.get(i).setColor(colony.get(i).getColor());
        }

        // Sprawdzenie czy rozwiązanie jest poprawne
        int bad = 0;
        for(ColorableVertex v : graph.getVertices()) {
            for(ColorableVertex c : graph.getNeighbors(v)) {
                if((c.getColor() == v.getColor()) && (c != v)) bad++;
            }
        }

        return bad;
    }
}
