package info.edytor.bartek.genetics.graph.dimacs;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import info.edytor.bartek.genetics.graph.ColorableVertex;

import java.io.*;
import java.util.ArrayList;

public class Loader {
    private final String fileName;
    private final Graph<ColorableVertex, Object> graph;
    private ArrayList<ColorableVertex> vertices;

    public Loader(String file) {
        fileName = file;
        graph = new SparseMultigraph<>();
    }

    public Graph load() throws IOException {
        if(graph.getVertexCount() != 0) return null;
        File file = new File(fileName);

        FileReader fileReader = null;
        BufferedReader reader = null;
        try {
            fileReader = new FileReader(file);
            reader = new BufferedReader(fileReader);
            String line = null;
            while((line = reader.readLine()) != null) {
                processLine(line, graph);
            }
        } finally {
            if(reader != null) reader.close();
            if(fileReader != null) fileReader.close();
        }

        return graph;
    }

    public Graph getGraph() {
        return graph;
    }

    protected void processLine(String line, Graph<ColorableVertex, Object> g) {
        if(line.charAt(0) == 'c') return;
        else if(line.charAt(0) == 'p') {
            String[] arr = line.split(" ");
            if(arr.length != 4) throw new RuntimeException("Illegal line " + line);

            int ver = Integer.parseInt(arr[2]);
            vertices = new ArrayList<>(ver);

            ColorableVertex v = null;
            for(int i = 0; i < ver; i++) {
                v = new ColorableVertex(0, i);
                graph.addVertex(v);
                vertices.add(v);
            }
        } else if(line.charAt(0) == 'e') {
            String[] arr = line.split(" ");
            if(arr.length != 3) throw new RuntimeException("Illegal line" + line);

            graph.addEdge(new Object(), vertices.get(Integer.parseInt(arr[1])-1), vertices.get(Integer.parseInt(arr[2])-1));
        } else {
            throw new RuntimeException("Illegal character " + line);
        }
    }
}
