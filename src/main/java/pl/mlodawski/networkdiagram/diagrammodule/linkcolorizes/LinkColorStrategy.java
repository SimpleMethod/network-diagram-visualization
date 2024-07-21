package pl.mlodawski.networkdiagram.diagrammodule.linkcolorizes;

import java.awt.*;

public interface LinkColorStrategy {
    Color getLinkColor(int percentage);
}