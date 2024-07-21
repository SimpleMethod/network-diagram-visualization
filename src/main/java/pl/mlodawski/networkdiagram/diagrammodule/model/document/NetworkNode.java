package pl.mlodawski.networkdiagram.diagrammodule.model.document;

import lombok.Data;

@Data
public class NetworkNode {
    private String id;
    private float x;
    private float y;
    private float width;
    private float height;
    private String name;

    public NetworkNode(String id, float x, float y, float width, float height, String name) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.name = name;
    }

    public NetworkNode(float x, float y, float width, float height, String name) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.name = name;
    }
}

