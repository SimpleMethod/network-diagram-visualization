package pl.mlodawski.networkdiagram.diagrammodule.model.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class SuperNodeJson {

    @JsonProperty("id")
    private String id;

    @JsonProperty("position")
    private PositionJson position;

    @JsonProperty("label")
    private String label;

    @JsonProperty("nodePositions")
    private List<SuperNodePositionJson> nodePositions;


}
