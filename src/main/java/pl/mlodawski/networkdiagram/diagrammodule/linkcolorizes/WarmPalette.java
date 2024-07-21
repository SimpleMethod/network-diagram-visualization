package pl.mlodawski.networkdiagram.diagrammodule.linkcolorizes;

import java.awt.Color;

class WarmPalette implements LinkColorStrategy {
    private static final WarmPalette INSTANCE = new WarmPalette();

    private WarmPalette() {
    }

    public static WarmPalette getInstance() {
        return INSTANCE;
    }

    @Override
    public Color getLinkColor(int percentage) {
        float ratio = Math.min(100, Math.max(0, percentage)) / 100.0f;
        int green = (int) (255 * ratio);
        return new Color(255, green, 0);
    }
}
