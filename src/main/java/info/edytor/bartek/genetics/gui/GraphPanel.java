package info.edytor.bartek.genetics.gui;

import com.sun.tools.visualvm.charts.ChartFactory;
import com.sun.tools.visualvm.charts.SimpleXYChartDescriptor;
import com.sun.tools.visualvm.charts.SimpleXYChartSupport;
import info.edytor.bartek.genetics.genetic.util.GenerationInfo;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.BlockingQueue;

public class GraphPanel extends JPanel {
    private final BlockingQueue<GenerationInfo<Integer>> source;

    public GraphPanel(BlockingQueue<GenerationInfo<Integer>> source, int max, int buff) {
        this.source = source;
        if(buff < 1) buff = 1000;
        setup(max, buff);
    }

    protected void setup(int max, int buff) {
        SimpleXYChartDescriptor descriptor = SimpleXYChartDescriptor.decimal(0, max, max, 1d, true, buff);
        descriptor.addLineFillItems("Maximum");
        descriptor.addLineFillItems("Average");
        descriptor.addLineFillItems("Minimum");

        descriptor.setYAxisDescription("<html>Przystosowanie</html>");
        descriptor.setXAxisDescription("<html>Generacja</html>");


        SimpleXYChartSupport support = ChartFactory.createSimpleXYChart(descriptor);
        setLayout(new BorderLayout());
        add(support.getChart(), BorderLayout.CENTER);

        Updater updater = new Updater(support);
        updater.execute();
    }

    protected class Updater extends SwingWorker<Object,Void> {
        private final SimpleXYChartSupport support;

        protected Updater(SimpleXYChartSupport s) {
            support = s;
        }

        @Override
        protected Object doInBackground() throws Exception {
            while(!Thread.interrupted()) {
                GenerationInfo<Integer> genInfo = source.take();
                long vals[] = {genInfo.maxFitness, genInfo.avgFitness, genInfo.minFitness};
                support.addValues(genInfo.ID, vals);
            }

            return null;
        }
    }
}
