package cadastro.gui;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

/**
 * Painel responsável por renderizar formas geométricas em um JPanel.
 * Utiliza a biblioteca JTS para manipular dados geométricos e realiza
 * renderização personalizada.
 * 
 * @author [Lei-G]
 * @version 1.0
 */
public class ShapePanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(ShapePanel.class);
    private final Geometry geometry;

    /**
     * Constrói um ShapePanel com a geometria especificada.
     *
     * @param geometry A forma geométrica a ser renderizada
     * @throws IllegalArgumentException se a geometria for nula
     */
    public ShapePanel(Geometry geometry) {
        if (geometry == null) {
            logger.error("Geometria não pode ser nula");
            throw new IllegalArgumentException("Geometria não pode ser nula");
        }
        this.geometry = geometry;
        setBackground(Color.WHITE);
        logger.debug("ShapePanel criado com sucesso para geometria: {}", geometry);
    }

    /**
     * Pinta o componente, renderizando a forma geométrica.
     * Aplica transformações para centralizar e dimensionar a forma
     * adequadamente no painel.
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
            drawGeometry(g2d, geometry);
            logger.trace("Geometria renderizada com sucesso");
        } catch (Exception e) {
            logger.error("Erro ao renderizar geometria: {}", e.getMessage());
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
        g.drawString("Erro ao renderizar forma geométrica", 10, 20);
    }

    /**
     * Calcula a transformação necessária para centralizar e dimensionar a forma.
     * A transformação inclui translação, escala e inversão do eixo Y.
     *
     * @return O objeto AffineTransform representando a transformação
     * @throws IllegalStateException se houver erro ao calcular a transformação
     */
    private AffineTransform calculateTransform() {
        try {
            double minX = geometry.getEnvelopeInternal().getMinX();
            double maxX = geometry.getEnvelopeInternal().getMaxX();
            double minY = geometry.getEnvelopeInternal().getMinY();
            double maxY = geometry.getEnvelopeInternal().getMaxY();
            
            if (Double.isInfinite(minX) || Double.isInfinite(maxX) || 
                Double.isInfinite(minY) || Double.isInfinite(maxY)) {
                throw new IllegalStateException("Coordenadas inválidas na geometria");
            }

            double width = maxX - minX;
            double height = maxY - minY;
            
            if (width <= 0 || height <= 0) {
                throw new IllegalStateException("Dimensões inválidas na geometria");
            }

            double scaleX = (getWidth() * 0.8) / width;
            double scaleY = (getHeight() * 0.8) / height;
            double scale = Math.min(scaleX, scaleY);

            AffineTransform transform = new AffineTransform();
            transform.translate(getWidth() / 2, getHeight() / 2);
            transform.scale(1, -1);
            transform.scale(scale, scale);
            transform.translate(-(minX + width / 2), -(minY + height / 2));
            
            return transform;
        } catch (Exception e) {
            logger.error("Erro ao calcular transformação: {}", e.getMessage());
            throw new IllegalStateException("Erro ao calcular transformação: " + e.getMessage(), e);
        }
    }

    /**
     * Desenha a geometria no objeto Graphics2D.
     * Suporta MultiPolygon e Polygon.
     *
     * @param g2d O objeto Graphics2D usado para desenho
     * @param geom A geometria a ser desenhada
     * @throws IllegalArgumentException se a geometria não for suportada
     */
    private void drawGeometry(Graphics2D g2d, Geometry geom) {
        try {
            if (geom instanceof MultiPolygon multiPolygon) {
                for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
                    Polygon polygon = (Polygon) multiPolygon.getGeometryN(i);
                    drawPolygon(g2d, polygon);
                }
            } else if (geom instanceof Polygon polygon) {
                drawPolygon(g2d, polygon);
            } else {
                throw new IllegalArgumentException("Tipo de geometria não suportado: " + geom.getClass().getSimpleName());
            }
        } catch (Exception e) {
            logger.error("Erro ao desenhar geometria: {}", e.getMessage());
            throw new RuntimeException("Erro ao desenhar geometria", e);
        }
    }

    /**
     * Desenha um polígono no objeto Graphics2D.
     * O polígono é preenchido com uma cor azul semi-transparente
     * e contornado em azul. Os buracos são preenchidos com a cor
     * de fundo e contornados em vermelho.
     *
     * @param g2d O objeto Graphics2D usado para desenho
     * @param polygon O polígono a ser desenhado
     * @throws IllegalStateException se houver erro ao desenhar o polígono
     */
    private void drawPolygon(Graphics2D g2d, Polygon polygon) {
        try {
            if (polygon == null) {
                throw new IllegalArgumentException("Polígono não pode ser nulo");
            }

            Path2D path = toPath2D(polygon.getExteriorRing());
            g2d.setColor(new Color(70, 130, 180, 150));
            g2d.fill(path);
            g2d.setColor(Color.BLUE);
            g2d.draw(path);

            for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
                Path2D hole = toPath2D(polygon.getInteriorRingN(i));
                g2d.setColor(getBackground());
                g2d.fill(hole);
                g2d.setColor(Color.RED);
                g2d.draw(hole);
            }
        } catch (Exception e) {
            logger.error("Erro ao desenhar polígono: {}", e.getMessage());
            throw new IllegalStateException("Erro ao desenhar polígono", e);
        }
    }

    /**
     * Converte um objeto Geometry para um objeto Path2D.
     * Cria um caminho fechado a partir das coordenadas da geometria.
     *
     * @param geometry A geometria a ser convertida
     * @return O objeto Path2D representando a geometria
     * @throws IllegalArgumentException se a geometria for nula ou inválida
     */
    private Path2D toPath2D(Geometry geometry) {
        try {
            if (geometry == null) {
                throw new IllegalArgumentException("Geometria não pode ser nula");
            }

            Path2D path = new Path2D.Double();
            for (int i = 0; i < geometry.getNumPoints(); i++) {
                double x = geometry.getCoordinates()[i].x;
                double y = geometry.getCoordinates()[i].y;
                
                if (Double.isInfinite(x) || Double.isInfinite(y)) {
                    throw new IllegalArgumentException("Coordenadas inválidas na geometria");
                }

                if (i == 0) {
                    path.moveTo(x, y);
                } else {
                    path.lineTo(x, y);
                }
            }
            path.closePath();
            return path;
        } catch (Exception e) {
            logger.error("Erro ao converter geometria para Path2D: {}", e.getMessage());
            throw new RuntimeException("Erro ao converter geometria para Path2D", e);
        }
    }
}