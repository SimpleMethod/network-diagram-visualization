package pl.mlodawski.networkdiagram.diagrammodule.drawingdiagram;

import org.jfree.svg.SVGGraphics2D;
import org.springframework.stereotype.Service;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.DocumentStyle;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.NetworkNode;

import java.awt.*;

@Service
class NodeDrawingService {

    /**
     * Draws a node
     *
     * @param svgGraphics2D SVG graphics object
     * @param node          node to be drawn
     * @param documentStyle document style
     * @return SVG element
     */
    public String drawNode(SVGGraphics2D svgGraphics2D, NetworkNode node, DocumentStyle documentStyle) {
        int fontStyle = documentStyle.fontStyle().getValue();
        if ((fontStyle & (Font.PLAIN | Font.BOLD | Font.ITALIC)) != fontStyle) {
            fontStyle = Font.PLAIN;
        }

        svgGraphics2D.setPaint(Color.decode(documentStyle.nodeColor()));
        svgGraphics2D.fillRect((int) node.getX(), (int) node.getY(), (int) node.getWidth(), (int) node.getHeight());
        svgGraphics2D.setPaint(Color.decode(documentStyle.borderColor()));
        svgGraphics2D.drawRect((int) node.getX(), (int) node.getY(), (int) node.getWidth(), (int) node.getHeight());
        String name = node.getName();
        if (name != null && !name.isEmpty()) {
            svgGraphics2D.setFont(new Font(documentStyle.font(), fontStyle, documentStyle.fontSize()));
            FontMetrics fm = svgGraphics2D.getFontMetrics();
            int textWidth = fm.stringWidth(name);
            int textHeight = fm.getAscent() - fm.getDescent();
            int x = (int) (node.getX() + (node.getWidth() - textWidth) / 2);
            int y = (int) (node.getY() + (node.getHeight() + textHeight) / 2);
            svgGraphics2D.drawString(name, x, y);
        }

        return svgGraphics2D.getSVGElement() + "</g>\n";
    }


}
