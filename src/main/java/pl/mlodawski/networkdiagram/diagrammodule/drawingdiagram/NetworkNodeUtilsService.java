package pl.mlodawski.networkdiagram.diagrammodule.drawingdiagram;

import org.springframework.stereotype.Service;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.NetworkNode;

import java.util.List;
import java.util.Objects;

@Service
public class NetworkNodeUtilsService {

    /**
     * Finds a node by id
     * @param nodes list of nodes
     * @param id id of node
     * @return node
     */
    public NetworkNode findNodeById(List<NetworkNode> nodes, String id) {
        for (NetworkNode node : nodes) {
            if (Objects.equals(node.getId(), id)) {
                return node;
            }
        }
        return null;
    }

}
