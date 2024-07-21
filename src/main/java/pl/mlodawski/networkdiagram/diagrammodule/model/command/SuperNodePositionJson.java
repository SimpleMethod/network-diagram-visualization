package pl.mlodawski.networkdiagram.diagrammodule.model.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.NodePosition;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class SuperNodePositionJson {

    @JsonProperty("nodeId")
    private String nodeId;

    @JsonProperty("position")
    private NodePosition position;


}
