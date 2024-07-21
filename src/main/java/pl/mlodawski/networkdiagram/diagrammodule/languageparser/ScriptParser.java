package pl.mlodawski.networkdiagram.diagrammodule.languageparser;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.mlodawski.networkdiagram.diagrammodule.drawingdiagram.NetworkNodeUtilsService;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.NetworkLink;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.NetworkNode;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.NodePosition;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.SuperNode;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
@Slf4j
public class ScriptParser {

    private final NetworkNodeUtilsService networkNodeUtilsService;


    /**
     * Method parses line from script and returns NetworkNode object
     * @param line line from script
     * @return NetworkNode object
     */
    public NetworkNode parseNetworkNode(String line) {
        Pattern pattern = Pattern.compile("Node N(\\d+): Position\\(([^,]+), ([^)]+)\\), Size\\(([^,]+), ([^)]+)\\), Label\\(\"([^\"]+)\"\\)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String id = matcher.group(1);
            float x = Float.parseFloat(matcher.group(2));
            float y = Float.parseFloat(matcher.group(3));
            float width = Float.parseFloat(matcher.group(4));
            float height = Float.parseFloat(matcher.group(5));
            String name = matcher.group(6);
            return new NetworkNode(id, x, y, width, height, name);
        }
        log.error("Error while parsing network node on line: {}",line);
        return null;
    }

    /**
     * Method parses line from script and returns SuperNode object
     * @param line line from script
     */
    public SuperNode parseSuperNode(String line) {
        Pattern pattern = Pattern.compile("SuperNode SN(\\d+): Position\\(([^,]+), ([^)]+)\\), Label\\(\"([^\"]+)\"\\)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String id = matcher.group(1);
            int x = (int) Double.parseDouble(matcher.group(2));
            int y = (int) Double.parseDouble(matcher.group(3));
            String name = matcher.group(4);
            return new SuperNode(id, x, y, name);
        }
        log.error("Error while parsing super node on line: {}",line);
        return null;
    }

    /**
     * Method parses line from script and returns NetworkLink object
     * @param line line from script
     * @return NetworkLink object
     */
    public NetworkLink parseNetworkLink(String line) {
        Pattern pattern = Pattern.compile("N(\\d+) ---\\[([^,]+),([^]]+)]--> N(\\d+)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String sourceNodeId = matcher.group(1);
            float outgoingTraffic = Float.parseFloat(matcher.group(2));
            float incomingTraffic = Float.parseFloat(matcher.group(3));
            String targetNodeId = matcher.group(4);
            return new NetworkLink(sourceNodeId, targetNodeId, incomingTraffic, outgoingTraffic);
        }
        log.error("Error while parsing network link on line: {}", line);
        return null;
    }

    /**
     * Method parses line from script and returns NetworkLink object
     * @param line line from script
     * @param allNodes list of all nodes
     * @param allSuperNodes list of all super nodes
     * @return NetworkLink object
     */
    public SuperNode parseNodePositionInSuperNode(String line, List<NetworkNode> allNodes, List<SuperNode> allSuperNodes) {
        // Pattern without position symbol
        Pattern pattern = Pattern.compile("N(\\d+) --([<>^v]?)--> SN(\\d+)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String nodeId = matcher.group(1);
            String positionSymbol = matcher.group(2);
            String superNodeId = matcher.group(3);

            NodePosition position = null;
            if (!positionSymbol.isEmpty()) {
                position = switch (positionSymbol) {
                    case "^" -> NodePosition.TOP;
                    case "v" -> NodePosition.BOTTOM;
                    case "<" -> NodePosition.LEFT;
                    case ">" -> NodePosition.RIGHT;
                    default -> throw new IllegalArgumentException("Invalid position symbol: " + positionSymbol);
                };
            }

            SuperNode superNode = allSuperNodes.stream()
                    .filter(node -> node.getId().equals(superNodeId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("SuperNode with ID " + superNodeId + " not found"));

            NetworkNode node = networkNodeUtilsService.findNodeById(allNodes, nodeId);
            if (node == null) {
                throw new IllegalArgumentException("NetworkNode with ID " + nodeId + " not found");
            }

            if (position != null) {
                superNode.addNode(node, position);
            } else {
                log.warn("No position symbol found for line: {}", line);
            }
            log.debug("Parsed line: " + line);
            return superNode;
        } else {
            log.error("Error while parsing on line: {}", line);
            throw new IllegalArgumentException("Invalid line format: " + line);
        }
    }

}
