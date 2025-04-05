package ui;

import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import cadastro.importer.Cadastro;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Geometry;

import java.util.List;

public class MapaVisualizacao extends Pane {
    private double scaleFactor = 1.0;
    private double[] center = new double[]{0, 0};

    public MapaVisualizacao(List<Cadastro> cadastros) {
        setPrefSize(2000, 2000);  // Large canvas for zooming
        setStyle("-fx-background-color: #f0f0f0;");

        if (cadastros != null) {
            renderizarCadastros(cadastros);
        }
    }


    private void renderizarCadastros(List<Cadastro> cadastros) {
        for (Cadastro cadastro : cadastros) {
            try {
                MultiPolygon multiPolygon = cadastro.getShape();
                Shape shape = convertToJavaFXShape(multiPolygon);

                if (shape != null && !shape.getBoundsInLocal().isEmpty()) {
                    configureShape(shape, cadastro);
                    this.getChildren().add(shape);
                }
            } catch (Exception e) {
                System.err.println("Erro ao renderizar cadastro " + cadastro.getId());
                e.printStackTrace();
            }
        }
    }

    private void calculateScaleAndCenter(List<Cadastro> cadastros) {
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Cadastro cadastro : cadastros) {
            Geometry envelope = cadastro.getShape().getEnvelope();
            for (Coordinate coord : envelope.getCoordinates()) {
                minX = Math.min(minX, coord.x);
                maxX = Math.max(maxX, coord.x);
                minY = Math.min(minY, coord.y);
                maxY = Math.max(maxY, coord.y);
            }
        }

        this.center[0] = (minX + maxX) / 2;
        this.center[1] = (minY + maxY) / 2;

        double width = maxX - minX;
        double height = maxY - minY;

        // Ajuste inicial para caber no painel (sem escala automática reduzida)
        this.scaleFactor = Math.min(800 / width, 600 / height) * 1.5; // Zoom inicial maior
    }

    private Shape convertToJavaFXShape(MultiPolygon multiPolygon) {
        if (multiPolygon == null || multiPolygon.isEmpty()) {
            System.out.println("MultiPolygon vazio ou nulo");
            return new Polygon(); // Retorna forma vazia
        }

        Shape result = null;

        try {
            for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
                Geometry geometry = multiPolygon.getGeometryN(i);

                if (geometry instanceof org.locationtech.jts.geom.Polygon jtsPolygon) {
                    Polygon fxPolygon = new Polygon();

                    // Adiciona anel externo
                    addRingToPolygon(fxPolygon, jtsPolygon.getExteriorRing().getCoordinates());

                    // Adiciona anéis internos (buracos)
                    for (int j = 0; j < jtsPolygon.getNumInteriorRing(); j++) {
                        addRingToPolygon(fxPolygon, jtsPolygon.getInteriorRingN(j).getCoordinates());
                    }

                    if (result == null) {
                        result = fxPolygon;
                    } else {
                        result = Shape.union(result, fxPolygon);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro na conversão: " + e.getMessage());
            e.printStackTrace();
        }

        return result != null ? result : new Polygon();
    }

    private void addRingToPolygon(Polygon polygon, Coordinate[] coords) {
        for (Coordinate coord : coords) {
            double x = (coord.x - center[0]) * scaleFactor + 400; // Centraliza em X
            double y = -(coord.y - center[1]) * scaleFactor + 300; // Inverte e centraliza em Y
            polygon.getPoints().addAll(x, y);
        }
    }

    private void configureShape(Shape shape, Cadastro cadastro) {
        // Cores baseadas na área (exemplo)
        double areaRatio = cadastro.getArea() / 1000.0; // Ajuste o divisor conforme necessário
        Color fillColor = Color.hsb(240, 0.7, 1.0, 0.6); // Azul base

        // Se quiser cores diferentes por área
        if (areaRatio > 1.0) {
            fillColor = Color.hsb(120, 0.7, 1.0, 0.6); // Verde para áreas maiores
        }

        shape.setFill(fillColor);
        shape.setStroke(fillColor.darker());
        shape.setStrokeWidth(0.5);

        // Tooltip com informações
        String tooltipText = String.format(
                "ID: %d\nÁrea: %.2f m²\nComprimento: %.2f m\nProprietário: %d",
                cadastro.getId(),
                cadastro.getArea(),
                cadastro.getLength(),
                cadastro.getOwner()
        );

        Tooltip.install(shape, new Tooltip(tooltipText));
    }
}