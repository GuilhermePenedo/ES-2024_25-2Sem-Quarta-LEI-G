package cadastro.gui;

import cadastro.graph.PropertyGraph;
import cadastro.importer.Cadastro;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.Set;

/**
 * Painel responsável por renderizar o grafo de propriedades.
 * Exibe as propriedades e suas adjacências de forma visual, onde:
 * - As propriedades são representadas por polígonos preenchidos em azul
 * - As adjacências são representadas por linhas em laranja
 * - O grafo é automaticamente dimensionado e centralizado no painel
 * 
 * @author [Lei-G]
 * @version 1.0
 */
public class GraphPanel extends JPanel {
    /** O grafo de propriedades a ser visualizado */
    private final PropertyGraph propertyGraph;
    
    /** Cor de preenchimento das propriedades (azul semi-transparente) */
    private final Color PROPERTY_FILL = new Color(70, 130, 180, 150);
    
    /** Cor da borda das propriedades (azul) */
    private final Color PROPERTY_BORDER = Color.BLUE;
    
    /** Cor das linhas de adjacência (laranja semi-transparente) */
    private final Color ADJACENCY_LINE = new Color(255, 69, 0, 180);
    
    /** Espessura das linhas de adjacência */
    private final float ADJACENCY_STROKE_WIDTH = 2.0f;

    /**
     * Constrói um GraphPanel com o grafo de propriedades especificado.
     * O painel será dimensionado para 800x600 pixels e terá fundo branco.
     *
     * @param propertyGraph O grafo de propriedades a ser visualizado
     * @throws IllegalArgumentException se o grafo for nulo
     */
    public GraphPanel(PropertyGraph propertyGraph) {
        if (propertyGraph == null) {
            throw new IllegalArgumentException("Grafo de propriedades não pode ser nulo");
        }
        this.propertyGraph = propertyGraph;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));
    }

    /**
     * Pinta o componente, renderizando o grafo de propriedades.
     * Primeiro desenha as adjacências e depois as propriedades,
     * garantindo que as adjacências fiquem atrás das propriedades.
     * 
     * @param g O objeto Graphics usado para pintura
     */
    @Override
    protected void paintComponent(Graphics g) {
        try {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            AffineTransform transform = calculateTransform();
            g2d.transform(transform);

            // Desenha as adjacências primeiro (para ficarem atrás das propriedades)
            drawAdjacencies(g2d);
            
            // Depois desenha as propriedades
            for (Cadastro cadastro : propertyGraph.getProperties()) {
                drawProperty(g2d, cadastro);
            }
        } catch (Exception e) {
            drawError(g);
        }
    }

    /**
     * Desenha uma mensagem de erro no painel.
     * 
     * @param g O objeto Graphics usado para pintura
     */
    private void drawError(Graphics g) {
        g.setColor(Color.RED);
        g.drawString("Erro ao renderizar grafo de propriedades", 10, 20);
    }

    /**
     * Calcula a transformação necessária para centralizar e dimensionar o grafo.
     * A transformação inclui:
     * 1. Translação para o centro do painel
     * 2. Inversão do eixo Y (coordenadas do mundo para coordenadas da tela)
     * 3. Escala para caber no painel
     * 4. Translação para a origem do grafo
     *
     * @return O objeto AffineTransform representando a transformação
     */
    private AffineTransform calculateTransform() {
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        // Calcula os limites do grafo
        for (Cadastro cadastro : propertyGraph.getProperties()) {
            MultiPolygon shape = cadastro.getShape();
            if (shape != null) {
                minX = Math.min(minX, shape.getEnvelopeInternal().getMinX());
                maxX = Math.max(maxX, shape.getEnvelopeInternal().getMaxX());
                minY = Math.min(minY, shape.getEnvelopeInternal().getMinY());
                maxY = Math.max(maxY, shape.getEnvelopeInternal().getMaxY());
            }
        }

        double width = maxX - minX;
        double height = maxY - minY;

        double scaleX = (getWidth() * 0.8) / width;
        double scaleY = (getHeight() * 0.8) / height;
        double scale = Math.min(scaleX, scaleY);

        AffineTransform transform = new AffineTransform();
        transform.translate(getWidth() / 2, getHeight() / 2);
        transform.scale(1, -1);
        transform.scale(scale, scale);
        transform.translate(-(minX + width / 2), -(minY + height / 2));

        return transform;
    }

    /**
     * Desenha uma propriedade no objeto Graphics2D.
     * A propriedade é desenhada como um polígono preenchido em azul
     * com borda azul.
     *
     * @param g2d O objeto Graphics2D usado para desenho
     * @param cadastro A propriedade a ser desenhada
     */
    private void drawProperty(Graphics2D g2d, Cadastro cadastro) {
        MultiPolygon shape = cadastro.getShape();
        if (shape != null) {
            Path2D path = toPath2D(shape);
            g2d.setColor(PROPERTY_FILL);
            g2d.fill(path);
            g2d.setColor(PROPERTY_BORDER);
            g2d.draw(path);
        }
    }

    /**
     * Desenha todas as adjacências do grafo.
     * As adjacências são desenhadas como linhas laranja
     * conectando os centroides das propriedades.
     *
     * @param g2d O objeto Graphics2D usado para desenho
     */
    private void drawAdjacencies(Graphics2D g2d) {
        g2d.setColor(ADJACENCY_LINE);
        g2d.setStroke(new BasicStroke(ADJACENCY_STROKE_WIDTH));

        for (Cadastro cadastro : propertyGraph.getProperties()) {
            Set<Cadastro> adjacents = propertyGraph.getAdjacentProperties(cadastro);
            for (Cadastro adjacent : adjacents) {
                drawAdjacencyLine(g2d, cadastro, adjacent);
            }
        }
    }

    /**
     * Desenha uma linha de adjacência entre duas propriedades.
     * A linha é desenhada entre os centroides das propriedades.
     *
     * @param g2d O objeto Graphics2D usado para desenho
     * @param prop1 Primeira propriedade
     * @param prop2 Segunda propriedade
     */
    private void drawAdjacencyLine(Graphics2D g2d, Cadastro prop1, Cadastro prop2) {
        MultiPolygon shape1 = prop1.getShape();
        MultiPolygon shape2 = prop2.getShape();
        
        if (shape1 != null && shape2 != null) {
            double x1 = shape1.getCentroid().getX();
            double y1 = shape1.getCentroid().getY();
            double x2 = shape2.getCentroid().getX();
            double y2 = shape2.getCentroid().getY();
            
            g2d.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
        }
    }

    /**
     * Converte um objeto Geometry para um objeto Path2D.
     * Cria um caminho fechado a partir das coordenadas da geometria.
     *
     * @param geometry A geometria a ser convertida
     * @return O objeto Path2D representando a geometria
     */
    private Path2D toPath2D(Geometry geometry) {
        Path2D path = new Path2D.Double();
        for (int i = 0; i < geometry.getNumPoints(); i++) {
            double x = geometry.getCoordinates()[i].x;
            double y = geometry.getCoordinates()[i].y;
            
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        path.closePath();
        return path;
    }
} 