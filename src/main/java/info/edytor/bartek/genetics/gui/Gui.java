package info.edytor.bartek.genetics.gui;


import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractModalGraphMouse;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import info.edytor.bartek.genetics.AppContext;
import info.edytor.bartek.genetics.Main;
import info.edytor.bartek.genetics.genetic.GeneticsAlgorithm;
import info.edytor.bartek.genetics.genetic.fitness.NeighborFitness;
import info.edytor.bartek.genetics.genetic.selection.ElitismSelection;
import info.edytor.bartek.genetics.graph.VertexColoring;
import info.edytor.bartek.genetics.graph.dimacs.Loader;
import info.edytor.bartek.genetics.graph.generators.CliqueGenerator;
import info.edytor.bartek.genetics.graph.generators.GraphGenerator;
import info.edytor.bartek.genetics.graph.generators.PlanarGenerator;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;

public class Gui extends JFrame {
    private static final boolean DEBUG = false;
    static final int WINDOW_HEIGHT = 500;
    static final int WINDOW_WIDTH = 800;

    private final JTabbedPane tabbedPane;

    private final JPanel contentPane;
    private final JTextField gVertices;
    private final JTextField aTournament;
    private final JTextField aPopulationSize;
    private final JRadioButton gDirected;
    private final JRadioButton gNotDirected;
    private final JTextField aMaxColors;
    private final JTextField aMaxSteps;
    private final JPanel chartPanel;
    private final JPanel graphPanel;
    private JSlider aMutationFactor;
    private JSlider aTrasmissionFactor;
    private final JRadioButton gCliqueRadio;
    private final JRadioButton gPlanarRadio;
    private final JRadioButton aRouletteRadio;
    private final JRadioButton aEliteRadio;
    private final JLabel resultGenerations;
    private final JLabel resultColors;

    private volatile boolean isGoing = false;


    /**
     * Create the frame.
     */
    public Gui() {
        //setResizable(false);
        setTitle("Kolorowanie wierzchołkowe grafu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - WINDOW_WIDTH)/ 2;
        int y = (screen.height - WINDOW_HEIGHT) / 2;
        setBounds(x, y, 818, 632);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        final JSplitPane splitPane = new JSplitPane();
        splitPane.setEnabled(false);
        contentPane.add(splitPane);

        final JPanel controlPanel = new JPanel();
        controlPanel.setPreferredSize(new Dimension(350, 400));
        controlPanel.setSize(350, getHeight());

        splitPane.setRightComponent(controlPanel);
        splitPane.setDividerSize(5);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                splitPane.setDividerLocation(getWidth() - controlPanel.getPreferredSize().width);
            }
        });

        JLabel lblNewLabel = new JLabel("Liczba wierzchołków:");
        gVertices = new JTextField();
        gVertices.setColumns(10);

        JLabel lblLiczbaRegionw = new JLabel("Liczba turniejów:");
        aTournament = new JTextField();
        aTournament.setText("2");
        aTournament.setColumns(10);
        
        ButtonGroup bg = new ButtonGroup();
        gDirected = new JRadioButton("skierowany");
        gNotDirected = new JRadioButton("nieskierowany");
        bg.add(gDirected);
        bg.add(gNotDirected);

        JButton btnGenerate = new JButton("Generuj!");
        btnGenerate.addActionListener(new GeneratorAction());

        JLabel lblWielkoPopulacji = new JLabel("Wielkość populacji:");
        aPopulationSize = new JTextField();
        aPopulationSize.setText("400");
        aPopulationSize.setColumns(10);

        JLabel lblWskanik = new JLabel("Max kolorów:");
        aMaxColors = new JTextField();
        aMaxColors.setText("0");
        aMaxColors.setColumns(10);

        JLabel lblMaxLiczbaKrokrw = new JLabel("Max liczba kroków:");
        aMaxSteps = new JTextField();
        aMaxSteps.setText("1000");
        aMaxSteps.setColumns(10);

        JButton btnColor = new JButton("Koloruj");
        btnColor.addActionListener(new ColorAction());

        JLabel lblNewLabel_2 = new JLabel("Współczynnik mutacji:");
        aMutationFactor = new JSlider();
        aMutationFactor.setValue(25);
        aMutationFactor.setPaintLabels(true);
        aMutationFactor.setMaximum(100);
        aMutationFactor.setMajorTickSpacing(10);
        aMutationFactor.setPaintTicks(true);
        aMutationFactor.setMinorTickSpacing(5);
        Hashtable<Integer, JLabel> table = new Hashtable<>();
        table.put(new Integer(0), new JLabel("0.0"));
        table.put(new Integer(50), new JLabel("0.5"));
        table.put(new Integer(100), new JLabel("1.0"));
        aMutationFactor.setLabelTable(table);

        JLabel lblWspczynnikPrzechodzenia = new JLabel("Współczynnik przechodzenia:");
        aTrasmissionFactor = new JSlider();
        aTrasmissionFactor.setValue(45);
        aTrasmissionFactor.setMaximum(100);
