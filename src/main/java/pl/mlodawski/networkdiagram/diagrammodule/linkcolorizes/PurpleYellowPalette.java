package pl.mlodawski.networkdiagram.diagrammodule.linkcolorizes;

import java.awt.Color;

 class PurpleYellowPalette implements LinkColorStrategy {
    private static final PurpleYellowPalette INSTANCE = new PurpleYellowPalette();

    private PurpleYellowPalette() {}

    public static PurpleYellowPalette getInstance() {
        return INSTANCE;
    }

    @Override
    public Color getLinkColor(int percentage) {
        float ratio = Math.min(100, Math.max(0, percentage)) / 100.0f;
        int purple = (int) (255 * ratio);
        int yellow = 255 - purple;
        return new Color(purple, yellow, purple);
    }
}

