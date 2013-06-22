package info.edytor.bartek.genetics.genetic;

import edu.uci.ics.jung.graph.Graph;
import info.edytor.bartek.genetics.genetic.fitness.FitnessStrategy;
import info.edytor.bartek.genetics.genetic.selection.SelectionStrategy;
import info.edytor.bartek.genetics.genetic.util.Colony;
import info.edytor.bartek.genetics.genetic.util.GenerationInfo;
import info.edytor.bartek.genetics.graph.ColorableVertex;
import org.apache.log4j.Logger;


import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Genetics algorithm to colour graph vertices
 */
public class GeneticsAlgorithm extends Algorithm {
    private static final Logger logger = Logger.getLogger(GeneticsAlgorithm.class);

    /**
     * Random number generator
     */
    protected static final Random rand = new Random(System.currentTimeMillis());

    /**
     * Graph population to get best solution
     */
    private List<Colony<ColorableVertex>> population;

    /**
     * Unique colours list
     */
    private List<Integer> colors;

    /**
     * Original graph to work with
     */
    protected Graph<ColorableVertex, ?> graph;

    /**
     * Fitness strategy to check quality of solutions
     */
    private final FitnessStrategy fitnessStrategy;

    /**
     * Determines how to nex population derives from previous one
     */
    private final SelectionStrategy selectionStrategy;

    private final int maxSteps;

    private final int toCrossing;

    /**
     * Genetics algorithm work result
     */
    protected Colony<ColorableVertex> solution;

    /**
     *
     */
    protected float mutation;

    private int generations;

    private final ArrayList<ColorableVertex> graphVertices;

    /**
     *
     */
    private final BlockingQueue<GenerationInfo<Integer>> generationInfos = new LinkedBlockingQueue<>();

    private final BlockingQueue<Colony<ColorableVertex>> randToDemo = new LinkedBlockingQueue<>(5);

    /**
     *
     * @param graph
     * @param fitness
     * @param popSize
     */
    protected GeneticsAlgorithm(Graph<ColorableVertex, ?> graph, FitnessStrategy fitness, SelectionStrategy selection, int popSize, float mutation, int maxSteps, int toCrossing, int maxColors) {
        fitnessStrategy = fitness;
        selectionStrategy = selection;
        this.graph = graph;
        this.mutation = mutation;
        this.maxSteps = maxSteps;
        this.toCrossing = toCrossing;
        graphVertices = new ArrayList<>(graph.getVertexCount());
        if(maxColors <= 0) maxColors = maxVertexLevel(graph)+1;
        colors = initColorSet(maxColors);
        initPopulation(popSize);
    }

    public GeneticsAlgorithm(GenAlgoBuilder builder) {
        this(
                builder.graph, builder.fitnessStrategy,
                builder.selectionStrategy, builder.popSize,
                builder.mutationRate, builder.maxSteps, builder.toCrossing, builder.maxColors
        );
    }

    /**
     * Initializes population
     */
    protected void initPopulation(int popSize) {
        population = new ArrayList<>(popSize);
        int gVertices = graph.getVertexCount();

        int id = 0;
        for(ColorableVertex v : graph.getVertices()) {
            v.setID(id);
            graphVertices.add(v);
            id++;
        }

        for(int i = 0; i < popSize; i++) {
            Colony<ColorableVertex> list = new Colony<>(gVertices);
            for(int j = 0; j < gVertices; j++)
                list.add(new ColorableVertex(colors.get(rand.nextInt(colors.size())), j));

            list.setFitness(fitnessStrategy.checkFitness(graph, graphVertices, list));
            population.add(list);
        }

        int min = gVertices-1;
        int max = 0;
        int sum = 0;
        for(Colony<ColorableVertex> colony : population) {
            if(min > colony.getFitness()) min = colony.getFitness();
            if(max < colony.getFitness()) max = colony.getFitness();
            sum += colony.getFitness();
        }

        generationInfos.add(new GenerationInfo<Integer>(min,sum/population.size(),max));

        Collections.sort(population);
    }

    /**
     * Fills colours list with random, unique colors
     * @param size number of colours to generate
     */
    protected List<Integer> initColorSet(int size) {
        List<Integer> list = new ArrayList<>(size);
        for(int i = 0; i < size; i++) list.add(i);
        return list;
    }

    /**
     * Gets the highest vertex level +1 - due to Brooks theory
     * @param graph graph
     * @return max vertex level
     */
    protected int maxVertexLevel(Graph graph) {
        int level = 0;
        int tmp;

        for(ColorableVertex v : (Collection<ColorableVertex>)graph.getVertices()) {
            tmp = graph.degree(v);
            if(tmp > level) level = tmp;
        }

        return level; //Due to Brooks theory
    }

    public void crossingOver2() {
        int popSize = population.size();
        selectionStrategy.before(population);
        Colony[] parents = new Colony[toCrossing];
        Colony[] breeding = new Colony[2];
        population = new ArrayList<>(popSize);
        float splice = 0.0f;
        float factor = 0.0f;
        int spliceIndex = 0;
        int segmentSize = 0;

        while(popSize >= population.size()) {
            for(int i = 0; i < parents.length; i++) {
                parents[i] = selectionStrategy.get();
            }
            for(int i = 0; i < breeding.length; i++)
                breeding[i] = new Colony<ColorableVertex>();

            factor = 1.0f / parents.length;
            segmentSize = (int)(factor*parents[0].size());
            for(int i = 0; i < parents.length; i+=2) {
                splice = rand.nextFloat() % factor + factor*i;
                spliceIndex = (int)(splice*parents[i].size());

                for(int j = i*segmentSize; j < segmentSize*(2+i); j++) {
                    if(j < spliceIndex) {
                        breeding[0].add(parents[i].get(j));
                        breeding[1].add(parents[i+1].get(j));
                    } else {
                        breeding[1].add(parents[i].get(j));
                        breeding[0].add(parents[i+1].get(j));
                    }
                }
            }

            population.add(breeding[0]);
            if(popSize > population.size())
                population.add(breeding[1]);
        }
    }

