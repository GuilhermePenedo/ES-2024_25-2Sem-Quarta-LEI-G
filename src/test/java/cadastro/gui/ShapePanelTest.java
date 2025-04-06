package cadastro.gui;

import cadastro.importer.Cadastro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de teste para ShapePanel
 * 
 * Complexidade Ciclomática dos métodos:
 * - ShapePanel(): 1
 * - paintComponent(): 1
 * - setCadastro(): 1
 * 
 * @author pedro
 * @date 2024-04-06 21:30
 */
class ShapePanelTest {

    private static class TestCadastro extends Cadastro {
        private final String id;
        private final String owner;
        private final double area;
        private final double length;
        private final MultiPolygon shape;

        public TestCadastro(String id, String owner, double area, double length, MultiPolygon shape) {
            this.id = id;
            this.owner = owner;
            this.area = area;
            this.length = length;
            this.shape = shape;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getOwner() {
            return owner;
        }

        @Override
        public double getArea() {
            return area;
        }

        @Override
        public double getLength() {
            return length;
        }

        @Override
        public MultiPolygon getShape() {
            return shape;
        }
    }

    private ShapePanel panel;
    private GeometryFactory geometryFactory;

    @BeforeEach
    void setUp() {
        geometryFactory = new GeometryFactory();
        panel = new ShapePanel();
    }

    @Test
    void constructor() {
        assertNotNull(panel, "O painel deve ser criado");
        assertTrue(panel.isVisible(), "O painel deve estar visível");
    }

    @Test
    void paintComponent() {
        // Cria um cadastro de teste
        Polygon shape = geometryFactory.createPolygon();
        MultiPolygon multiShape = geometryFactory.createMultiPolygon(new Polygon[]{shape});
        TestCadastro cadastro = new TestCadastro("1", "João", 100.0, 10.0, multiShape);
        
        // Configura o painel
        panel.setCadastro(cadastro);
        
        // Cria uma imagem para testar o desenho
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // Chama o método de pintura
        panel.paintComponent(g2d);
        
        // Verifica se a imagem foi alterada
        assertNotEquals(0, image.getRGB(50, 50), "A imagem deve ter sido pintada");
    }

    @Test
    void setCadastro() {
        // Cria um cadastro de teste
        Polygon shape = geometryFactory.createPolygon();
        MultiPolygon multiShape = geometryFactory.createMultiPolygon(new Polygon[]{shape});
        TestCadastro cadastro = new TestCadastro("1", "João", 100.0, 10.0, multiShape);
        
        // Configura o painel
        panel.setCadastro(cadastro);
        
        // Verifica se o cadastro foi configurado
        assertNotNull(panel.getCadastro(), "O cadastro deve ser configurado");
    }
}