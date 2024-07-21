package pl.mlodawski.networkdiagram.diagrammodule.model.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class SizeJson {

    @JsonProperty("width")
    private float width;

    @JsonProperty("height")
    private float height;


}
