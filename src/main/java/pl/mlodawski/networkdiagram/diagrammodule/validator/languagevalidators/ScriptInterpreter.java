package pl.mlodawski.networkdiagram.diagrammodule.validator.languagevalidators;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.NetworkLink;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.NetworkNode;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.SuperNode;
import pl.mlodawski.networkdiagram.diagrammodule.languageparser.ScriptParser;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class ScriptInterpreter {

    private final ScriptParser scriptParser;

    public void interpretScript(String script, List<NetworkNode> networkNodes, List<SuperNode> superNodes, List<NetworkLink> networkLinks) {
        String[] lines = script.split("\\n");
        log.debug("Interpreting script with {} lines", lines.length);
        for (String line : lines) {
            interpretLine(line.trim(), networkNodes, superNodes, networkLinks);
        }
        log.debug("Script interpreted successfully: {}{}{}", networkNodes.size(), superNodes.size(), networkLinks.size());
        validateScript(networkNodes, superNodes, networkLinks);
    }

    private void interpretLine(String line, List<NetworkNode> networkNodes, List<SuperNode> superNodes, List<NetworkLink> networkLinks) {
        NetworkNode node = scriptParser.parseNetworkNode(line);
        if (node != null) {
            networkNodes.add(node);
            return;
        }
        log.debug("Interpreting line: {}", line);
        NetworkLink link = scriptParser.parseNetworkLink(line);
        if (link != null) {
            networkLinks.add(link);
            return;
        }
        log.debug("Interpreting line: {}", line);
        SuperNode superNode = scriptParser.parseSuperNode(line);
        if (superNode != null) {
            superNodes.add(superNode);
            return;
        }
        log.debug("Interpreting line: {}", line);
        interpretNodePositionInSuperNode(line, networkNodes, superNodes);
    }

    private void interpretNodePositionInSuperNode(String line, List<NetworkNode> networkNodes, List<SuperNode> superNodes) {
        SuperNode updatedSuperNode = scriptParser.parseNodePositionInSuperNode(line, networkNodes, superNodes);
        if (updatedSuperNode != null) {
            superNodes.remove(updatedSuperNode);
            superNodes.add(updatedSuperNode);
            log.debug("Node position in SuperNode updated: {}", updatedSuperNode.getId());
        }
    }

    private void validateScript(List<NetworkNode> networkNodes, List<SuperNode> superNodes, List<NetworkLink> networkLinks) {
        Set<String> uniqueNodeIds = new HashSet<>();
        Set<String> uniqueSuperNodeIds = new HashSet<>();
        Set<String> nodeIdsInSuperNodes = new HashSet<>();

        for (NetworkNode node : networkNodes) {
            if (!uniqueNodeIds.add(node.getId())) {
                log.error("Duplicate Node ID: {}", node.getId());
                throw new IllegalArgumentException("Duplicate Node ID: " + node.getId());
            }
        }

        for (SuperNode superNode : superNodes) {
            if (!uniqueSuperNodeIds.add(superNode.getId())) {
                log.error("Duplicate SuperNode ID: {}", superNode.getId());
                throw new IllegalArgumentException("Duplicate SuperNode ID: " + superNode.getId());
            }
        }

        for (NetworkLink link : networkLinks) {
            if (!uniqueNodeIds.contains(link.getSourceNodeId()) || !uniqueNodeIds.contains(link.getTargetNodeId())) {
                log.error("Invalid link between non-existing nodes: {} --> {}", link.getSourceNodeId(), link.getTargetNodeId());
                throw new IllegalArgumentException("Invalid link between non-existing nodes: " + link.getSourceNodeId() + " --> " + link.getTargetNodeId());
            }
        }

        for (SuperNode superNode : superNodes) {
            validateNodesInSuperNode(superNode, uniqueNodeIds, nodeIdsInSuperNodes);
        }
    }

    private void validateNodesInSuperNode(SuperNode superNode, Set<String> uniqueNodeIds, Set<String> nodeIdsInSuperNodes) {
        Set<String> nodeIdsInThisSuperNode = new HashSet<>();

        for (List<NetworkNode> nodeList : Arrays.asList(superNode.getTopNodes(), superNode.getBottomNodes(), superNode.getLeftNodes(), superNode.getRightNodes())) {
            for (NetworkNode node : nodeList) {
                String nodeId = node.getId();
                if (!uniqueNodeIds.contains(nodeId)) {
                    log.error("Invalid node in SuperNode: Node ID {} in SuperNode {}", nodeId, superNode.getId());
                    throw new IllegalArgumentException("Invalid node in SuperNode: Node ID " + nodeId + " in SuperNode " + superNode.getId());
                }
                if (!nodeIdsInThisSuperNode.add(nodeId)) {
                    log.error("Duplicate node in SuperNode: Node ID {} in SuperNode {}", nodeId, superNode.getId());
                    throw new IllegalArgumentException("Duplicate node in SuperNode: Node ID " + nodeId + " in SuperNode " + superNode.getId());
                }
                if (!nodeIdsInSuperNodes.add(nodeId)) {
                    log.error("Node in multiple SuperNodes: Node ID {}", nodeId);
                    throw new IllegalArgumentException("Node in multiple SuperNodes: Node ID " + nodeId);
                }
            }
        }

        if (nodeIdsInThisSuperNode.isEmpty()) {
            log.error("Empty SuperNode: SuperNode ID {}", superNode.getId());
            throw new IllegalArgumentException("Empty SuperNode: SuperNode ID " + superNode.getId());
        }
    }
}
