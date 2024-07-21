package pl.mlodawski.networkdiagram.diagrammodule.linkcolorizes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum ColorStrategy {
    RED_GREEN("Red-Green", RedGreenPalette.getInstance()),
    BLUE_YELLOW("Blue-Yellow", BlueYellowPalette.getInstance()),
    GRAYSCALE("Grayscale", GrayscalePalette.getInstance()),
    COLD("Cold", ColdPalette.getInstance()),
    WARM("Warm", WarmPalette.getInstance()),
    RAINBOW("Rainbow", RainbowPalette.getInstance()),
    PURPLE_YELLOW("Purple-Yellow", PurpleYellowPalette.getInstance());

    @JsonProperty("name")
    private final String name;

    @JsonProperty("strategy")
    private final LinkColorStrategy strategy;

    ColorStrategy(String name, LinkColorStrategy strategy) {
        this.name = name;
        this.strategy = strategy;
    }
}