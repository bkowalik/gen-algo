package info.edytor.bartek.genetics.graph.generators;

/**
 * megamu.mesh packege was downloaded from http://www.leebyron.com/else/mesh/
 * (and slightly modified, mainly by adding some moethods)
 *
 * @author Adam Sędziwy
 */

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import info.edytor.bartek.genetics.graph.ColorableVertex;
import megamu.mesh.Voronoi;


import java.util.ArrayList;
import java.util.Random;

public class PlanarGenerator implements GraphGenerator {

    private final int size;
    private final boolean directed;

    public PlanarGenerator(int vertexNumber, boolean directed){
         this.size = vertexNumber;
        this.directed = directed;
    }

    public PlanarGenerator(int vertexNumber){
        this.size = vertexNumber;
        this.directed = false;
    }


    private int resize(int vertexNumber){
        // ustalanie rozmiaru okna - rozwiązanie dość nieelegackie
        int W=0;
        if(vertexNumber <= 8){
            W = 30;
        } else if(vertexNumber > 8 && vertexNumber <=16 ){
            W = 40;
        } else if(vertexNumber> 16 && vertexNumber <=32){
            W = 50;
        } else if(vertexNumber > 32 && vertexNumber <=50){
            W = 60;
        } else if(vertexNumber > 50 && vertexNumber <=72){
            W = 70;
        } else if(vertexNumber >72 && vertexNumber <=96){
            W = 80;
        } else if(vertexNumber>96 && vertexNumber <= 124){
            W = 90;
        } else if(vertexNumber>124 && vertexNumber <=163){
            W = 100;
        } else if(vertexNumber>163 && vertexNumber <= 200){
            W = 110;
        } else if (vertexNumber > 200 && vertexNumber <= 244 ){
            W = 120;
        } else if (vertexNumber>244 && vertexNumber <= 300){
            W = 130;
        } else {
            W = 200;
            // w ten sposób można uzyskać 700 wierzchołków. Dalej moim zdaniem nie ma sensu.
        }
        return W;
    }

    public Graph<ColorableVertex, Object> generate(){
        Voronoi myVoronoi = null;
        ArrayList<ColorableVertex> vertexSet = null;
        Graph<ColorableVertex, Object> planarGraph = null;
        int maxVertex = 0;
        int W = this.resize(this.size);
        int H = W;
        int X = W / 10 ;
        int Y = H / 10 ;
        int previous_iteration = 0;

        Random r = new Random(System.currentTimeMillis());
        float[][] points = new float[X * Y][2];

        int i = 0;

        for (int ix = 0; ix < X; ix++) {

            for (int iy = 0; iy < Y; iy++) {

                points[i][0] = (100F * ix + r.nextInt(100)) / 10;
                points[i][1] = (100F * iy + r.nextInt(200)) / 10;
                i++;

            }

        }
        myVoronoi = new Voronoi(points);
        if(this.directed == false)  {
            planarGraph = new UndirectedSparseGraph<>();
        }else{
            planarGraph = new DirectedSparseGraph<>();
        }
        vertexSet = new ArrayList<>();
        float[][] edges = myVoronoi.getEdges();
        do{
            for (int j = 0; j < edges.length ; j++) {

                //Keep all vertices within the window W x H
                if (edges[j][0] > W || edges[j][1] > H || edges[j][2] > W
                        || edges[j][3] > H || edges[j][0] < 0 || edges[j][1] < 0
                        || edges[j][2] < 0 || edges[j][3] < 0)
                    continue;
                Pair<Float> vc1 = new Pair<>(edges[j][0], edges[j][1]);
                Pair<Float> vc2 = new Pair<>(edges[j][2], edges[j][3]);

                ColorableVertex v1 = null;
                ColorableVertex v2 = null;

                if(maxVertex == 0){
                    maxVertex = 2;
                    v1 = new ColorableVertex(vc1);
                    v2 = new ColorableVertex(vc2);
                    vertexSet.add(v1);
                    vertexSet.add(v2);
                }

                for(int g = 0; g<vertexSet.size();g++) {
                    if (vertexSet.get(g).getVoronoiCoord().equals(vc1)) v1 = vertexSet.get(g);
                    if (vertexSet.get(g).getVoronoiCoord().equals(vc2)) v2 = vertexSet.get(g);
                }

                if(v1 == null && v2 != null && maxVertex < this.size){
                    v1 = new ColorableVertex(vc1);
                    vertexSet.add(v1);
                    maxVertex ++; }

                if(v2 == null && v1 !=null && maxVertex < this.size){
                    v2 = new ColorableVertex(vc2);
                    vertexSet.add(v2);
                    maxVertex++;}
                if( v1 != null && v2 != null ){
                    if(this.directed == false)
                      planarGraph.addEdge(new Object(), v1, v2); else
                      planarGraph.addEdge(new Object(),v1,v2,EdgeType.DIRECTED)  ;
                }

            }

            if(previous_iteration == maxVertex){
                W = W+10;
                H = W;
            }

            previous_iteration = maxVertex;


        }while (maxVertex<this.size);

        return planarGraph;
    }




}
