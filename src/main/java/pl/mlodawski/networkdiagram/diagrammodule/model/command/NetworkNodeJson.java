package pl.mlodawski.networkdiagram.diagrammodule.model.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class NetworkNodeJson {

    @JsonProperty("id")
    private String id;

    @JsonProperty("position")
    private PositionJson position;

    @JsonProperty("size")
    private SizeJson size;

    @JsonProperty("label")
    private String label;


}
