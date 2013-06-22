package info.edytor.bartek.genetics;


import edu.uci.ics.jung.graph.Graph;
import info.edytor.bartek.genetics.genetic.Algorithm;
import info.edytor.bartek.genetics.graph.generators.GraphGenerator;
import info.edytor.bartek.genetics.gui.Gui;


/**
 *
 */
public final class AppContext {
    private static final AppContext instance = new AppContext();
    private Graph graph;
    private GraphGenerator graphGenerator;
    private Algorithm algorithm;
    private Gui mainWindow;

    private AppContext() {}

    public void setMainWindow(Gui g) {
        mainWindow = g;
    }

    public Gui getMainWindow() {
        return mainWindow;
    }

    public void drawGraph() {
        if(mainWindow == null) throw new IllegalStateException("MainWindow reference is null");
        if(graph == null) throw new IllegalStateException("Graph reference is null");
        mainWindow.drawGraph(getGraph());
    }

    public void setAlgorithm(Algorithm a) {
        algorithm = a;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setGraphGenerator(GraphGenerator g) {
        graphGenerator = g;
    }

    public GraphGenerator getGraphGenerator() {
        return graphGenerator;
    }

    public void setGraph(Graph g) {
        graph = g;
    }

    public Graph getGraph() {
        return graph;
    }

    public static AppContext getInstance() {
        return instance;
    }
}
