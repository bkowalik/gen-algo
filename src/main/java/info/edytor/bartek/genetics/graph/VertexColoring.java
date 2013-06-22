package info.edytor.bartek.genetics.graph;

import org.apache.commons.collections15.Transformer;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class VertexColoring implements Transformer<ColorableVertex, Paint> {
    private static final Logger logger = Logger.getLogger(VertexColoring.class);
    protected static final Random rand = new Random(new Date().getTime());
    /**
     * All colors available by default
     */
    public static final Color[] COLORS = {
            Color.BLACK, Color.BLUE, Color.CYAN,
            /*Color.DARK_GRAY, Color.GRAY,*/ Color.GREEN,
            Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE,
            Color.PINK, Color.RED, Color.WHITE, Color.YELLOW
    };

    private List<Color> colors;

    public VertexColoring(List<Color> colors) {
        this.colors = colors;
    }

    @Override
    public Paint transform(ColorableVertex colorableVertex) {
        return colors.get(colorableVertex.getColor());
    }

    public static List<Color> generateColors(int size) {
        List<Color> colors = new ArrayList<>();
        int get = 0;
        for(int i = 0; i < COLORS.length; i++) {
            colors.add(COLORS[i]);
            get++;
        }

        if(colors.size() < size) {
            logger.debug((size - colors.size()) + " colors will be generated randomly!");
        }

        while(colors.size() < size) {
            Color c = new Color(rand.nextInt(256),rand.nextInt(256),rand.nextInt(256));
            if(colors.contains(c)) continue;
            colors.add(c);
            get++;
        }

        return colors;
    }
}
