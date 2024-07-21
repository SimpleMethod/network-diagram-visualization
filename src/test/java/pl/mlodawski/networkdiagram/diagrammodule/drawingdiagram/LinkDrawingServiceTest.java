package pl.mlodawski.networkdiagram.diagrammodule.drawingdiagram;

import org.jfree.svg.SVGGraphics2D;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import pl.mlodawski.networkdiagram.diagrammodule.linkcolorizes.ColorStrategy;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.DocumentStyle;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.FontStyle;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.NetworkLink;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.NetworkNode;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.geom.Point2D;

import static pl.mlodawski.networkdiagram.diagrammodule.linkcolorizes.ColorStrategy.RED_GREEN;

@SpringBootTest
class LinkDrawingServiceTest {

    private final NetworkNodeUtilsService networkNodeUtilsService = Mockito.mock(NetworkNodeUtilsService.class);
    private final ArrowDrawingService arrowDrawingService = Mockito.mock(ArrowDrawingService.class);
    private final LinkDrawingService linkDrawingService = new LinkDrawingService(networkNodeUtilsService, arrowDrawingService);
    private final SVGGraphics2D svgGraphics2D = new SVGGraphics2D(500, 500);

    @Test
    void drawLink_NodesAreNotNull_DrawsArrowsTwice() {
        // Arrange
        NetworkNode node1 = new NetworkNode("1", 10.0f, 20.0f, 5.0f, 5.0f, "Node 1");
        NetworkNode node2 = new NetworkNode("2", 30.0f, 40.0f, 5.0f, 5.0f, "Node 2");
        List<NetworkNode> nodes = new ArrayList<>();
        nodes.add(node1);
        nodes.add(node2);
        Mockito.when(networkNodeUtilsService.findNodeById(nodes, "1")).thenReturn(node1);
        Mockito.when(networkNodeUtilsService.findNodeById(nodes, "2")).thenReturn(node2);
        NetworkLink link = new NetworkLink("1", "2", 50.0f, 60.0f);
        Point2D.Float sourcePoint = new Point2D.Float(10.0f, 20.0f);
        Point2D.Float targetPoint = new Point2D.Float(30.0f, 40.0f);
        DocumentStyle documentStyle = new DocumentStyle("Arial", 10, "#000000", FontStyle.PLAIN,
                "#FFFFFF", "#000000", "#FFFFFF", RED_GREEN, true, "Title", true);

        // Act
        linkDrawingService.drawLink(svgGraphics2D, link, nodes, sourcePoint, targetPoint, documentStyle);

        // Assert
        Mockito.verify(arrowDrawingService, Mockito.times(2))
                .drawArrow(Mockito.any(SVGGraphics2D.class), Mockito.anyFloat(), Mockito.anyFloat(),
                        Mockito.anyFloat(), Mockito.anyFloat(), Mockito.any(Color.class),
                        Mockito.any(Color.class));
    }

    @Test
    void drawLink_SourceNodeIsNull_DoesNotDrawArrow() {
        // Arrange
        List<NetworkNode> nodes = new ArrayList<>();
        NetworkNode node2 = new NetworkNode("2", 30.0f, 40.0f, 5.0f, 5.0f, "Node 2");
        Mockito.when(networkNodeUtilsService.findNodeById(nodes, "1")).thenReturn(null);
        Mockito.when(networkNodeUtilsService.findNodeById(nodes, "2")).thenReturn(node2);
        NetworkLink link = new NetworkLink("1", "2", 50.0f, 60.0f);
        Point2D.Float sourcePoint = new Point2D.Float(10.0f, 20.0f);
        Point2D.Float targetPoint = new Point2D.Float(30.0f, 40.0f);
        DocumentStyle documentStyle = new DocumentStyle("Arial", 10, "#000000", FontStyle.PLAIN,
                "#FFFFFF", "#000000", "#FFFFFF", RED_GREEN, true, "Title", true);

        // Act
        linkDrawingService.drawLink(svgGraphics2D, link, nodes, sourcePoint, targetPoint, documentStyle);

        // Assert
        Mockito.verify(arrowDrawingService, Mockito.times(0))
                .drawArrow(Mockito.any(SVGGraphics2D.class), Mockito.anyFloat(), Mockito.anyFloat(),
                        Mockito.anyFloat(), Mockito.anyFloat(), Mockito.any(Color.class),
                        Mockito.any(Color.class));
    }


