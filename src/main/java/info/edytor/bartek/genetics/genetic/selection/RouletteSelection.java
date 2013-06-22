package info.edytor.bartek.genetics.genetic.selection;

import info.edytor.bartek.genetics.genetic.util.Colony;
import info.edytor.bartek.genetics.graph.ColorableVertex;

import java.util.*;

public class RouletteSelection extends SelectionStrategy {
    private Random rand = new Random(new Date().getTime());
    private float[] probabilityMap;
    private List<Colony<ColorableVertex>> source;
    private int sum;

    public RouletteSelection(float factor) {
        super(factor);// Factor is irrelevant in this strategy
    }

    @Override
    public List<Colony<ColorableVertex>> before(List<Colony<ColorableVertex>> source) {
        if(source == null) throw new IllegalStateException("Initialization has not been performed.");
        this.source = source;
        probabilityMap = new float[source.size()];

        Collections.sort(source);

        probabilityMap[0] =  adjust(source.get(0).getFitness());
        for(int i = 1; i < source.size(); i++) {
            probabilityMap[i] = adjust(source.get(i).getFitness()) + probabilityMap[i-1];
        }

        List<Colony<ColorableVertex>> newPop = new ArrayList<>(source.size());
        for(int i = 0; i < (int)(source.size()*getFactor()); i++) {
            newPop.add(get());
        }

        return newPop;
    }

    protected final float adjust(float fitness) {
        return fitness == 0 ? Float.POSITIVE_INFINITY : 1 / fitness;
    }

    @Override
    public Colony<ColorableVertex> get() {
        float num = rand.nextFloat()*probabilityMap[probabilityMap.length-1];
//        int index = Arrays.binarySearch(probabilityMap, num);
//        if(index < 0) {
//            index = Math.abs(index+1);
//        }
        float sum = adjust(source.get(0).getFitness());
        for(int i = 0; i < source.size(); i++) {
            if(sum >= num) {
                return source.get(i);
            }
            sum += adjust(source.get(i).getFitness());
        }

        throw new IllegalStateException("Niestety :/");
    }
}
