package info.edytor.bartek.genetics.graph.generators;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import info.edytor.bartek.genetics.graph.ColorableVertex;

/**
 * Generates size-clique
 */
public class CliqueGenerator implements GraphGenerator {
    private Graph<ColorableVertex, Object> graph;
    private final int size;

    /**
     *
     * @param size clique size
     */
    public CliqueGenerator(int size) {
        this.size = size;
    }

    @Override
    public Graph<ColorableVertex, Object> generate() {
        if(graph != null) return graph;

        graph = new SparseMultigraph<>();
        ColorableVertex[] vertices = new ColorableVertex[size];

        for(int i = 0; i < size; i++) {
            vertices[i] = new ColorableVertex();
            graph.addVertex(vertices[i]);
        }

        int k = 0;
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if((vertices[i] != vertices[j]) && (graph.findEdge(vertices[i], vertices[j]) == null)) {
                    graph.addEdge(k, vertices[i], vertices[j]);
                    k++;
                }
            }
        }

        return graph;
    }
}
