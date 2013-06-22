package info.edytor.bartek.genetics.genetic.selection;

import info.edytor.bartek.genetics.genetic.util.Colony;
import info.edytor.bartek.genetics.graph.ColorableVertex;

import java.util.*;

public class ElitismSelection extends SelectionStrategy {
    private Random rand = new Random(System.currentTimeMillis());
    private List<Colony<ColorableVertex>> tab;
    private int MAX;

    public ElitismSelection(float factor) {
        super(factor);
    }

    @Override
    public List<Colony<ColorableVertex>> before(List<Colony<ColorableVertex>> source) {
        MAX = (int)(source.size()*getFactor());

        Collections.sort(source);

        tab = new ArrayList<>(MAX);
        List<Colony<ColorableVertex>> newPop = new ArrayList<>(source.size());

        for(int i = 0; i < MAX; i++){
            tab.add(source.get(i));
            newPop.add(source.get(i));
        }

        return newPop;
    }

    @Override
    public Colony<ColorableVertex> get() {
        return tab.get(rand.nextInt(MAX));
    }
}