    /**
     * CorssingOver in population
     */
    @Override
    public void crossingOver() {
        int popSize = population.size();
        population = selectionStrategy.before(population);
//        population = new ArrayList<>(popSize);
        Colony<ColorableVertex> parentA = null;
        Colony<ColorableVertex> parentB = null;
        Colony<ColorableVertex> childA = null;
        Colony<ColorableVertex> childB = null;
        float splice;

        while(popSize >= population.size()) {
            do { parentA = selectionStrategy.get(); } while(parentA == null);
            do { parentB = selectionStrategy.get(); } while ((parentB == null) || (parentB == parentA));
            childA = new Colony<>(parentA.size());
            childB = new Colony<>(parentA.size());
            splice = rand.nextFloat();

            for(int i = 0; i < parentA.size(); i++) {
                if(i < ((int)(parentA.size()*splice))) {
                    childA.add(parentA.get((i)));
                    childB.add(parentB.get((i)));
                } else {
                    childA.add(parentB.get((i)));
                    childB.add(parentA.get((i)));
                }
            }

            population.add(childA);
            if(population.size() <= popSize) population.add(childB);
        }
    }

    public void applySolution() {
        List<ColorableVertex> best = getSolution();
        ArrayList<ColorableVertex> arr = new ArrayList<>(best.size());
        for(ColorableVertex c : graph.getVertices()) {
            arr.add(c);
        }

        Collections.sort(arr);

        for(int i = 0; i < arr.size(); i++) {
            arr.get(i).setColor(best.get(i).getColor());
        }
    }

    public void compute() {
        int i = 0;
        int max;
        int min;
        int avg;
        int fitnessMargin = 10;
        Colony<ColorableVertex> closest = new Colony<>();
        closest.setFitness(graph.getVertexCount());

        while((population.get(0).getFitness() != 0) && (i < maxSteps)) {
            i++;
            avg = 0;
            max = 0;
            min = graph.getVertexCount();

            crossingOver();
            mutations();
            for(Colony<ColorableVertex> colony : population) {
                colony.setFitness(fitnessStrategy.checkFitness(graph, graphVertices, colony));
                if(min > colony.getFitness()) min = colony.getFitness();
                if(max < colony.getFitness()) max = colony.getFitness();
                avg += colony.getFitness();
            }

            generationInfos.add(new GenerationInfo<Integer>(min, avg/population.size(), max));
            Collections.sort(population);
            if(closest.getFitness() > population.get(0).getFitness())
                closest = population.get(0);
        }
        if(population.get(0).getFitness() < closest.getFitness())
            solution = population.get(0);
        else
            solution = closest;
        generations = i;
        logger.debug("Generations " + i);
    }

    public int getColorsCount() {
        return colors.size();
    }

    /**
     * Generates random mutation due to mutation rate - random number <0, 0.5).
     * Iterates through all graphs and randomizes mutations with different mutation rate;
     */
    @Override
    public void mutations() {
        float mr = mutation;
        for(Colony<ColorableVertex> v : population) {
            if(v.isMutated() || (rand.nextFloat() >= mr)) continue;
            Collections.sort(v, new Comparator<ColorableVertex>() {
                @Override
                public int compare(ColorableVertex o1, ColorableVertex o2) {
                    return o1.getId() - o2.getId();
                }
            });
            for(int i = 0; i < graphVertices.size(); i++) {
                for(ColorableVertex c : graph.getNeighbors(graphVertices.get((i)))) {
                    if(graphVertices.get(i).getColor() == c.getColor()) {
                        v.get(i).setColor(colors.get(rand.nextInt(colors.size())));
                        break;
                    }
                }
            }
            v.mutate();
        }
    }

    /**
     * Algorithm work solution
     * @return solution
     */
    public List<ColorableVertex> getSolution() {
        return Collections.unmodifiableList(solution);
    }

    public BlockingQueue<GenerationInfo<Integer>> getGenInfos() {
        return generationInfos;
    }

    public int getGenNum() {
        return generations;
    }

    public static class GenAlgoBuilder {
        Graph graph;
        FitnessStrategy fitnessStrategy;
        SelectionStrategy selectionStrategy;
        int maxSteps;
        float mutationRate;
        int popSize;
        int toCrossing;
        int maxColors;

        public GenAlgoBuilder maxSteps(int max) { maxSteps = max; return this; }

        public GenAlgoBuilder toCrossing(int toCrissing) { this.toCrossing = toCrissing; return this; }

        public GenAlgoBuilder mutatuionRate(float m) { mutationRate = m; return this; }

        public GenAlgoBuilder populationSize(int p) { popSize = p; return this; }

        public GenAlgoBuilder selectionStrategy(SelectionStrategy ss) { selectionStrategy = ss; return this; }

        public GenAlgoBuilder graph(Graph g) { graph = g; return this; }

        public GenAlgoBuilder fitnessStrategy(FitnessStrategy fs) { fitnessStrategy = fs; return this; }

        public GenAlgoBuilder maxColors(int m) { maxColors = m; return this; }

        public GeneticsAlgorithm build() {
            return new GeneticsAlgorithm(this);
        }
    }
}
