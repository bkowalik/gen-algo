package info.edytor.bartek.genetics.genetic.util;

public class GenerationInfo<T> {
    private static int counter = 0;
    public final int ID = counter++;
    public final T minFitness;
    public final T avgFitness;
    public final T maxFitness;

    public GenerationInfo(T min, T avg, T max) {
        minFitness = min;
        avgFitness = avg;
        maxFitness = max;
    }

    @Override
    public String toString() {
        return "[ID=" +ID + ", min=" + minFitness + ", avg=" + avgFitness + ", max=" + maxFitness + "]";
    }
}