    @Test
    void drawLink_TargetNodeIsNull_DoesNotDrawArrow() {
        // Arrange
        List<NetworkNode> nodes = new ArrayList<>();
        NetworkNode node1 = new NetworkNode("1", 10.0f, 20.0f, 5.0f, 5.0f, "Node 1");
        nodes.add(node1); // Adding node1 to the list to simulate a real scenario

        Mockito.when(networkNodeUtilsService.findNodeById(nodes, "1")).thenReturn(node1);
        Mockito.when(networkNodeUtilsService.findNodeById(nodes, "2")).thenReturn(null);

        NetworkLink link = new NetworkLink("1", "2", 50.0f, 60.0f);
        Point2D.Float sourcePoint = new Point2D.Float(10.0f, 20.0f);
        Point2D.Float targetPoint = new Point2D.Float(30.0f, 40.0f);
        DocumentStyle documentStyle = new DocumentStyle("Arial", 10, "#000000", FontStyle.PLAIN,
                "#FFFFFF", "#000000", "#FFFFFF", RED_GREEN, true,
                "Title", true);

        // Act
        linkDrawingService.drawLink(svgGraphics2D, link, nodes, sourcePoint, targetPoint, documentStyle);

        // Assert
        Mockito.verify(arrowDrawingService, Mockito.times(0))
                .drawArrow(Mockito.any(SVGGraphics2D.class), Mockito.anyFloat(), Mockito.anyFloat(),
                        Mockito.anyFloat(), Mockito.anyFloat(), Mockito.any(Color.class),
                        Mockito.any(Color.class));
    }

    @Test
    void drawTrafficLabels_WhenPercentageIsZero() {
        int percentage = 0;
        Point2D.Float start = new Point2D.Float(10.0f, 20.0f);
        Point2D.Float end = new Point2D.Float(30.0f, 40.0f);
        DocumentStyle documentStyle = new DocumentStyle("Arial", 10, "#000000", FontStyle.PLAIN,
                "#FFFFFF", "#000000", "#FFFFFF", RED_GREEN, true,
                "Title", true);

        SVGGraphics2D spySvgGraphics2D = Mockito.spy(svgGraphics2D);
        linkDrawingService.drawTrafficLabels(spySvgGraphics2D, start, end, percentage, documentStyle);

        Mockito.verify(spySvgGraphics2D, Mockito.times(1)).drawString(Mockito.eq(percentage + "%"), Mockito.anyFloat(), Mockito.anyFloat());
    }

    @Test
    void drawTrafficLabels_WhenPercentageIsNonzero() {
        int percentage = 20;
        Point2D.Float start = new Point2D.Float(10.0f, 20.0f);
        Point2D.Float end = new Point2D.Float(30.0f, 40.0f);
        DocumentStyle documentStyle = new DocumentStyle("Arial", 10, "#000000", FontStyle.PLAIN,
                "#FFFFFF", "#000000", "#FFFFFF", RED_GREEN, true,
                "Title", true);

        SVGGraphics2D spySvgGraphics2D = Mockito.spy(svgGraphics2D);
        linkDrawingService.drawTrafficLabels(spySvgGraphics2D, start, end, percentage, documentStyle);

        Mockito.verify(spySvgGraphics2D, Mockito.times(1)).drawString(Mockito.eq(percentage + "%"), Mockito.anyFloat(), Mockito.anyFloat());
    }


}
