package pl.mlodawski.networkdiagram.diagrammodule.networkcontroller;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.mlodawski.networkdiagram.diagrammodule.drawingdiagram.NetworkDrawingService;
import pl.mlodawski.networkdiagram.diagrammodule.jsoninterpreter.JsonInterpreter;
import pl.mlodawski.networkdiagram.diagrammodule.linkcolorizes.ColorStrategy;
import pl.mlodawski.networkdiagram.diagrammodule.model.command.DiagramConfig;
import pl.mlodawski.networkdiagram.diagrammodule.model.command.NetworkDiagramCommand;
import pl.mlodawski.networkdiagram.diagrammodule.validator.languagevalidators.ScriptInterpreter;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.FontStyle;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.NetworkLink;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.NetworkNode;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.SuperNode;
import pl.mlodawski.networkdiagram.diagrammodule.model.document.DocumentStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/diagram")
public class NetworkDrawingController {

    private final ScriptInterpreter interpreter;

    private final JsonInterpreter jsonInterpreter;

    private final NetworkDrawingService networkDrawingService;



    @PostMapping(value = "/nl", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    public ResponseEntity<byte[]> genDiagramSvgByNaturalLanguage(@RequestBody String diagramDescription) {
        log.debug("Received diagram description: " + diagramDescription + " from user");
        List<NetworkNode> networkNodes = new ArrayList<>();
        List<SuperNode> superNodes = new ArrayList<>();
        List<NetworkLink> networkLinks = new ArrayList<>();
        log.debug("Interpreting diagram description");
        interpreter.interpretScript(diagramDescription, networkNodes, superNodes, networkLinks);
        log.info("Interpreted diagram description successfully");
        DocumentStyle documentStyle = new DocumentStyle("Arial", 12, "#000000", FontStyle.PLAIN, "#ffffff", "#000000", "#ffffff", ColorStrategy.RED_GREEN,true,"true",true);
        log.debug("Drawing diagram");
        StringBuilder file = networkDrawingService.drawDocument(networkNodes, superNodes, networkLinks, documentStyle);
        log.info("Diagram drawn successfully");
        return ResponseEntity.ok(file.toString().getBytes());
    }

    @PostMapping(value = "", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    public ResponseEntity<byte[]> genDiagramSvgByJson(@RequestBody NetworkDiagramCommand networkDiagramCommand) {
        log.debug("Received diagram description: " + networkDiagramCommand + " from user");
        List<NetworkNode> networkNodes = new ArrayList<>();
        List<SuperNode> superNodes = new ArrayList<>();
        List<NetworkLink> networkLinks = new ArrayList<>();
        log.debug("Interpreting diagram description");
        jsonInterpreter.interpretJson(networkDiagramCommand, networkNodes, superNodes, networkLinks);
        log.info("Interpreted diagram description successfully");

        DiagramConfig diagramConfig = Optional.ofNullable(networkDiagramCommand.getDiagramConfig()).orElse(getDefaultDiagramConfig());

        DocumentStyle documentStyle = new DocumentStyle(
                Optional.ofNullable(diagramConfig.getFontName()).orElse("Arial"),
                Optional.of(diagramConfig.getFontSize()).orElse(12),
                Optional.ofNullable(diagramConfig.getFontColor()).orElse("#000000"),
                Optional.ofNullable(diagramConfig.getFontType()).orElse(FontStyle.PLAIN),
                Optional.ofNullable(diagramConfig.getBackgroundColor()).orElse("#ffffff"),
                Optional.ofNullable(diagramConfig.getBorderColor()).orElse("#000000"),
                Optional.ofNullable(diagramConfig.getNodeColor()).orElse("#ffffff"),
                Optional.ofNullable(diagramConfig.getColorStrategy()).orElse(ColorStrategy.RED_GREEN),
                Optional.of(diagramConfig.isShowTitle()).orElse(true),
                Optional.ofNullable(diagramConfig.getTitle()).orElse("true"),
                Optional.of(diagramConfig.isShowLegend()).orElse(true)
        );

        log.debug("Drawing diagram");
        StringBuilder file = networkDrawingService.drawDocument(networkNodes, superNodes, networkLinks, documentStyle);
        log.info("Diagram drawn successfully");
        return ResponseEntity.ok(file.toString().getBytes());
    }

    private DiagramConfig getDefaultDiagramConfig() {
        return new DiagramConfig(
                "Arial",
                12,
                "#000000",
                FontStyle.PLAIN,
                "#ffffff",
                "#000000",
                "#ffffff",
                ColorStrategy.RED_GREEN,
                null,
                false,
                true
        );
    }
}