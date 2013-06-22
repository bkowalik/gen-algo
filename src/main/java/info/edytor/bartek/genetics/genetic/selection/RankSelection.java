package info.edytor.bartek.genetics.genetic.selection;

import info.edytor.bartek.genetics.genetic.util.Colony;
import info.edytor.bartek.genetics.graph.ColorableVertex;

import java.util.List;

public class RankSelection extends SelectionStrategy {

    public RankSelection(float factor) {
        super(factor);
    }

    @Override
    public List<Colony<ColorableVertex>> before(List<Colony<ColorableVertex>> source) {
        throw new UnsupportedOperationException("Method unimplemented");
    }

    @Override
    public Colony<ColorableVertex> get() {
        throw new UnsupportedOperationException("Method unimplemented");
    }
}
