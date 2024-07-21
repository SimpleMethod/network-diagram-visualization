package pl.mlodawski.networkdiagram.diagrammodule.jsoninterpreter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pl.mlodawski.networkdiagram.diagrammodule.model.command.NetworkDiagramCommand;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.NetworkLink;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.NetworkNode;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.SuperNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Test cases for JsonInterpreter class
 */
@SpringBootTest
public class JsonInterpreterTest {

    /**
     * Test method for interpretJson of class JsonInterpreter for NetworkNode creation
     */
    @Test
    public void testInterpretJsonForNetworkNodeCreation() {

        NetworkDiagramCommand command = new NetworkDiagramCommand();
        List<NetworkNode> nodes = new ArrayList<>();
        List<SuperNode> superNodes = new ArrayList<>();
        List<NetworkLink> links = new ArrayList<>();

        JsonInterpreter interpreter = new JsonInterpreter();
        interpreter.interpretJson(command, nodes, superNodes, links);

        Assertions.assertEquals(0, nodes.size());
    }

    /**
     * Test method for interpretJson of class JsonInterpreter for SuperNode creation
     */
    @Test
    public void testInterpretJsonForSuperNodeCreation() {

        NetworkDiagramCommand command = new NetworkDiagramCommand();
        List<NetworkNode> nodes = new ArrayList<>();
        List<SuperNode> superNodes = new ArrayList<>();
        List<NetworkLink> links = new ArrayList<>();

        JsonInterpreter interpreter = new JsonInterpreter();
        interpreter.interpretJson(command, nodes, superNodes, links);

        Assertions.assertEquals(0, superNodes.size());
    }

    /**
     * Test method for interpretJson of class JsonInterpreter for NetworkLink creation
     */
    @Test
    public void testInterpretJsonForNetworkLinkCreation() {

        NetworkDiagramCommand command = new NetworkDiagramCommand();
        List<NetworkNode> nodes = new ArrayList<>();
        List<SuperNode> superNodes = new ArrayList<>();
        List<NetworkLink> links = new ArrayList<>();

        JsonInterpreter interpreter = new JsonInterpreter();
        interpreter.interpretJson(command, nodes, superNodes, links);

        Assertions.assertEquals(0, links.size());
    }

    /**
     * Test method for interpretJson of class JsonInterpreter for exception scenario
     */
    @Test
    public void testInterpretJsonForExceptionScenario() {

        List<NetworkNode> nodes = new ArrayList<>();
        List<SuperNode> superNodes = new ArrayList<>();
        List<NetworkLink> links = new ArrayList<>();

        JsonInterpreter interpreter = new JsonInterpreter();

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                interpreter.interpretJson(null, nodes, superNodes, links));
    }
}