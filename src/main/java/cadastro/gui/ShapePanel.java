package cadastro.gui;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

/**
 * The ShapePanel class is responsible for rendering geometric shapes on a JPanel.
 * It uses the JTS library to handle geometric data and performs custom rendering.
 */
public class ShapePanel extends JPanel {
    private final Geometry geometry;

    /**
     * Constructs a ShapePanel with the specified geometry.
     *
     * @param geometry The geometric shape to be rendered.
     */
    public ShapePanel(Geometry geometry) {
        this.geometry = geometry;
        setBackground(Color.WHITE);
    }

    /**
     * Paints the component, rendering the geometric shape.
     *
     * @param g The Graphics object used for painting.
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
     * Calculates the transformation needed to center and scale the shape.
     *
     * @return The AffineTransform object representing the transformation.
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
     * Draws the geometry on the Graphics2D object.
     *
     * @param g2d The Graphics2D object used for drawing.
     * @param geom The geometry to be drawn.
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
     * Draws a polygon on the Graphics2D object.
     *
     * @param g2d The Graphics2D object used for drawing.
     * @param polygon The polygon to be drawn.
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
     * Converts a Geometry object to a Path2D object.
     *
     * @param geometry The geometry to be converted.
     * @return The Path2D object representing the geometry.
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