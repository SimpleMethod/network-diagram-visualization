package pl.mlodawski.networkdiagram.diagrammodule.drawingdiagram;

import lombok.AllArgsConstructor;
import org.jfree.svg.SVGGraphics2D;
import org.springframework.stereotype.Service;
import pl.mlodawski.networkdiagram.diagrammodule.linkcolorizes.LinkColorizes;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.DocumentStyle;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.NetworkLink;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.NetworkNode;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

@Service
@AllArgsConstructor
class LinkDrawingService {

    private final int PADDING = 2;

    private final NetworkNodeUtilsService networkNodeUtilsService;
    private final ArrowDrawingService arrowDrawingService;


    /**
     * Draws a link between two nodes.
     *
     * @param svgGraphics2D SVG graphics object
     * @param link          link to be drawn
     * @param nodes         list of all nodes
     * @param sourcePoint   source point
     * @param targetPoint   target point
     * @param documentStyle document style
     */
    public void drawLink(SVGGraphics2D svgGraphics2D, NetworkLink link, List<NetworkNode> nodes, Point2D.Float sourcePoint, Point2D.Float targetPoint, DocumentStyle documentStyle) {

        LinkColorizes linkColorizes = new LinkColorizes(documentStyle.linkPalette());

        NetworkNode sourceNode = networkNodeUtilsService.findNodeById(nodes, link.getSourceNodeId());
        NetworkNode targetNode = networkNodeUtilsService.findNodeById(nodes, link.getTargetNodeId());

        if (sourceNode == null || targetNode == null) {
            return;
        }

        int outgoingTrafficPercentage = (int) link.getOutgoingTraffic();
        int incomingTrafficPercentage = (int) link.getIncomingTraffic();

        Color outgoingLinkColor = linkColorizes.getLinkColor(outgoingTrafficPercentage);
        Color incomingLinkColor = linkColorizes.getLinkColor(incomingTrafficPercentage);

        int STROKE_WIDTH = 3;
        svgGraphics2D.setStroke(new BasicStroke(STROKE_WIDTH));

        Point2D.Float midPoint = calculateMidPointOffset(sourcePoint, targetPoint);

        drawArrow(svgGraphics2D, sourcePoint, midPoint, outgoingLinkColor, Color.decode(documentStyle.borderColor()));
        drawArrow(svgGraphics2D, targetPoint, midPoint, incomingLinkColor, Color.decode(documentStyle.borderColor()));

        drawTrafficLabels(svgGraphics2D, sourcePoint, midPoint, outgoingTrafficPercentage, documentStyle);
        drawTrafficLabels(svgGraphics2D, targetPoint, midPoint, incomingTrafficPercentage, documentStyle);
    }

    /**
     * Draws an arrow between two points.
     *
     * @param svgGraphics2D SVG graphics object
     * @param start         start point
     * @param end           end point
     * @param color         arrow color
     * @param borderColor   arrow border color
     */
    private void drawArrow(SVGGraphics2D svgGraphics2D, Point2D.Float start, Point2D.Float end, Color color, Color borderColor) {
        svgGraphics2D.setPaint(color);
        arrowDrawingService.drawArrow(svgGraphics2D, start.x, start.y, end.x, end.y, color, borderColor);
    }

    /**
     * Draws a label with traffic percentage.
     *
     * @param svgGraphics2D SVG graphics object
     * @param start         start point
     * @param end           end point
     * @param percentage    traffic percentage
     * @param documentStyle document style
     */
    public void drawTrafficLabels(SVGGraphics2D svgGraphics2D, Point2D.Float start, Point2D.Float end, int percentage, DocumentStyle documentStyle) {
        int fontStyle = documentStyle.fontStyle().getValue();
        if ((fontStyle & ~(Font.PLAIN | Font.BOLD | Font.ITALIC)) != 0) {
            fontStyle = Font.PLAIN;
        }

        Font font = new Font(documentStyle.font(), fontStyle, documentStyle.fontSize());
        svgGraphics2D.setFont(font);
        FontMetrics fm = svgGraphics2D.getFontMetrics();
        int textWidth = fm.stringWidth(percentage + "%");
        int textHeight = fm.getAscent() - fm.getDescent();
        Point2D.Float labelPoint = new Point2D.Float((start.x + end.x - textWidth) / 2, (start.y + end.y + textHeight) / 2);

        drawLabelBackground(svgGraphics2D, labelPoint, textWidth, textHeight, Color.decode(documentStyle.nodeColor()));
        drawLabelBorder(svgGraphics2D, labelPoint, textWidth, textHeight, Color.decode(documentStyle.borderColor()));
        drawLabelText(svgGraphics2D, labelPoint, percentage + "%", Color.decode(documentStyle.fontColor()));
    }


    /**
     * Draws a label border.
     *
     * @param svgGraphics2D SVG graphics object
     * @param point         point
     * @param textWidth     text width
     * @param textHeight    text height
     * @param color         border color
     */
    private void drawLabelBorder(SVGGraphics2D svgGraphics2D, Point2D.Float point, int textWidth, int textHeight, Color color) {
        svgGraphics2D.setPaint(color);
        svgGraphics2D.drawRect((int) point.x - PADDING, (int) point.y - textHeight - PADDING, textWidth + 2 * PADDING, textHeight + 2 * PADDING);
    }

    /**
     * Draws a label background.
     *
     * @param svgGraphics2D SVG graphics object
     * @param point         point
     * @param textWidth     text width
     * @param textHeight    text height
     * @param color         background color
     */
    private void drawLabelBackground(SVGGraphics2D svgGraphics2D, Point2D.Float point, int textWidth, int textHeight, Color color) {
        svgGraphics2D.setPaint(color);
        svgGraphics2D.fillRect((int) point.x - PADDING, (int) point.y - textHeight - PADDING, textWidth + 2 * PADDING, textHeight + 2 * PADDING);
    }

    /**
     * Draws a label text.
     *
     * @param svgGraphics2D SVG graphics object
     * @param point         point
     * @param text          text
     * @param color         text color
     */
    private void drawLabelText(SVGGraphics2D svgGraphics2D, Point2D.Float point, String text, Color color) {
        svgGraphics2D.setPaint(color);
        svgGraphics2D.drawString(text, point.x, point.y);
    }


    /**
     * Calculates a mid-point offset between two points.
     *
     * @param p1 point 1
     * @param p2 point 2
     * @return mid-point offset
     */
    private Point2D.Float calculateMidPointOffset(Point2D.Float p1, Point2D.Float p2) {
        float midX = (p1.x + p2.x) / 2;
        float midY = (p1.y + p2.y) / 2;
        return new Point2D.Float(midX, midY);
    }

}