//        aTrasmissionFactor.setMinorTickSpacing(5);
        aTrasmissionFactor.setPaintTicks(true);
        aTrasmissionFactor.setPaintLabels(true);
        aTrasmissionFactor.setMajorTickSpacing(10);
        Hashtable<Integer, JLabel> transmissionLabels = new Hashtable<>();
        transmissionLabels.put(new Integer(0), new JLabel("0.0"));
        transmissionLabels.put(new Integer(50), new JLabel("0.5"));
        transmissionLabels.put(new Integer(100), new JLabel("1.0"));
        aTrasmissionFactor.setLabelTable(transmissionLabels);

        gCliqueRadio = new JRadioButton("klika");
        gPlanarRadio = new JRadioButton("planarny");
        gPlanarRadio.setSelected(true);
        ButtonGroup graphButtonGroup = new ButtonGroup();
        graphButtonGroup.add(gCliqueRadio);
        graphButtonGroup.add(gPlanarRadio);
        
        JLabel lblWybieranie = new JLabel("Wybieranie:");

        aRouletteRadio = new JRadioButton("Ruletkowe");
        aRouletteRadio.setSelected(true);
        aEliteRadio = new JRadioButton("Elitowe");
        ButtonGroup choosingButtonGroup = new ButtonGroup();
        choosingButtonGroup.add(aEliteRadio);
        choosingButtonGroup.add(aRouletteRadio);
        
        JLabel lbllgeneracji = new JLabel("Liczba generacji:");
        
        JLabel lbllkolorow = new JLabel("Liczba użytych kolorów:");

        resultGenerations = new JLabel("");
        resultColors = new JLabel("");
        
        JButton btnStop = new JButton("Stop");


        GroupLayout gl_controlPanel = new GroupLayout(controlPanel);
        gl_controlPanel.setHorizontalGroup(
            gl_controlPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_controlPanel.createSequentialGroup()
                        .addGroup(gl_controlPanel.createParallelGroup(Alignment.LEADING)
                                .addGroup(gl_controlPanel.createSequentialGroup()
                                        .addGap(52)
                                        .addGroup(gl_controlPanel.createParallelGroup(Alignment.LEADING)
                                                .addComponent(gCliqueRadio)
                                                .addComponent(lblNewLabel)
                                                .addComponent(gDirected))
                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                        .addGroup(gl_controlPanel.createParallelGroup(Alignment.LEADING)
                                                .addComponent(gVertices, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(gPlanarRadio)
                                                .addComponent(gNotDirected))
                                        .addPreferredGap(ComponentPlacement.RELATED, 32, Short.MAX_VALUE))
                                .addGroup(gl_controlPanel.createSequentialGroup()
                                        .addContainerGap(15, Short.MAX_VALUE)
                                        .addGroup(gl_controlPanel.createParallelGroup(Alignment.TRAILING, false)
                                                .addGroup(gl_controlPanel.createSequentialGroup()
                                                        .addComponent(lblWspczynnikPrzechodzenia)
                                                        .addGap(10))
                                                .addGroup(gl_controlPanel.createSequentialGroup()
                                                        .addGroup(gl_controlPanel.createParallelGroup(Alignment.TRAILING)
                                                                .addComponent(lblWielkoPopulacji)
                                                                .addComponent(lblNewLabel_2)
                                                                .addComponent(lblLiczbaRegionw))
                                                        .addGap(30)))
                                        .addGroup(gl_controlPanel.createParallelGroup(Alignment.LEADING)
                                                .addComponent(aTrasmissionFactor, GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                                                .addComponent(aMutationFactor, GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                                                .addComponent(aPopulationSize, GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                                                .addComponent(aTournament, GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE))))
                        .addGap(17))
                .addGroup(gl_controlPanel.createSequentialGroup()
                    .addGap(65)
                    .addGroup(gl_controlPanel.createParallelGroup(Alignment.TRAILING)
                        .addComponent(lblWskanik)
                        .addComponent(lblMaxLiczbaKrokrw)
                        .addComponent(aRouletteRadio))
                    .addGap(10)
                    .addGroup(gl_controlPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(aEliteRadio)
                        .addComponent(aMaxColors, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                        .addComponent(aMaxSteps, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(79, Short.MAX_VALUE))
                .addGroup(gl_controlPanel.createSequentialGroup()
                    .addContainerGap(114, Short.MAX_VALUE)
                    .addComponent(btnGenerate)
                    .addGap(115))
                .addGroup(gl_controlPanel.createSequentialGroup()
                    .addContainerGap(125, Short.MAX_VALUE)
                    .addComponent(lblWybieranie)
                    .addGap(121))
                .addGroup(gl_controlPanel.createSequentialGroup()
                    .addGap(41)
                    .addGroup(gl_controlPanel.createParallelGroup(Alignment.TRAILING)
                            .addComponent(btnColor)
                            .addComponent(lbllkolorow)
                            .addComponent(lbllgeneracji))
                    .addGap(18)
                    .addGroup(gl_controlPanel.createParallelGroup(Alignment.LEADING)
                            .addComponent(resultColors)
                            .addComponent(btnStop)
                            .addComponent(resultGenerations))
                    .addContainerGap(76, Short.MAX_VALUE))
        );
        gl_controlPanel.setVerticalGroup(
            gl_controlPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_controlPanel.createSequentialGroup()
                    .addGroup(gl_controlPanel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_controlPanel.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(gl_controlPanel.createParallelGroup(Alignment.BASELINE)
                                .addComponent(gVertices, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblNewLabel))
                            .addGap(18)
                            .addGroup(gl_controlPanel.createParallelGroup(Alignment.BASELINE)
                                .addComponent(gPlanarRadio)
                                .addComponent(gCliqueRadio)))
                        .addGroup(gl_controlPanel.createSequentialGroup()
                            .addGap(75)
                            .addGroup(gl_controlPanel.createParallelGroup(Alignment.BASELINE)
                                .addComponent(gNotDirected)
                                .addComponent(gDirected))))
                    .addGap(40)
                    .addComponent(btnGenerate)
                    .addGap(33)
                    .addGroup(gl_controlPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(aTournament, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblLiczbaRegionw))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_controlPanel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(aPopulationSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblWielkoPopulacji))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_controlPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(lblNewLabel_2)
                        .addComponent(aMutationFactor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(gl_controlPanel.createParallelGroup(Alignment.LEADING)
                            .addGroup(gl_controlPanel.createSequentialGroup()
                                    .addGap(19)
                                    .addComponent(lblWspczynnikPrzechodzenia))
                            .addGroup(gl_controlPanel.createSequentialGroup()
                                    .addPreferredGap(ComponentPlacement.UNRELATED)
                                    .addComponent(aTrasmissionFactor, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)))
                    .addGap(18)
                    .addComponent(lblWybieranie)
                    .addGap(5)
                    .addGroup(gl_controlPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(aRouletteRadio)
                        .addComponent(aEliteRadio))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl_controlPanel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(lblWskanik)
                            .addComponent(aMaxColors, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_controlPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(aMaxSteps, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblMaxLiczbaKrokrw))
                    .addGap(18)
                    .addGroup(gl_controlPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(btnColor)
                        .addComponent(btnStop))
                    .addGap(18)
                    .addGroup(gl_controlPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lbllgeneracji)
                        .addComponent(resultGenerations))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl_controlPanel.createParallelGroup(Alignment.LEADING)
                            .addComponent(resultColors)
                            .addComponent(lbllkolorow))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        controlPanel.setLayout(gl_controlPanel);

        tabbedPane = new JTabbedPane();

        graphPanel = new JPanel();
        FlowLayout fl_graphPanel = (FlowLayout) graphPanel.getLayout();
        fl_graphPanel.setAlignOnBaseline(true);
        fl_graphPanel.setHgap(238);
        tabbedPane.add("Graf", graphPanel);
        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    System.out.println("KLIK");
                }
            }
        });
        splitPane.setLeftComponent(tabbedPane);

        chartPanel = new JPanel();
