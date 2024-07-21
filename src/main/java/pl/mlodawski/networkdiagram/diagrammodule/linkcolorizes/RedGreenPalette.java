package pl.mlodawski.networkdiagram.diagrammodule.linkcolorizes;

import java.awt.*;

 class RedGreenPalette implements LinkColorStrategy {
    private static final RedGreenPalette INSTANCE = new RedGreenPalette();

    private RedGreenPalette() {}

    public static RedGreenPalette getInstance() {
        return INSTANCE;
    }

    @Override
    public Color getLinkColor(int percentage) {
        float ratio = Math.min(100, Math.max(0, percentage)) / 100.0f;
        int red = (int) (255 * ratio);
        int green = 255 - red;
        return new Color(red, green, 0);
    }
}