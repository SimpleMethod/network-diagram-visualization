package pl.mlodawski.networkdiagram.diagrammodule.linkcolorizes;

import java.awt.*;

 class BlueYellowPalette implements LinkColorStrategy {
    private static final BlueYellowPalette INSTANCE = new BlueYellowPalette();

    private BlueYellowPalette() {}

    public static BlueYellowPalette getInstance() {
        return INSTANCE;
    }

    @Override
    public Color getLinkColor(int percentage) {
        percentage = Math.min(100, Math.max(0, percentage));
        float ratio = percentage / 100.0f;
        int blue = (int) (255 * ratio);
        int yellow = 255 - blue;
        return new Color(yellow, yellow, blue);
    }
}
