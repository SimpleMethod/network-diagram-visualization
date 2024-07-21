package pl.mlodawski.networkdiagram.diagrammodule.model.document;


import lombok.Getter;

import java.awt.*;

@Getter
public enum FontStyle {
    PLAIN(Font.PLAIN, "PLAIN"),
    BOLD(Font.BOLD, "BOLD"),
    ITALIC(Font.ITALIC, "ITALIC");
    private final int value;
    private final String name;

    FontStyle(int value, String name) {
        this.value = value;
        this.name = name;
    }

}
