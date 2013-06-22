package info.edytor.bartek.genetics.genetic.selection;


import info.edytor.bartek.genetics.genetic.util.Colony;
import info.edytor.bartek.genetics.graph.ColorableVertex;

import java.util.List;

public abstract class SelectionStrategy {
    private final float factor;

    public SelectionStrategy(float factor) {
        this.factor = factor;
    }

    public abstract List<Colony<ColorableVertex>> before(List<Colony<ColorableVertex>> source);

    public abstract Colony<ColorableVertex> get();

    public float getFactor() {
        return factor;
    }
}
