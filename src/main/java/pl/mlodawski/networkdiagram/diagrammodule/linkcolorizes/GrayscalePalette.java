package pl.mlodawski.networkdiagram.diagrammodule.linkcolorizes;

import java.awt.*;

 class GrayscalePalette implements LinkColorStrategy {
    private static final GrayscalePalette INSTANCE = new GrayscalePalette();

    private GrayscalePalette() {}

    public static GrayscalePalette getInstance() {
        return INSTANCE;
    }

    @Override
    public Color getLinkColor(int percentage) {
        int gray = (int) (255 * (Math.min(100, Math.max(0, percentage)) / 100.0f));
        return new Color(gray, gray, gray);
    }
}