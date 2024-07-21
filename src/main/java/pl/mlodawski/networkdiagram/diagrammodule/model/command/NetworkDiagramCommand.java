package pl.mlodawski.networkdiagram.diagrammodule.model.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class NetworkDiagramCommand {

    @JsonProperty("config")
    private DiagramConfig diagramConfig;

    @JsonProperty("nodes")
    private List<NetworkNodeJson> networkNodes;

    @JsonProperty("superNodes")
    private List<SuperNodeJson> superNodes;

    @JsonProperty("links")
    private List<NetworkLinkJson> networkLinks;


}

