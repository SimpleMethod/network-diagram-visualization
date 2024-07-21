package pl.mlodawski.networkdiagram.diagrammodule.model.document;


import com.fasterxml.jackson.annotation.JsonProperty;
import pl.mlodawski.networkdiagram.diagrammodule.linkcolorizes.ColorStrategy;


import pl.mlodawski.networkdiagram.diagrammodule.validator.hexvalidators.HexColor;


public record DocumentStyle(@JsonProperty("font") String font, @JsonProperty("fontSize") int fontSize,
                            @HexColor @JsonProperty("fontColor") String fontColor,
                            @JsonProperty("fontStyle") FontStyle fontStyle,
                            @HexColor @JsonProperty("bgColor") String bgColor,
                            @HexColor @JsonProperty("borderColor") String borderColor,
                            @HexColor @JsonProperty("nodeColor") String nodeColor,
                            @JsonProperty("linkPalette") ColorStrategy linkPalette,
                            @JsonProperty("showLegend") boolean showTitle,
                            @JsonProperty("title") String title,
                            @JsonProperty("showLegend") boolean showLegend
) {
}
