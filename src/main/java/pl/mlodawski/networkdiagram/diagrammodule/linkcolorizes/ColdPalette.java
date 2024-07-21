package pl.mlodawski.networkdiagram.diagrammodule.linkcolorizes;

import java.awt.Color;

 class ColdPalette implements LinkColorStrategy {
    private static final ColdPalette INSTANCE = new ColdPalette();

    private ColdPalette() {}

    public static ColdPalette getInstance() {
        return INSTANCE;
    }

    @Override
    public Color getLinkColor(int percentage) {
        float ratio = Math.min(100, Math.max(0, percentage)) / 100.0f;
        int green = (int) (255 * ratio);
        return new Color(0, green, 255);
    }
}

