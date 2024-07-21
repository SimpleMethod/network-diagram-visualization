package pl.mlodawski.networkdiagram.diagrammodule.model.document;

import lombok.Data;

@Data
public class NetworkLink {
    private String sourceNodeId;
    private String targetNodeId;
    private float incomingTraffic;
    private float outgoingTraffic;

    public NetworkLink(String sourceNodeId, String targetNodeId, float incomingTraffic, float outgoingTraffic) {
        this.sourceNodeId = sourceNodeId;
        this.targetNodeId = targetNodeId;
        this.incomingTraffic = incomingTraffic;
        this.outgoingTraffic = outgoingTraffic;
    }

}
