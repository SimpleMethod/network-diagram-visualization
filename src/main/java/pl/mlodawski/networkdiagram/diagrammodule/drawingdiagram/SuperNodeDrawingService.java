package pl.mlodawski.networkdiagram.diagrammodule.drawingdiagram;

import lombok.AllArgsConstructor;
import org.jfree.svg.SVGGraphics2D;
import org.springframework.stereotype.Service;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.DocumentStyle;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.NetworkNode;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.SuperNode;

import java.awt.*;
import java.util.List;

@Service
@AllArgsConstructor
class SuperNodeDrawingService {

    private final NodeDrawingService nodeDrawer;


    /**
     * Draws a super node and all its nodes
     *
     * @param svgGraphics2D if null, a new SVGGraphics2D object will be created
     * @param superNode     the super node to draw
     * @param documentStyle the style to use
     * @return the SVG content of the super node
     */
    public StringBuilder drawSuperNode(SVGGraphics2D svgGraphics2D, SuperNode superNode, DocumentStyle documentStyle) {

        if (svgGraphics2D == null) {
            svgGraphics2D = new SVGGraphics2D(superNode.getWidth(), superNode.getHeight());
        }

        int xTranslate = superNode.getX();
        int yTranslate = superNode.getY();

        Stroke originalStroke = svgGraphics2D.getStroke();
        svgGraphics2D.setStroke(new BasicStroke(2.0f));

        StringBuilder mainSvgContent = new StringBuilder();
        NetworkNode superNodeAsNode = new NetworkNode(0, 0, superNode.getWidth(), superNode.getHeight(), superNode.getName());
        nodeDrawer.drawNode(svgGraphics2D, superNodeAsNode, documentStyle);
        svgGraphics2D.setStroke(originalStroke);
        mainSvgContent.append("<g id=\"").append(superNode.getName()).append("\" transform=\"translate(").append(xTranslate).append(",").append(yTranslate).append(")\">").append(svgGraphics2D.getSVGElement()).append("</g>\n");
        updateNodePositions(superNode);
        return mainSvgContent;
    }

    /**
     * Calculates the size of the super node based on the size of its nodes and its title
     *
     * @param superNode the super node to calculate the size for
     */
    public void calculateSize(SuperNode superNode) {
        int topWidth = calculateTotalWidth(superNode.getTopNodes());
        int bottomWidth = calculateTotalWidth(superNode.getBottomNodes());
        int leftHeight = calculateTotalHeight(superNode.getLeftNodes());
        int rightHeight = calculateTotalHeight(superNode.getRightNodes());

        int widthFromNodes = Math.max(topWidth, bottomWidth);
        int heightFromNodes = Math.max(leftHeight, rightHeight);

        int widthFromTitle = superNode.getName().length() * 7;
        superNode.setWidth(Math.max(widthFromNodes, widthFromTitle) + 40);
        superNode.setHeight(heightFromNodes + 40);

    }

    /**
     * Updates the positions of the nodes inside the super node
     *
     * @param superNode the super node to update the positions for
     */
    private void updateNodePositions(SuperNode superNode) {
        updateNodesAtPosition(superNode.getTopNodes(), "top", superNode);
        updateNodesAtPosition(superNode.getBottomNodes(), "bottom", superNode);
        updateNodesAtPosition(superNode.getLeftNodes(), "left", superNode);
        updateNodesAtPosition(superNode.getRightNodes(), "right", superNode);
    }

    /**
     * Updates the positions of the nodes at the given position
     *
     * @param nodes     the nodes to update
     * @param position  the position of the nodes
     * @param superNode the super node that contains the nodes
     */
    private void updateNodesAtPosition(List<NetworkNode> nodes, String position, SuperNode superNode) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }

        int spacing = 5;
        int offset = 5;
        int currentX, currentY;

        int totalWidth = nodes.stream().mapToInt(node -> (int) node.getWidth()).sum() + (nodes.size() - 1) * spacing;
        int totalHeight = nodes.stream().mapToInt(node -> (int) node.getHeight()).sum() + (nodes.size() - 1) * spacing;

        currentY = switch (position.toLowerCase()) {
            case "top" -> {
                currentX = superNode.getX() + (superNode.getWidth() - totalWidth) / 2;
                yield (int) (superNode.getY() - offset - nodes.get(0).getHeight());
            }
            case "bottom" -> {
                currentX = superNode.getX() + (superNode.getWidth() - totalWidth) / 2;
                yield superNode.getY() + superNode.getHeight() + offset;
            }
            case "left" -> {
                currentX = (int) (superNode.getX() - offset - nodes.get(0).getWidth());
                yield superNode.getY() + (superNode.getHeight() - totalHeight) / 2;
            }
            case "right" -> {
                currentX = superNode.getX() + superNode.getWidth() + offset;
                yield superNode.getY() + (superNode.getHeight() - totalHeight) / 2;
            }
            default -> throw new IllegalArgumentException("Invalid position: " + position);
        };

        for (NetworkNode node : nodes) {
            node.setX(currentX);
            node.setY(currentY);

            if (position.equalsIgnoreCase("top") || position.equalsIgnoreCase("bottom")) {
                currentX += (int) (node.getWidth() + spacing);
            } else {
                currentY += (int) (node.getHeight() + spacing);
            }
        }
    }

    /**
     * Calculates the total width of the nodes
     *
     * @param nodes the nodes to calculate the width for
     * @return the total width of the nodes
     */
    private int calculateTotalWidth(java.util.List<NetworkNode> nodes) {
        return nodes.stream().mapToInt(node -> (int) node.getWidth()).sum() + (nodes.size() - 1) * 10;
    }

    /**
     * Calculates the total height of the nodes
     *
     * @param nodes the nodes to calculate the height for
     * @return the total height of the nodes
     */
    private int calculateTotalHeight(List<NetworkNode> nodes) {
        return nodes.stream().mapToInt(node -> (int) node.getHeight()).sum() + (nodes.size() - 1) * 10;
    }

}
