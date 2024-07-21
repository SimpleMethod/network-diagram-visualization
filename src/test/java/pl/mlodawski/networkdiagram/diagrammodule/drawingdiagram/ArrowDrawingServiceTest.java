package pl.mlodawski.networkdiagram.diagrammodule.drawingdiagram;

import org.jfree.svg.SVGGraphics2D;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;
import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class ArrowDrawingServiceTest {

    @Test
    void drawArrow_ShouldWorkCorrectlyWithValidInputs() {
        // Arrange
        SVGGraphics2D svgGraphics2D= mock(SVGGraphics2D.class);
        ArrowDrawingService arrowDrawingService = new ArrowDrawingService();
        float x1 = 0;
        float y1 = 0;
        float x2 = 0;
        float y2 = 0;
        Color lineColor = Color.RED;
        Color borderColor = Color.BLACK;

        // Act
        arrowDrawingService.drawArrow(svgGraphics2D, x1, y1, x2, y2, lineColor, borderColor);

        // Assert
        verify(svgGraphics2D).setPaint(lineColor);
        verify(svgGraphics2D).setPaint(borderColor);
    }

    @Test
    void drawArrow_ShouldThrowIllegalArgumentExceptionForInvalidSVGGraphics2D() {
        // Arrange
        ArrowDrawingService arrowDrawingService = new ArrowDrawingService();
        float x1 = 0;
        float y1 = 0;
        float x2 = 0;
        float y2 = 0;
        Color lineColor = Color.RED;
        Color borderColor = Color.BLACK;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> arrowDrawingService.drawArrow(null, x1, y1, x2, y2, lineColor, borderColor));
    }

    @Test
    void drawArrow_ShouldThrowIllegalArgumentExceptionForInvalidLineColor() {
        // Arrange
        ArrowDrawingService arrowDrawingService = new ArrowDrawingService();
        SVGGraphics2D svgGraphics2D = mock(SVGGraphics2D.class);
        float x1 = 0;
        float y1 = 0;
        float x2 = 0;
        float y2 = 0;
        Color borderColor = Color.BLACK;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> arrowDrawingService.drawArrow(svgGraphics2D, x1, y1, x2, y2, null, borderColor));
    }

    @Test
    void drawArrow_ShouldThrowIllegalArgumentExceptionForNegativeCoordinates() {
        // Arrange
        ArrowDrawingService arrowDrawingService = new ArrowDrawingService();
        SVGGraphics2D svgGraphics2D = mock(SVGGraphics2D.class);
        float x1 = -1;
        float y1 = -1;
        float x2 = -1;
        float y2 = -1;
        Color lineColor = Color.RED;
        Color borderColor = Color.BLACK;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> arrowDrawingService.drawArrow(svgGraphics2D, x1, y1, x2, y2, lineColor, borderColor));
    }

    @Test
    void calculateAngle_ReturnsCorrectAngleForGivenCoordinates() {
        // Arrange
        ArrowDrawingService arrowDrawingService = new ArrowDrawingService();
        float x1 = 0;
        float y1 = 0;
        float x2 = 10;
        float y2 = 10;
        float expectedAngle = (float) Math.PI / 4; // 45 degrees in radians

        // Act
        float resultAngle = arrowDrawingService.calculateAngle(x1, y1, x2, y2);

        // Assert
        assertEquals(expectedAngle, resultAngle, 0.01, "The calculated angle should be 45 degrees in radians.");
    }

    @Test
    void calculateBasePoints_ReturnsCorrectBasePointsForGivenParameters() {
        // Arrange
        ArrowDrawingService arrowDrawingService = new ArrowDrawingService();
        float ax = 10.0f;
        float ay = 10.0f;
        float angle = (float) Math.PI / 4; // 45 degrees in radians
        Point2D.Float[] expectedBasePoints = new Point2D.Float[]{
                new Point.Float(
                        ax - 8.0f * (float) Math.sin(angle),
                        ay + 8.0f * (float) Math.cos(angle)
                ),
                new Point.Float(
                        ax + 8.0f * (float) Math.sin(angle),
                        ay - 8.0f * (float) Math.cos(angle)
                )
        };

        // Act
        Point2D.Float[] basePoints = arrowDrawingService.calculateBasePoints(ax, ay, angle);

        // Assert
        assertEquals(expectedBasePoints[0].x, basePoints[0].x, 0.01, "The x coordinate of the first base point should match the expected value.");
        assertEquals(expectedBasePoints[0].y, basePoints[0].y, 0.01, "The y coordinate of the first base point should match the expected value.");
        assertEquals(expectedBasePoints[1].x, basePoints[1].x, 0.01, "The x coordinate of the second base point should match the expected value.");
        assertEquals(expectedBasePoints[1].y, basePoints[1].y, 0.01, "The y coordinate of the second base point should match the expected value.");
    }

    @Test
    void calculateTrunkPoints_ReturnsCorrectTrunkPointsForGivenParameters() {
        // Arrange
        ArrowDrawingService arrowDrawingService = new ArrowDrawingService();
        float x1 = 10.0f;
        float y1 = 10.0f;
        float angle = (float) Math.PI / 4; // 45 degrees in radians
        float ax = 20.0f;
        float ay = 20.0f;
        float TRUNK_WIDTH = 4.0f;
        Point2D.Float[] expectedTrunkPoints = new Point2D.Float[]{
                new Point.Float(x1 - TRUNK_WIDTH * (float) Math.sin(angle), y1 + TRUNK_WIDTH * (float) Math.cos(angle)),
                new Point.Float(x1 + TRUNK_WIDTH * (float) Math.sin(angle), y1 - TRUNK_WIDTH * (float) Math.cos(angle)),
                new Point.Float(ax - TRUNK_WIDTH * (float) Math.sin(angle), ay + TRUNK_WIDTH * (float) Math.cos(angle)),
                new Point.Float(ax + TRUNK_WIDTH * (float) Math.sin(angle), ay - TRUNK_WIDTH * (float) Math.cos(angle))
        };

        // Act
        Point2D.Float[] trunkPoints = arrowDrawingService.calculateTrunkPoints(x1, y1, angle, ax, ay);

        // Assert
        for (int i = 0; i < trunkPoints.length; i++) {
            assertEquals(expectedTrunkPoints[i].x, trunkPoints[i].x, 0.01, "The x coordinate of trunk point " + i + " should match the expected value.");
            assertEquals(expectedTrunkPoints[i].y, trunkPoints[i].y, 0.01, "The y coordinate of trunk point " + i + " should match the expected value.");
        }
    }

    
}