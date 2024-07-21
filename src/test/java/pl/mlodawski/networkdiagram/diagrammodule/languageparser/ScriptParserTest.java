package pl.mlodawski.networkdiagram.diagrammodule.languageparser;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import pl.mlodawski.networkdiagram.diagrammodule.drawingdiagram.NetworkNodeUtilsService;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.NetworkNode;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.SuperNode;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ScriptParserTest {

  private NetworkNodeUtilsService networkNodeUtilsService = new NetworkNodeUtilsService();
  private ScriptParser scriptParser = new ScriptParser(networkNodeUtilsService);

  @Test
  void parseNetworkNode_validInput_returnsNetworkNode() {
    String line = "Node N1: Position(10.0, 20.0), Size(100.0, 200.0), Label(\"Node1\")";

    NetworkNode networkNode = scriptParser.parseNetworkNode(line);

    assertNotNull(networkNode);
    assertEquals("1", networkNode.getId());
    assertEquals(10.0, networkNode.getX(), 0.01);
    assertEquals(20.0, networkNode.getY(), 0.01);
    assertEquals(100.0, networkNode.getWidth(), 0.01);
    assertEquals(200.0, networkNode.getHeight(), 0.01);
    assertEquals("Node1", networkNode.getName());
  }

  @Test
  void parseNetworkNode_invalidInput_returnsNull() {
    String line = "Invalid line";

    NetworkNode networkNode = scriptParser.parseNetworkNode(line);

    assertNull(networkNode);
  }

  @Test
  void parseSuperNode_validInput_returnsSuperNode() {
    String line = "SuperNode SN1: Position(30.0, 40.0), Label(\"SuperNode1\")";

    SuperNode superNode = scriptParser.parseSuperNode(line);

    assertNotNull(superNode);
    assertEquals("1", superNode.getId());
    assertEquals(30, superNode.getX());
    assertEquals(40, superNode.getY());
    assertEquals("SuperNode1", superNode.getName());
  }

  @Test
  void parseSuperNode_invalidInput_returnsNull() {
    String line = "Invalid line";

    SuperNode superNode = scriptParser.parseSuperNode(line);

    assertNull(superNode);
  }
  @Test
  void parseNodePositionInSuperNode_validInput_returnsSuperNode() {
    String line = "N1 --v--> SN1";
    List<NetworkNode> allNodes = new ArrayList<>();
    NetworkNode node = new NetworkNode("1", 10, 20, 100, 200, "Node1");
    allNodes.add(node);
    List<SuperNode> allSuperNodes = new ArrayList<>();
    SuperNode superNode = new SuperNode("1", 30, 40, "SuperNode1");
    allSuperNodes.add(superNode);

    SuperNode parsedSuperNode = scriptParser.parseNodePositionInSuperNode(line, allNodes, allSuperNodes);
    assertNotNull(parsedSuperNode, "Parsed SuperNode should not be null");
    assertEquals("1", parsedSuperNode.getId());
    assertEquals(30, parsedSuperNode.getX());
    assertEquals(40, parsedSuperNode.getY());
    assertEquals("SuperNode1", parsedSuperNode.getName());
  }

}
