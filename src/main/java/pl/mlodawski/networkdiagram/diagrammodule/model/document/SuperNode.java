package pl.mlodawski.networkdiagram.diagrammodule.model.document;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SuperNode {
    private String id;
    private int x;
    private int y;
    private int width;
    private int height;
    private String name;
    private List<NetworkNode> topNodes = new ArrayList<>();
    private List<NetworkNode> bottomNodes = new ArrayList<>();
    private List<NetworkNode> leftNodes = new ArrayList<>();
    private List<NetworkNode> rightNodes = new ArrayList<>();


    public SuperNode(int x, int y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    public SuperNode(int x, int y, int width, int height, String name) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.name = name;
    }

    public SuperNode(String id, int x, int y, String name) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.name = name;
    }

    public void addNode(NetworkNode node, NodePosition position) {
        switch (position) {
            case TOP:
                topNodes.add(node);
                break;
            case BOTTOM:
                bottomNodes.add(node);
                break;
            case LEFT:
                leftNodes.add(node);
                break;
            case RIGHT:
                rightNodes.add(node);
                break;
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }


}