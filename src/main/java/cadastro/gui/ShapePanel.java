package cadastro.gui;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

/**
 * Painel responsável por renderizar formas geométricas em um JPanel.
 * Utiliza a biblioteca JTS para manipular dados geométricos e realiza
 * renderização personalizada.
 * 
 * @author [Nome do Autor]
 * @version 1.0
 */
public class ShapePanel extends JPanel {
    private final Geometry geometry;

    /**
     * Constrói um ShapePanel com a geometria especificada.
     *
     * @param geometry A forma geométrica a ser renderizada
     */
    public ShapePanel(Geometry geometry) {
        this.geometry = geometry;
        setBackground(Color.WHITE);
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
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform transform = calculateTransform();
        g2d.transform(transform);
        drawGeometry(g2d, geometry);
    }

    /**
     * Calcula a transformação necessária para centralizar e dimensionar a forma.
     * A transformação inclui translação, escala e inversão do eixo Y.
     *
     * @return O objeto AffineTransform representando a transformação
     */
    private AffineTransform calculateTransform() {
        double minX = geometry.getEnvelopeInternal().getMinX();
        double maxX = geometry.getEnvelopeInternal().getMaxX();
        double minY = geometry.getEnvelopeInternal().getMinY();
        double maxY = geometry.getEnvelopeInternal().getMaxY();
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
     * Desenha a geometria no objeto Graphics2D.
     * Suporta MultiPolygon e Polygon.
     *
     * @param g2d O objeto Graphics2D usado para desenho
     * @param geom A geometria a ser desenhada
     */
    private void drawGeometry(Graphics2D g2d, Geometry geom) {
        if (geom instanceof MultiPolygon multiPolygon) {
            for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
                Polygon polygon = (Polygon) multiPolygon.getGeometryN(i);
                drawPolygon(g2d, polygon);
            }
        } else if (geom instanceof Polygon polygon) {
            drawPolygon(g2d, polygon);
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
     */
    private void drawPolygon(Graphics2D g2d, Polygon polygon) {
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