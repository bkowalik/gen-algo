package info.edytor.bartek.genetics;


import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import info.edytor.bartek.genetics.graph.ColorableVertex;
import info.edytor.bartek.genetics.gui.Gui;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static final Logger logger = Logger.getLogger(Main.class);
    static {
        BasicConfigurator.configure();
        logger.setLevel(Level.DEBUG);
    }

    public static void main(final String[] args) {
        try {
            graphicInterface();
        } catch(Throwable e) {
            JOptionPane.showMessageDialog(null, "Fatal error!", "Wyjątek", JOptionPane.ERROR_MESSAGE, null);
        }
    }

    protected static void graphicInterface() {
        final AppContext context = AppContext.getInstance();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Gui g = new Gui();
                g.setVisible(true);
                context.setMainWindow(g);
            }
        });
    }

    public static Graph<ColorableVertex, Object> simpleGraph() {
        //  Generowanie grafu
        Graph<ColorableVertex, Object> graph = new SparseMultigraph<>();
        List<ColorableVertex> vertices = new ArrayList<>(5);
        Random rand = new Random(System.currentTimeMillis());

        //  Dodanie krawędzi
        for(int i = 0; i < 5; i++) vertices.add(new ColorableVertex());
        graph.addEdge(1, vertices.get(0), vertices.get(1));
        graph.addEdge(2, vertices.get(1), vertices.get(4));
        graph.addEdge(3, vertices.get(2), vertices.get(3));
        graph.addEdge(4, vertices.get(3), vertices.get(0));
        graph.addEdge(5, vertices.get(4), vertices.get(2));
        graph.addEdge(6, vertices.get(3), vertices.get(4));
        graph.addEdge(7, vertices.get(2), vertices.get(0));

        graph.addEdge(8, vertices.get(2), vertices.get(2));

        return graph;
    }
}
