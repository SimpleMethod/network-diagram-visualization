package pl.mlodawski.networkdiagram.diagrammodule.model.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import pl.mlodawski.networkdiagram.diagrammodule.linkcolorizes.ColorStrategy;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.FontStyle;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
public class DiagramConfig {

    private String fontName;
    private int fontSize;
    private String fontColor;
    private FontStyle fontType;
    private String backgroundColor;
    private String borderColor;
    private String nodeColor;
    private ColorStrategy colorStrategy;
    private String title;
    private boolean showTitle;
    private boolean showLegend;


}
