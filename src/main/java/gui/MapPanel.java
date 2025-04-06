package gui;

import cadastro.importer.Cadastro;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.List;

public class MapPanel extends JPanel {
    private final List<Cadastro> cadastros;
    private double minX = Double.MAX_VALUE;
    private double maxX = Double.MIN_VALUE;
    private double minY = Double.MAX_VALUE;
    private double maxY = Double.MIN_VALUE;

    private double zoomFactor = 1.0;
    private final double zoomStep = 1.1;

    private double translateX = 0;
    private double translateY = 0;
    private Point lastDragPoint;

    public MapPanel(List<Cadastro> cadastros) {
        this.cadastros = cadastros;
        calculateBounds();
        setPreferredSize(new Dimension(1200, 800));
        setBackground(Color.WHITE);

        // Mouse wheel for zoom
        addMouseWheelListener(e -> {
            int rotation = e.getWheelRotation();
            double zoomChange = (rotation < 0) ? zoomStep : 1 / zoomStep;

            // Get mouse point in "world coordinates"
            double mouseX = (e.getX() - getWidth() / 2.0) / zoomFactor - translateX;
            double mouseY = (e.getY() - getHeight() / 2.0) / zoomFactor - translateY;

            zoomFactor *= zoomChange;

            // Adjust pan so zoom centers on mouse position
            translateX -= mouseX * (zoomChange - 1);
            translateY -= mouseY * (zoomChange - 1);

            repaint();
        });


        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                lastDragPoint = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point current = e.getPoint();
                double dx = (current.x - lastDragPoint.x) / zoomFactor;
                double dy = (current.y - lastDragPoint.y) / zoomFactor;

                translateX += dx;
                translateY += dy;
                lastDragPoint = current;
                repaint();
            }
        });

    }

    private void calculateBounds() {
        for (Cadastro cadastro : cadastros) {
            MultiPolygon shape = cadastro.getShape();
            minX = Math.min(minX, shape.getEnvelopeInternal().getMinX());
            maxX = Math.max(maxX, shape.getEnvelopeInternal().getMaxX());
            minY = Math.min(minY, shape.getEnvelopeInternal().getMinY());
            maxY = Math.max(maxY, shape.getEnvelopeInternal().getMaxY());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AffineTransform transform = new AffineTransform();

        // 1. Move to center
        transform.translate(getWidth() / 2.0, getHeight() / 2.0);

        // 2. Apply zoom
        transform.scale(zoomFactor, zoomFactor);

        // 3. Apply pan (in world coordinates)
        transform.translate(translateX, translateY);

        // 4. Apply base transform (scale to fit map originally)
        transform.concatenate(calculateTransform());

        g2d.setTransform(transform);

        for (Cadastro cadastro : cadastros) {
            drawProperty(g2d, cadastro);
        }

        drawLegend(g2d, transform);
    }


    private AffineTransform calculateTransform() {
        double width = maxX - minX;
        double height = maxY - minY;

        double dataAspectRatio = width / height;
        double panelWidth = getWidth() - 100;
        double panelHeight = getHeight() - 100;
        double panelAspectRatio = panelWidth / panelHeight;

        double scale = (dataAspectRatio > panelAspectRatio) ?
                panelWidth / width : panelHeight / height;

        AffineTransform transform = new AffineTransform();
        transform.translate(50, getHeight() - 50);
        transform.scale(1, -1);
        transform.scale(scale, scale);
        transform.translate(-minX, -minY);

        return transform;
    }

    private void drawProperty(Graphics2D g2d, Cadastro cadastro) {
        Geometry geom = cadastro.getShape();
        Color color = getColorForCadastro(cadastro.getId());

        if (geom instanceof MultiPolygon multiPolygon) {
            for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
                Polygon polygon = (Polygon) multiPolygon.getGeometryN(i);
                drawPolygon(g2d, polygon, color);
            }
        } else if (geom instanceof Polygon polygon) {
            drawPolygon(g2d, polygon, color);
        }
    }

    private void drawPolygon(Graphics2D g2d, Polygon polygon, Color color) {
        Path2D path = toPath2D(polygon.getExteriorRing());
        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 150));
        g2d.fill(path);
        g2d.setColor(color.darker());
        g2d.draw(path);

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

    private Color getColorForCadastro(int id) {
        return new Color(
                (id * 37) % 200 + 55,
                (id * 73) % 200 + 55,
                (id * 101) % 200 + 55
        );
    }

    private void drawLegend(Graphics2D g2d, AffineTransform transform) {
        AffineTransform originalTransform = g2d.getTransform();
        g2d.setTransform(new AffineTransform());

        double[] scaleOptions = {1, 5, 10, 50, 100, 500, 1000};
        double scaleMeters = 100;

        for (double option : scaleOptions) {
            if (option * transform.getScaleX() < getWidth() / 3) {
                scaleMeters = option;
            } else {
                break;
            }
        }

        double scalePixels = scaleMeters / getMetersPerUnit() * transform.getScaleX();

        int legendY = 30;
        g2d.setColor(Color.BLACK);
        g2d.drawLine(50, legendY, 50 + (int) scalePixels, legendY);
        g2d.drawString(String.format("%d m", (int) scaleMeters), 50 + (int) scalePixels / 2 - 15, legendY - 5);
        g2d.drawLine(50, legendY - 5, 50, legendY + 5);
        g2d.drawLine(50 + (int) scalePixels, legendY - 5, 50 + (int) scalePixels, legendY + 5);

        g2d.setTransform(originalTransform);
    }

    private double getMetersPerUnit() {
        return 1.0;
    }
}
