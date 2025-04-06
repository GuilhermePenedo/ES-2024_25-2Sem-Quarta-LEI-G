package cadastro.gui;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

public class ShapePanel extends JPanel {
    private final Geometry geometry;

    public ShapePanel(Geometry geometry) {
        this.geometry = geometry;
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Configuração de renderização para melhor qualidade
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Calcula a transformação para centralizar e dimensionar a shape
        AffineTransform transform = calculateTransform();
        g2d.transform(transform);

        // Desenha a shape
        drawGeometry(g2d, geometry);
    }

    private AffineTransform calculateTransform() {
        // Obtém os limites da shape
        double minX = geometry.getEnvelopeInternal().getMinX();
        double maxX = geometry.getEnvelopeInternal().getMaxX();
        double minY = geometry.getEnvelopeInternal().getMinY();
        double maxY = geometry.getEnvelopeInternal().getMaxY();

        // Calcula dimensões
        double width = maxX - minX;
        double height = maxY - minY;

        // Calcula escala para caber no painel
        double scaleX = (getWidth() * 0.8) / width;
        double scaleY = (getHeight() * 0.8) / height;
        double scale = Math.min(scaleX, scaleY);

        // Cria transformação
        AffineTransform transform = new AffineTransform();
        transform.translate(getWidth()/2, getHeight()/2); // Centraliza
        transform.scale(1, -1); // Inverte eixo Y para coordenadas cartesianas
        transform.scale(scale, scale); // Aplica escala
        transform.translate(-(minX + width/2), -(minY + height/2)); // Centraliza a shape

        return transform;
    }

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

    private void drawPolygon(Graphics2D g2d, Polygon polygon) {
        // Desenha o polígono exterior
        Path2D path = toPath2D(polygon.getExteriorRing());
        g2d.setColor(new Color(70, 130, 180, 150)); // Azul transparente
        g2d.fill(path);
        g2d.setColor(Color.BLUE);
        g2d.draw(path);

        // Desenha os buracos (se houver)
        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
            Path2D hole = toPath2D(polygon.getInteriorRingN(i));
            g2d.setColor(getBackground());
            g2d.fill(hole);
            g2d.setColor(Color.RED);
            g2d.draw(hole);
        }
    }

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