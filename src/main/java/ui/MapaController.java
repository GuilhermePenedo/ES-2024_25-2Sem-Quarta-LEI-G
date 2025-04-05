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

        // Enable zoom only when mouse is over the content
        mapaPane.setOnScroll(event -> {
            System.out.println("Scroll event: Ctrl="+event.isControlDown()+" Delta="+event.getDeltaY());
            if (event.isControlDown()) {  // Zoom only when Ctrl is held
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
        // Calculate new scale with limits
        scaleValue = Math.max(0.1, Math.min(10, scaleValue * zoomFactor));

        // Apply zoom centered on mouse position
        scaleTransform.setX(scaleValue);
        scaleTransform.setY(scaleValue);

        // Adjust scroll to keep focus on zoom point
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