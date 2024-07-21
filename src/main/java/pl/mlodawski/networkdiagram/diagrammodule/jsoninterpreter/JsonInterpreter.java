package pl.mlodawski.networkdiagram.diagrammodule.jsoninterpreter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.mlodawski.networkdiagram.diagrammodule.model.command.NetworkDiagramCommand;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.NetworkLink;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.NetworkNode;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.SuperNode;


import java.util.*;

@Service
@Slf4j
public class JsonInterpreter {

    /**
     * Interprets JSON
     * @param networkJson JSON to interpret
     * @param networkNodes List of NetworkNodes
     * @param superNodes List of SuperNodes
     * @param networkLinks List of NetworkLinks
     */
    public void interpretJson(NetworkDiagramCommand networkJson, List<NetworkNode> networkNodes, List<SuperNode> superNodes, List<NetworkLink> networkLinks) {
        if (networkJson == null) {
            throw new IllegalArgumentException("NetworkDiagramCommand cannot be null");
        }

        Optional.ofNullable(networkJson.getNetworkNodes()).ifPresent(nodes -> nodes.forEach(nodeJson -> {
            var node = new NetworkNode(
                    nodeJson.getId(),
                    nodeJson.getPosition().getX(),
                    nodeJson.getPosition().getY(),
                    nodeJson.getSize().getWidth(),
                    nodeJson.getSize().getHeight(),
                    nodeJson.getLabel()
            );
            networkNodes.add(node);
        }));

        Optional.ofNullable(networkJson.getNetworkLinks()).ifPresent(links -> links.forEach(linkJson -> {
            var link = new NetworkLink(
                    linkJson.getSourceNodeId(),
                    linkJson.getTargetNodeId(),
                    linkJson.getRxTraffic(),
                    linkJson.getTxTraffic()
            );
            networkLinks.add(link);
        }));

        Optional.ofNullable(networkJson.getSuperNodes()).ifPresent(superNodesJson -> superNodesJson.forEach(superNodeJson -> {
            var superNode = new SuperNode(
                    superNodeJson.getId(),
                    (int) superNodeJson.getPosition().getX(),
                    (int) superNodeJson.getPosition().getY(),
                    superNodeJson.getLabel()
            );

            Optional.ofNullable(superNodeJson.getNodePositions()).ifPresent(nodePositions -> nodePositions.forEach(nodePositionJson ->
                    networkNodes.stream()
                            .filter(n -> Objects.equals(n.getId(), nodePositionJson.getNodeId()))
                            .findFirst().ifPresent(node -> superNode.addNode(node, nodePositionJson.getPosition()))
            ));

            superNodes.add(superNode);
        }));

        validateScript(networkNodes, superNodes, networkLinks);
    }

    /**
     * Validates the script
     * @param networkNodes List of NetworkNodes
     * @param superNodes List of SuperNodes
     * @param networkLinks List of NetworkLinks
     */
    private void validateScript(List<NetworkNode> networkNodes, List<SuperNode> superNodes, List<NetworkLink> networkLinks) {
        Set<String> uniqueNodeIds = networkNodes.stream().map(NetworkNode::getId).collect(java.util.stream.Collectors.toSet());
        Set<String> uniqueSuperNodeIds = superNodes.stream().map(SuperNode::getId).collect(java.util.stream.Collectors.toSet());
        Set<String> nodeIdsInSuperNodes = new HashSet<>();

        if (uniqueNodeIds.size() != networkNodes.size()) {
            log.error("Duplicate Node IDs found");
            throw new IllegalArgumentException("Duplicate Node IDs found");
        }

        if (uniqueSuperNodeIds.size() != superNodes.size()) {
            log.error("Duplicate SuperNode IDs found");
            throw new IllegalArgumentException("Duplicate SuperNode IDs found");
        }

        networkLinks.forEach(link -> {
            if (!uniqueNodeIds.contains(link.getSourceNodeId()) || !uniqueNodeIds.contains(link.getTargetNodeId())) {
                log.error("Invalid link between non-existing nodes: {} --> {}", link.getSourceNodeId(), link.getTargetNodeId());
                throw new IllegalArgumentException("Invalid link between non-existing nodes: " + link.getSourceNodeId() + " --> " + link.getTargetNodeId());
            }
        });

        superNodes.forEach(superNode -> validateNodesInSuperNode(superNode, uniqueNodeIds, nodeIdsInSuperNodes));
    }

    /**
     * Super Node validation
     * @param superNode SuperNode to validate
     * @param uniqueNodeIds Set of unique node IDs
     * @param nodeIdsInSuperNodes Set of node IDs in SuperNodes
     */
    private void validateNodesInSuperNode(SuperNode superNode, Set<String> uniqueNodeIds, Set<String> nodeIdsInSuperNodes) {
        Set<String> nodeIdsInThisSuperNode = new HashSet<>();

        superNode.getTopNodes().forEach(node -> validateNodeInSuperNode(node, superNode, uniqueNodeIds, nodeIdsInSuperNodes, nodeIdsInThisSuperNode));
        superNode.getBottomNodes().forEach(node -> validateNodeInSuperNode(node, superNode, uniqueNodeIds, nodeIdsInSuperNodes, nodeIdsInThisSuperNode));
        superNode.getLeftNodes().forEach(node -> validateNodeInSuperNode(node, superNode, uniqueNodeIds, nodeIdsInSuperNodes, nodeIdsInThisSuperNode));
        superNode.getRightNodes().forEach(node -> validateNodeInSuperNode(node, superNode, uniqueNodeIds, nodeIdsInSuperNodes, nodeIdsInThisSuperNode));

        if (nodeIdsInThisSuperNode.isEmpty()) {
            log.error("Empty SuperNode: SuperNode ID {}", superNode.getId());
            throw new IllegalArgumentException("Empty SuperNode: SuperNode ID " + superNode.getId());
        }
    }

    /**
     * Validates a node within a SuperNode
     * @param node Node to validate
     * @param superNode SuperNode containing the node
     * @param uniqueNodeIds Set of unique node IDs
     * @param nodeIdsInSuperNodes Set of node IDs in SuperNodes
     * @param nodeIdsInThisSuperNode Set of node IDs in the current SuperNode
     */
    private void validateNodeInSuperNode(NetworkNode node, SuperNode superNode, Set<String> uniqueNodeIds, Set<String> nodeIdsInSuperNodes, Set<String> nodeIdsInThisSuperNode) {
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