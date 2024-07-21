package pl.mlodawski.networkdiagram.diagrammodule.drawingdiagram;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jfree.svg.SVGGraphics2D;
import org.springframework.stereotype.Service;


import pl.mlodawski.networkdiagram.diagrammodule.linkcolorizes.LinkColorStrategy;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.*;


import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class NetworkDrawingService {
    private static final float OFFSET = 5.0f;
    private final NetworkNodeUtilsService networkNodeUtilsService;
    private final NodeDrawingService nodeDrawer;

    private final SuperNodeDrawingService superNodeDrawingService;

    private final LinkDrawingService linkDrawingService;



    /**
     * Draws network.
     *
     * @param networkNodes  - nodes
     * @param superNodes    - super nodes
     * @param networkLinks  - links
     * @param documentStyle - document style
     * @return SVG content
     */
    public StringBuilder drawDocument(List<NetworkNode> networkNodes, List<SuperNode> superNodes, List<NetworkLink> networkLinks, DocumentStyle documentStyle) {
        List<String> superNodeContents = calculateAndDrawSuperNodes(superNodes, documentStyle);
        DocumentDimensions dimensions = calculateDocumentDimensions(networkNodes, superNodes);
        StringBuilder linksSvgContent = drawNetworkLinks(networkLinks, networkNodes, documentStyle);
        StringBuilder nodesSvgContent = drawNetworkNodes(networkNodes, documentStyle);
        StringBuilder legendSvgContent = drawLegend(documentStyle, dimensions);
        StringBuilder titleSvgContent = drawTitle(documentStyle, dimensions);
        return parseDocument(dimensions, linksSvgContent, nodesSvgContent, new StringBuilder(String.join("", superNodeContents)), legendSvgContent, titleSvgContent, documentStyle.bgColor());
    }


    /**
     * Draws network.
     *
     * @param dimensions        - document dimensions
     * @param linksSvgContent   - links SVG content
     * @param nodesSvgContent   - nodes SVG content
     * @param superNodeContents - super node contents
     * @param backgroundColor   - background color
     * @return SVG content
     */
    private StringBuilder parseDocument(DocumentDimensions dimensions, StringBuilder linksSvgContent, StringBuilder nodesSvgContent, StringBuilder superNodeContents, StringBuilder legendSvgContent, StringBuilder titleSvgContent, String backgroundColor){
        StringBuilder mainSvgContent = new StringBuilder();
        mainSvgContent.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"")
                .append(dimensions.getWidth())
                .append("\" height=\"")
                .append(dimensions.getHeight())
                .append("\">\n");

        mainSvgContent.append("<rect width=\"100%\" height=\"100%\" fill=\"")
                .append(backgroundColor)
                .append("\" />\n");

        mainSvgContent.append(titleSvgContent);
        mainSvgContent.append(linksSvgContent);
        mainSvgContent.append(nodesSvgContent);
        mainSvgContent.append(superNodeContents);
        mainSvgContent.append(legendSvgContent);
        mainSvgContent.append("</svg>");

        return removeNamespace(mainSvgContent);
    }

    private StringBuilder removeNamespace(StringBuilder content) {
        return new StringBuilder(content.toString().replaceAll("xmlns:jfreesvg='http://www.jfree.org/jfreesvg/svg'", ""));
    }

    /**
     * Draws network.
     *
     * @param links         - links
     * @param nodes         - nodes
     * @param documentStyle - document style
     * @return SVG content
     */
    private StringBuilder drawNetworkLinks(List<NetworkLink> links, List<NetworkNode> nodes, DocumentStyle documentStyle) {
        StringBuilder linksSvgContent = new StringBuilder();
        for (NetworkLink link : links) {
            NetworkNode sourceNode = networkNodeUtilsService.findNodeById(nodes, link.getSourceNodeId());
            NetworkNode targetNode = networkNodeUtilsService.findNodeById(nodes, link.getTargetNodeId());
            if (sourceNode == null || targetNode == null) continue;

            Point2D.Float sourcePoint = calculateStartingPoint(sourceNode, targetNode);
            Point2D.Float targetPoint = calculateStartingPoint(targetNode, sourceNode);

            int minX = (int) Math.min(sourcePoint.x, targetPoint.x) - 50;
            int minY = (int) Math.min(sourcePoint.y, targetPoint.y) - 50;
            int maxX = (int) Math.max(sourcePoint.x, targetPoint.x) + 50;
            int maxY = (int) Math.max(sourcePoint.y, targetPoint.y) + 50;

            SVGGraphics2D svgLinkGraphics = new SVGGraphics2D((maxX - minX), (maxY - minY));


            sourcePoint.x -= minX;
            sourcePoint.y -= minY;
            targetPoint.x -= minX;
            targetPoint.y -= minY;

            linkDrawingService.drawLink(svgLinkGraphics, link, nodes, sourcePoint, targetPoint, documentStyle);

            linksSvgContent.append("<g transform=\"translate(").append(minX).append(",").append(minY).append(")\">").append(svgLinkGraphics.getSVGElement()).append("</g>\n");
        }
        return linksSvgContent;
    }

    /**
     * Draws network.
     *
     * @param nodes         - nodes
     * @param documentStyle - document style
     * @return SVG content
     */
    private StringBuilder drawNetworkNodes(List<NetworkNode> nodes, DocumentStyle documentStyle) {
        StringBuilder nodesSvgContent = new StringBuilder();
        for (NetworkNode node : nodes) {
            int minX = (int) node.getX() - 50;
            int minY = (int) node.getY() - 50;
            int maxX = (int) (node.getX() + node.getWidth()) + 50;
            int maxY = (int) (node.getY() + node.getHeight()) + 50;
            SVGGraphics2D svgNodeGraphics = new SVGGraphics2D((maxX - minX), (maxY - minY));
            node.setX(node.getX() - minX);
            node.setY(node.getY() - minY);
            nodesSvgContent.append("<g id=\"").append(node.getName()).append("\"").append(" transform=\"translate(").append(minX).append(",").append(minY).append(")\">");
            nodesSvgContent.append(nodeDrawer.drawNode(svgNodeGraphics, node, documentStyle));
        }
        return nodesSvgContent;
    }

    private List<String> calculateAndDrawSuperNodes(List<SuperNode> superNodes, DocumentStyle documentStyle) {

        List<String> superNodeContents = new ArrayList<>();
        for (SuperNode superNode : superNodes) {
            superNodeDrawingService.calculateSize(superNode);
            String content = String.valueOf(superNodeDrawingService.drawSuperNode(null, superNode, documentStyle));
            superNodeContents.add(content);
        }
        return superNodeContents;
    }


    /**
     * Calculates document dimensions.
     *
     * @param nodes      - nodes
     * @param superNodes - super nodes
     * @return document dimensions
     */
    public DocumentDimensions calculateDocumentDimensions(List<NetworkNode> nodes, List<SuperNode> superNodes) {
        float globalMinX = Float.MAX_VALUE;
        float globalMinY = Float.MAX_VALUE;
        float globalMaxX = Float.MIN_VALUE;
        float globalMaxY = Float.MIN_VALUE;

        for (NetworkNode node : nodes) {
            globalMinX = Math.min(globalMinX, node.getX());
            globalMinY = Math.min(globalMinY, node.getY());
            globalMaxX = Math.max(globalMaxX, node.getX() + node.getWidth());
            globalMaxY = Math.max(globalMaxY, node.getY() + node.getHeight());
        }

        if (!superNodes.isEmpty()) {
            for (SuperNode superNode : superNodes) {
                globalMinX = Math.min(globalMinX, superNode.getX());
                globalMinY = Math.min(globalMinY, superNode.getY());
                globalMaxX = Math.max(globalMaxX, (superNode.getX() + superNode.getWidth()));
                globalMaxY = Math.max(globalMaxY, (superNode.getY() + superNode.getHeight()));
            }
        }

        int width = (int) Math.ceil(globalMaxX);
        int height = (int) Math.ceil(globalMaxY);

        return new DocumentDimensions(width + 5, height + 5);
    }

    /**
     * Calculates starting point of the link.
     *
     * @param node       - source node
     * @param targetNode - target node
     * @return starting point of the link
     */
    public Point2D.Float calculateStartingPoint(NetworkNode node, NetworkNode targetNode) {
        if (node == null || targetNode == null) {
            log.error("Node and targetNode cannot be null.");
            throw new IllegalArgumentException("Node and targetNode cannot be null.");
        }

        float[] center1 = getCenter(node);
        float[] center2 = getCenter(targetNode);

        float diffX = center1[0] - center2[0];
        float diffY = center1[1] - center2[1];

        Point2D.Float startingPoint = new Point2D.Float();

        if (Math.abs(diffX) > Math.abs(diffY)) {
            calculateHorizontalStartingPoint(node, center1, center2, startingPoint);
        } else {
            calculateVerticalStartingPoint(node, center1, center2, startingPoint);
        }

        return startingPoint;
    }

    /**
     * Calculates center of the node.
     *
     * @param node - node
     * @return center of the node
     */
    private float[] getCenter(NetworkNode node) {
        return new float[]{
                node.getX() + node.getWidth() / 2.0f,
                node.getY() + node.getHeight() / 2.0f
        };
    }

    /**
     * Calculates horizontal mid-point offset  .
     *
     * @param node    - node
     * @param center1 - center of the node
     * @param center2 - center of the target node
     * @param point   - point
     */
    private void calculateHorizontalStartingPoint(NetworkNode node, float[] center1, float[] center2, Point2D.Float point) {
        point.x = center1[0] < center2[0] ? node.getX() + node.getWidth() : node.getX();

        if (Math.abs(center1[1] - center2[1]) <= node.getHeight() + 3) {
            point.y = center1[1];
        } else {
            if (center1[1] < center2[1]) {
                point.y = node.getY() + node.getHeight();
            } else {
                point.y = node.getY();
            }
        }

        if (point.y == node.getY() || point.y == node.getY() + node.getHeight()) {
            adjustOffset(point, center1);
        }
    }

    /**
     * Calculates Vertical mid-point offset.
     *
     * @param node    - node
     * @param center1 - center of the node
     * @param center2 - center of the target node
     * @param point   - point
     */
    private void calculateVerticalStartingPoint(NetworkNode node, float[] center1, float[] center2, Point2D.Float point) {
        point.x = center1[0];
        point.y = center1[1] < center2[1] ? node.getY() + node.getHeight() : node.getY();

        point.y += center1[1] < center2[1] ? -OFFSET : OFFSET;

        if (point.x == node.getX() || point.x == node.getX() + node.getWidth()) {
            adjustOffset(point, center1);
        }
    }

    /**
     * Adjusts offset.
     *
     * @param point  - point
     * @param center - center of the node
     */
    private void adjustOffset(Point2D.Float point, float[] center) {
        point.x += point.x < center[0] ? OFFSET : -OFFSET;
        point.y += point.y < center[1] ? OFFSET : -OFFSET;
    }


    private StringBuilder drawLegend(DocumentStyle documentStyle, DocumentDimensions dimensions) {
        StringBuilder legendSvgContent = new StringBuilder();
        if (documentStyle.showLegend()) {
            SVGGraphics2D svgLegendGraphics = new SVGGraphics2D(dimensions.getWidth(), dimensions.getHeight());

            int fontStyle = documentStyle.fontStyle().getValue();
            if ((fontStyle & ~(Font.PLAIN | Font.BOLD | Font.ITALIC)) != 0) {
                fontStyle = Font.PLAIN;
            }

            int titleX = 20;
            int titleY = dimensions.getHeight() - 50;
            svgLegendGraphics.setPaint(Color.BLACK);
            svgLegendGraphics.setFont(new Font(documentStyle.font(), fontStyle, documentStyle.fontSize()));
            svgLegendGraphics.drawString("Load Scale Legend", titleX, titleY);

            int paletteY = titleY +10;
            int paletteWidth = 200;
            int paletteHeight = 20;

            LinkColorStrategy colorStrategy = documentStyle.linkPalette().getStrategy();
            int numColors = 100;
            int colorWidth = paletteWidth / numColors;

            for (int i = 0; i < numColors; i++) {
                Color color = colorStrategy.getLinkColor(i);
                svgLegendGraphics.setPaint(color);
                svgLegendGraphics.fillRect(titleX + i * colorWidth, paletteY, colorWidth, paletteHeight);
            }

            int labelY = paletteY + paletteHeight + 15;
            svgLegendGraphics.setPaint(Color.BLACK);
            svgLegendGraphics.setFont(new Font(documentStyle.font(), fontStyle, documentStyle.fontSize() - 2));

            int[] percentages = {0, 10, 25, 40, 55, 70, 85, 100};
            for (int percentage : percentages) {
                int labelX = titleX + percentage * paletteWidth / 100;
                svgLegendGraphics.drawString(percentage + "%", labelX, labelY);
            }

            legendSvgContent.append(svgLegendGraphics.getSVGElement());
        }
        return legendSvgContent;
    }

    private StringBuilder drawTitle(DocumentStyle documentStyle, DocumentDimensions dimensions) {
        StringBuilder titleSvgContent = new StringBuilder();
        if (documentStyle.title() != null && !documentStyle.title().isEmpty() && documentStyle.showTitle()) {
            SVGGraphics2D svgTitleGraphics = new SVGGraphics2D(dimensions.getWidth(), dimensions.getHeight());
            int titleX = dimensions.getWidth() / 2;
            int titleY = 30;
            svgTitleGraphics.setPaint(Color.BLACK);
            svgTitleGraphics.setFont(new Font(documentStyle.font(), Font.BOLD, documentStyle.fontSize() + 4));
            FontMetrics fm = svgTitleGraphics.getFontMetrics();
            int textWidth = fm.stringWidth(documentStyle.title());
            svgTitleGraphics.drawString(documentStyle.title(), titleX - textWidth / 2, titleY);
            titleSvgContent.append(svgTitleGraphics.getSVGElement());
        }
        return titleSvgContent;
    }
}
