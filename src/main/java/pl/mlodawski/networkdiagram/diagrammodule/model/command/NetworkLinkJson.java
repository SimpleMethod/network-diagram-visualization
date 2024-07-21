package pl.mlodawski.networkdiagram.diagrammodule.model.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class NetworkLinkJson {

    @JsonProperty("sourceNodeId")
    private String sourceNodeId;

    @JsonProperty("targetNodeId")
    private String targetNodeId;

    @JsonProperty("txTraffic")
    private float txTraffic;

    @JsonProperty("rxTraffic")
    private float rxTraffic;


}
