package info.edytor.bartek.genetics.graph.generators;

import edu.uci.ics.jung.graph.Graph;
import info.edytor.bartek.genetics.graph.ColorableVertex;

public interface GraphGenerator {
    Graph<ColorableVertex, Object> generate();
}
