package info.edytor.bartek.genetics.graph;

import edu.uci.ics.jung.graph.util.Pair;
import info.edytor.bartek.genetics.genetic.util.Mutable;

/**
 * Vertex that has ability to be coloured
 */
public class ColorableVertex implements Mutable<Integer>, Comparable<ColorableVertex> {
    /**
     * Vertexes colour
     */
    private int color;
    private int ID;
    private Pair <Float> voronoiCoord;

    public ColorableVertex() {  }

    public ColorableVertex(int color, int id) {
        ID = id;
        this.color = color;
    }
    public ColorableVertex(Pair <Float> p){
        this.voronoiCoord = p;
    }
    public ColorableVertex(ColorableVertex v) {
        color = v.color;
    }

    public int getId() {
        return ID;
    }

    public void setID(int i) {
        ID = i;
    }

    /**
     * Sets color
     * @param c color
     */
    public void setColor(int c) {
        if(c < 0) throw new IllegalArgumentException("Color cannot be null");
        color = c;
    }

    /**
     * Returns colour
     * @return colour
     */
    public int getColor() {
        return color;
    }
    public Pair<Float> getVoronoiCoord(){
        return this.voronoiCoord;
    }

    @Override
    public void mutate(Integer mutator) {
        setColor(mutator);
    }

    @Override
    public int compareTo(ColorableVertex o) {
        if(ID - o.ID == 0) throw new IllegalStateException("The same ID!");
        return ID - o.ID;
    }

    @Override
    public String toString() {
        return "" + ID;
    }
}
