package pl.mlodawski.networkdiagram.diagrammodule.linkcolorizes;

import java.awt.Color;

class RainbowPalette implements LinkColorStrategy {
    private static final RainbowPalette INSTANCE = new RainbowPalette();

    private RainbowPalette() {}

    public static RainbowPalette getInstance() {
        return INSTANCE;
    }

    @Override
    public Color getLinkColor(int percentage) {
        if (percentage == 0) {
            return Color.GRAY;
        }
        float hue = Math.min(100, Math.max(0, percentage)) / 101.0f;
        return Color.getHSBColor(hue, 1.0f, 1.0f);
    }
}
