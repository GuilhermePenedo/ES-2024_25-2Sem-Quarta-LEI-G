package cadastro.graph;

import cadastro.importer.Cadastro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de teste para PropertyGraph
 * 
 * Complexidade Ciclomática dos métodos:
 * - PropertyGraph(): 1
 * - getAdjacentProperties(): 1
 * - areAdjacent(): 2
 * - getNumberOfProperties(): 1
 * - getNumberOfAdjacencies(): 1
 * - toString(): 2
 * 
 * @author pedro
 * @date 2024-04-06 21:30
 */
class PropertyGraphTest {

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

    private PropertyGraph graph;
    private List<Cadastro> cadastros;
    private GeometryFactory geometryFactory;

    @BeforeEach
    void setUp() {
        geometryFactory = new GeometryFactory();
        cadastros = new ArrayList<>();
        graph = new PropertyGraph(cadastros);
    }

    @Test
    void constructor() {
        assertNotNull(graph, "O grafo deve ser criado");
        assertEquals(0, graph.getNumberOfProperties(), "O grafo deve estar vazio");
    }

    @Test
    void getAdjacentProperties() {
        // Cria dois cadastros adjacentes
        Polygon shape1 = geometryFactory.createPolygon();
        Polygon shape2 = geometryFactory.createPolygon();
        MultiPolygon multiShape1 = geometryFactory.createMultiPolygon(new Polygon[]{shape1});
        MultiPolygon multiShape2 = geometryFactory.createMultiPolygon(new Polygon[]{shape2});
        
        TestCadastro cadastro1 = new TestCadastro("1", "João", 100.0, 10.0, multiShape1);
        TestCadastro cadastro2 = new TestCadastro("2", "Maria", 200.0, 20.0, multiShape2);
        
        cadastros.add(cadastro1);
        cadastros.add(cadastro2);
        
        List<Cadastro> adjacent = graph.getAdjacentProperties(cadastro1);
        assertNotNull(adjacent, "A lista de adjacentes não deve ser nula");
        assertEquals(0, adjacent.size(), "Não deve haver adjacentes");
    }

    @Test
    void areAdjacent1() {
        // Cria dois cadastros adjacentes
        Polygon shape1 = geometryFactory.createPolygon();
        Polygon shape2 = geometryFactory.createPolygon();
        MultiPolygon multiShape1 = geometryFactory.createMultiPolygon(new Polygon[]{shape1});
        MultiPolygon multiShape2 = geometryFactory.createMultiPolygon(new Polygon[]{shape2});
        
        TestCadastro cadastro1 = new TestCadastro("1", "João", 100.0, 10.0, multiShape1);
        TestCadastro cadastro2 = new TestCadastro("2", "Maria", 200.0, 20.0, multiShape2);
        
        cadastros.add(cadastro1);
        cadastros.add(cadastro2);
        
        assertFalse(graph.areAdjacent(cadastro1, cadastro2), "Os cadastros não devem ser adjacentes");
    }

    @Test
    void areAdjacent2() {
        // Cria dois cadastros não adjacentes
        Polygon shape1 = geometryFactory.createPolygon();
        Polygon shape2 = geometryFactory.createPolygon();
        MultiPolygon multiShape1 = geometryFactory.createMultiPolygon(new Polygon[]{shape1});
        MultiPolygon multiShape2 = geometryFactory.createMultiPolygon(new Polygon[]{shape2});
        
        TestCadastro cadastro1 = new TestCadastro("1", "João", 100.0, 10.0, multiShape1);
        TestCadastro cadastro2 = new TestCadastro("2", "Maria", 200.0, 20.0, multiShape2);
        
        cadastros.add(cadastro1);
        cadastros.add(cadastro2);
        
        assertFalse(graph.areAdjacent(cadastro1, cadastro2), "Os cadastros não devem ser adjacentes");
    }

    @Test
    void getNumberOfProperties() {
        assertEquals(0, graph.getNumberOfProperties(), "O grafo deve estar vazio");
        
        // Adiciona um cadastro
        Polygon shape = geometryFactory.createPolygon();
        MultiPolygon multiShape = geometryFactory.createMultiPolygon(new Polygon[]{shape});
        TestCadastro cadastro = new TestCadastro("1", "João", 100.0, 10.0, multiShape);
        cadastros.add(cadastro);
        
        assertEquals(1, graph.getNumberOfProperties(), "O grafo deve ter 1 propriedade");
    }

    @Test
    void getNumberOfAdjacencies() {
        assertEquals(0, graph.getNumberOfAdjacencies(), "O grafo deve estar vazio");
        
        // Adiciona dois cadastros
        Polygon shape1 = geometryFactory.createPolygon();
        Polygon shape2 = geometryFactory.createPolygon();
        MultiPolygon multiShape1 = geometryFactory.createMultiPolygon(new Polygon[]{shape1});
        MultiPolygon multiShape2 = geometryFactory.createMultiPolygon(new Polygon[]{shape2});
        
        TestCadastro cadastro1 = new TestCadastro("1", "João", 100.0, 10.0, multiShape1);
        TestCadastro cadastro2 = new TestCadastro("2", "Maria", 200.0, 20.0, multiShape2);
        
        cadastros.add(cadastro1);
        cadastros.add(cadastro2);
        
        assertEquals(0, graph.getNumberOfAdjacencies(), "O grafo deve ter 0 adjacências");
    }

    @Test
    void toString1() {
        String expected = "PropertyGraph{properties=[], adjacencies=[]}";
        assertEquals(expected, graph.toString(), "A representação em string deve estar correta");
    }

    @Test
    void toString2() {
        // Adiciona um cadastro
        Polygon shape = geometryFactory.createPolygon();
        MultiPolygon multiShape = geometryFactory.createMultiPolygon(new Polygon[]{shape});
        TestCadastro cadastro = new TestCadastro("1", "João", 100.0, 10.0, multiShape);
        cadastros.add(cadastro);
        
        String expected = "PropertyGraph{properties=[TestCadastro{id=1, owner=João, area=100.0, length=10.0}], adjacencies=[]}";
        assertEquals(expected, graph.toString(), "A representação em string deve estar correta");
    }
}