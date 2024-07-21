package pl.mlodawski.networkdiagram.diagrammodule.linkcolorizes;

import java.awt.*;


public class LinkColorizes {
    private LinkColorStrategy colorStrategy;

    public LinkColorizes(ColorStrategy strategy) {
        this.colorStrategy = strategy.getStrategy();
    }

    public Color getLinkColor(int percentage) {
        return colorStrategy.getLinkColor(percentage);
    }

    public void setStrategy(ColorStrategy strategy) {
        this.colorStrategy = strategy.getStrategy();
    }
}
