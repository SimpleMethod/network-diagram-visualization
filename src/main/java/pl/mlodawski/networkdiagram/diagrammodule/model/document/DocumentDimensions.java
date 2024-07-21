package pl.mlodawski.networkdiagram.diagrammodule.model.document;


import lombok.Data;


@Data
public class DocumentDimensions {

    private int width;
    private int height;

    public DocumentDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

}
