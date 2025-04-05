package ui;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import cadastro.importer.Cadastro;
import java.util.List;

public class MapaController {
    @FXML
    private ScrollPane scrollPane;
    private Pane mapaPane;
    private MapaVisualizacao mapaVisualizacao;
    private final Scale scaleTransform = new Scale(1, 1);
    private double scaleValue = 1.0;

    public void initialize() {
        this.mapaPane = new Pane();
        this.mapaPane.getTransforms().add(scaleTransform);
        this.scrollPane.setContent(mapaPane);

        // Adiciona evento de rolagem para zoom
        mapaPane.setOnScroll(event -> {
            if (event.isControlDown()) {  // Zoom apenas quando Ctrl está pressionado
                double zoomFactor = event.getDeltaY() > 0 ? 1.2 : 0.8;
                applyZoom(zoomFactor, event.getX(), event.getY());
                event.consume();
            }
        });
    }

    public void setCadastros(List<Cadastro> cadastros) {
        this.mapaVisualizacao = new MapaVisualizacao(cadastros);
        this.mapaPane.getChildren().clear();
        this.mapaPane.getChildren().add(mapaVisualizacao);
    }

    private void applyZoom(double zoomFactor, double pivotX, double pivotY) {
        // Calcula nova escala com limites
        scaleValue = Math.max(0.1, Math.min(10, scaleValue * zoomFactor));
        
        // Aplica zoom centralizado na posição do mouse
        scaleTransform.setX(scaleValue);
        scaleTransform.setY(scaleValue);
        
        // Ajusta a rolagem para manter o foco no ponto de zoom
        scrollPane.setHvalue(
            (pivotX/scrollPane.getContent().getBoundsInLocal().getWidth()) * 
            (scaleTransform.getX()/zoomFactor)
        );
        scrollPane.setVvalue(
            (pivotY/scrollPane.getContent().getBoundsInLocal().getHeight()) * 
            (scaleTransform.getY()/zoomFactor)
        );
    }
}