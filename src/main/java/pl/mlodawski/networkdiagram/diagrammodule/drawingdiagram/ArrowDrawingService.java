package pl.mlodawski.networkdiagram.diagrammodule.drawingdiagram;


import org.jfree.svg.SVGGraphics2D;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;


@Service
 class ArrowDrawingService {

    private  final BasicStroke STROKE = new BasicStroke(1);


    /**
     * Draws an arrow between two points
     * @param svgGraphics2D - SVGGraphics2D object
     * @param x1 - x coordinate of the first point
     * @param y1 - y coordinate of the first point
     * @param x2 - x coordinate of the second point
     * @param y2 - y coordinate of the second point
     * @param lineColor - color of the arrow
     * @param borderColor - color of the border
     */
    public void drawArrow(SVGGraphics2D svgGraphics2D, float x1, float y1, float x2, float y2, Color lineColor, Color borderColor) {
        validateArguments(svgGraphics2D, x1, y1, x2, y2, lineColor);
        float angle = calculateAngle(x1, y1, x2, y2);
        if (svgGraphics2D == null || lineColor == null || borderColor == null) {
            throw new IllegalArgumentException("Invalid argument provided");
        }
        if (x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0) {
            throw new IllegalArgumentException("Coordinates cannot be negative");
        }
        Point2D.Float arrowTip = calculateArrowTip(x2, y2, angle);
        Point2D.Float[] basePoints = calculateBasePoints(arrowTip.x, arrowTip.y, angle);
        Point2D.Float[] trunkPoints = calculateTrunkPoints(x1, y1, angle, arrowTip.x, arrowTip.y);

        Path2D.Float arrowPath = buildArrowPath(trunkPoints, basePoints, x2, y2);
        drawArrow(svgGraphics2D, arrowPath, lineColor,borderColor);
    }

    /**
     * Validates arguments
     * @param svgGraphics2D - SVGGraphics2D object
     * @param x1 - x coordinate of the first point
     * @param y1 - y coordinate of the first point
     * @param x2 - x coordinate of the second point
     * @param y2 - y coordinate of the second point
     * @param lineColor - color of the arrow
     */
    private void validateArguments(SVGGraphics2D svgGraphics2D, float x1, float y1, float x2, float y2, Color lineColor) {
        if (svgGraphics2D == null || lineColor == null) {
            if(x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0)
                throw new IllegalArgumentException("Coordinates cannot be negative");
            throw new IllegalArgumentException("Arguments cannot be null");
        }
    }

    /**
     * Calculates angle between two points
     * @param x1 - x coordinate of the first point
     * @param y1 - y coordinate of the first point
     * @param x2 - x coordinate of the second point
     * @param y2 - y coordinate of the second point
     * @return angle between two points
     */
    float calculateAngle(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.atan2(dy, dx);
    }

    /**
     * Calculates arrow tip
     * @param x2 - x coordinate of the second point
     * @param y2 - y coordinate of the second point
     * @param angle - angle between two points
     * @return arrow tip
     */
    private Point2D.Float calculateArrowTip(float x2, float y2, float angle) {
        float ARROW_LENGTH = 12.0f;
        return new Point.Float(
                x2 - ARROW_LENGTH * (float) Math.cos(angle),
                y2 - ARROW_LENGTH * (float) Math.sin(angle)
        );
    }

    /**
     * Calculates base points
     * @param ax - x coordinate of the arrow tip
     * @param ay - y coordinate of the arrow tip
     * @param angle - angle between two points
     * @return base points
     */
    Point2D.Float[] calculateBasePoints(float ax, float ay, float angle) {
        float ARROW_WIDTH = 8.0f;
        return new Point2D.Float[]{
                new Point.Float(ax - ARROW_WIDTH * (float) Math.sin(angle), ay + ARROW_WIDTH * (float) Math.cos(angle)),
                new Point.Float(ax + ARROW_WIDTH * (float) Math.sin(angle), ay - ARROW_WIDTH * (float) Math.cos(angle))
        };
    }

    /**
     * Calculates trunk points
     * @param x1 - x coordinate of the first point
     * @param y1 - y coordinate of the first point
     * @param angle - angle between two points
     * @param ax - x coordinate of the arrow tip
     * @param ay - y coordinate of the arrow tip
     * @return trunk points
     */
    Point2D.Float[] calculateTrunkPoints(float x1, float y1, float angle, float ax, float ay) {
        float TRUNK_WIDTH = 4.0f;
        return new Point2D.Float[]{
                new Point.Float(x1 - TRUNK_WIDTH * (float) Math.sin(angle), y1 + TRUNK_WIDTH * (float) Math.cos(angle)),
                new Point.Float(x1 + TRUNK_WIDTH * (float) Math.sin(angle), y1 - TRUNK_WIDTH * (float) Math.cos(angle)),
                new Point.Float(ax - TRUNK_WIDTH * (float) Math.sin(angle), ay + TRUNK_WIDTH * (float) Math.cos(angle)),
                new Point.Float(ax + TRUNK_WIDTH * (float) Math.sin(angle), ay - TRUNK_WIDTH * (float) Math.cos(angle))
        };
    }

    /**
     * Builds arrow path
     * @param trunkPoints - trunk points
     * @param basePoints - base points
     * @param x2 - x coordinate of the second point
     * @param y2 - y coordinate of the second point
     * @return arrow path
     */
    private Path2D.Float buildArrowPath(Point2D.Float[] trunkPoints, Point2D.Float[] basePoints, float x2, float y2) {
        Path2D.Float arrowPath = new Path2D.Float();
        arrowPath.moveTo(trunkPoints[0].x, trunkPoints[0].y);
        arrowPath.lineTo(trunkPoints[2].x, trunkPoints[2].y);
        arrowPath.lineTo(basePoints[0].x, basePoints[0].y);
        arrowPath.lineTo(x2, y2);
        arrowPath.lineTo(basePoints[1].x, basePoints[1].y);
        arrowPath.lineTo(trunkPoints[3].x, trunkPoints[3].y);
        arrowPath.lineTo(trunkPoints[1].x, trunkPoints[1].y);
        arrowPath.closePath();
        return arrowPath;
    }

    /**
     * Draws arrow
     * @param svgGraphics2D - SVGGraphics2D object
     * @param arrowPath - arrow path
     * @param lineColor - color of the arrow
     * @param borderColor - color of the border
     */
    private void drawArrow(SVGGraphics2D svgGraphics2D, Path2D.Float arrowPath, Color lineColor, Color borderColor) {
        svgGraphics2D.setPaint(lineColor);
        svgGraphics2D.fill(arrowPath);
        svgGraphics2D.setStroke(STROKE);
        svgGraphics2D.setPaint(borderColor);
        svgGraphics2D.draw(arrowPath);
    }
}