//        tabbedPane.add("Wykresy", null);
//        tabbedPane.setEnabledAt(1, false);

        if(!DEBUG)
            Thread.setDefaultUncaughtExceptionHandler(new GuiExceptionHandler());
        addMenuBar();
    }

    protected void addMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("Plik");

        JMenuItem newSession = new JMenuItem("Nowy");
        newSession.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int opt = JOptionPane.showConfirmDialog(Gui.this, "Czy chcesz zakończyć obecną sesję?", "Nowa sesja", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
                if(opt == JOptionPane.YES_OPTION) {
                    throw new UnsupportedOperationException("Action unimplemented");
                }
            }
        });
        file.add(newSession);

        JMenuItem load = new JMenuItem("Załaduj");
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser loader = new JFileChooser();
                int val = loader.showOpenDialog(Gui.this);
                if(val == JFileChooser.APPROVE_OPTION) {
                    Loader dimacsLoader = new Loader(loader.getSelectedFile().getAbsolutePath());
                    try {
                        dimacsLoader.load();
                    } catch (IOException e1) {
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    AppContext.getInstance().setGraph(dimacsLoader.getGraph());
                    drawGraph(dimacsLoader.getGraph());
                }
            }
        });
        file.add(load);

        JMenuItem exit = new JMenuItem("Wyjście");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int opt = JOptionPane.showConfirmDialog(Gui.this, "Czy chcesz opuścić program?", "Wyjście?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
                if(opt == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        file.add(exit);

        JMenu help = new JMenu("Pomoc");
        JMenuItem about = new JMenuItem("O programie");
        about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException("Action unimplemented");
            }
        });
        help.add(about);

        menuBar.add(file);
        menuBar.add(help);
        setJMenuBar(menuBar);
    }

    public void drawGraph(Graph g) {
        Layout layout = new KKLayout(g);
        layout.setSize(new Dimension(2000, 2000));

        final VisualizationViewer viewer = new VisualizationViewer(layout, graphPanel.getSize());

        final GraphZoomScrollPane panel = new GraphZoomScrollPane(viewer);
        AbstractModalGraphMouse mouse = new DefaultModalGraphMouse();
        viewer.setGraphMouse(mouse);
        viewer.addKeyListener(mouse.getModeKeyListener());
        viewer.getRenderContext().setVertexFillPaintTransformer(new VertexColoring(Arrays.asList(VertexColoring.COLORS)));
        ScalingControl scaler = new CrossoverScalingControl();

        if(tabbedPane.getTabCount() > 0) {
            tabbedPane.removeAll();
        }

        tabbedPane.add("Graf", panel);
    }

    protected class GuiExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            if(e instanceof NumberFormatException) {
                JOptionPane.showMessageDialog(Gui.this, "Proszę wpisać poprawną wartość liczbową.", "Error!", JOptionPane.ERROR_MESSAGE, null);
            } else {
                JOptionPane.showMessageDialog(Gui.this, e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE, null);
            }
        }
    }

    private class ColorAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int pop = Integer.parseInt(aPopulationSize.getText());
            int maxSteps = Integer.parseInt(aMaxSteps.getText());
            int toCrossing = Integer.parseInt(aTournament.getText());
            float mutRate = ((float)aMutationFactor.getValue()/aMutationFactor.getMaximum());
            int maxc = Integer.parseInt(aMaxColors.getText());
            float selStrat = (float)aTrasmissionFactor.getValue()/aTrasmissionFactor.getMaximum();
            GeneticsAlgorithm.GenAlgoBuilder builder = new GeneticsAlgorithm.GenAlgoBuilder();
            builder = builder.fitnessStrategy(new NeighborFitness())
                    .graph(AppContext.getInstance().getGraph())
                    .maxSteps(maxSteps)
                    .toCrossing(toCrossing)
                    .mutatuionRate(mutRate)
                    .populationSize(pop)
                    .maxColors(maxc)
                    .selectionStrategy(new ElitismSelection(selStrat));
            Main.logger.debug("Mutacje " + mutRate);
            Main.logger.debug("Crossing " + selStrat);
            final GeneticsAlgorithm ga = builder.build();

            GraphPanel panel = new GraphPanel(ga.getGenInfos(),AppContext.getInstance().getGraph().getVertexCount(), maxSteps);

            if(tabbedPane.getTabCount() > 1) {
                tabbedPane.remove(1);
            }

            tabbedPane.add("Wykres", panel);
            tabbedPane.setSelectedIndex(1);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    ga.compute();
                    ga.applySolution();
                    resultGenerations.setText(String.valueOf(ga.getGenNum()));
                    resultColors.setText(String.valueOf(ga.getColorsCount()));
//                    graphPanel.updateUI();
                }
            }).start();
        }
    }

    private class GeneratorAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int vertices = Integer.parseInt(gVertices.getText());
            GraphGenerator gen = null;
            if(gPlanarRadio.isSelected()) {
                boolean directed = false;
                if(gDirected.isSelected()) directed = true;
                gen = new PlanarGenerator(vertices, directed);
            } else if(gCliqueRadio.isSelected()) {
                 gen = new CliqueGenerator(vertices);
            }
            Graph g = gen.generate();
            drawGraph(g);
            AppContext.getInstance().setGraph(g);
        }
    }
}
