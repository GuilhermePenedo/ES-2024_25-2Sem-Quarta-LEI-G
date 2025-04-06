package cadastro.importer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de teste para Cadastro
 * 
 * Complexidade Ciclomática dos métodos:
 * - Cadastro(): 1
 * - handleShape(): 2
 * - handleLocation(): 1
 * - getCadastros(): 3
 * - sortCadastros(): 4
 * - toString(): 1
 * - getId(): 1
 * - getLength(): 1
 * - getArea(): 1
 * - getShape(): 1
 * - getOwner(): 1
 * - getLocation(): 1
 * 
 * @author pedro
 * @date 2024-04-06 21:30
 */
class CadastroTest {

    private static class TestCSVRecord {
        private final String[] values;

        public TestCSVRecord(String[] values) {
            this.values = values;
        }

        public String get(int index) {
            return values[index];
        }
    }

    private GeometryFactory geometryFactory;
    private TestCSVRecord validRecord;
    private TestCSVRecord invalidRecord;

    @BeforeEach
    void setUp() {
        geometryFactory = new GeometryFactory();
        
        // Cria um registro CSV válido
        String[] validValues = {
            "1", // id
            "João", // nome
            "Rua A", // endereço
            "10.0", // comprimento
            "100.0", // área
            "MULTIPOLYGON(((0 0, 0 1, 1 1, 1 0, 0 0)))", // shape
            "1", // owner
            "Lisboa", // location1
            "Portugal" // location2
        };
        validRecord = new TestCSVRecord(validValues);
        
        // Cria um registro CSV inválido
        String[] invalidValues = {
            "a", // id inválido
            "João", // nome
            "Rua A", // endereço
            "10.0", // comprimento
            "100.0", // área
            "MULTIPOLYGON(((0 0, 0 1, 1 1, 1 0, 0 0)))", // shape
            "1", // owner
            "Lisboa", // location1
            "Portugal" // location2
        };
        invalidRecord = new TestCSVRecord(invalidValues);
    }

    @Test
    void constructor1() {
        // Testa a criação de um cadastro com registro válido
        Cadastro cadastro = new Cadastro(validRecord);
        assertNotNull(cadastro, "O cadastro deve ser criado");
        assertEquals(1, cadastro.getId(), "O ID deve ser 1");
        assertEquals(10.0, cadastro.getLength(), "O comprimento deve ser 10.0");
        assertEquals(100.0, cadastro.getArea(), "A área deve ser 100.0");
        assertNotNull(cadastro.getShape(), "A forma deve ser criada");
        assertEquals(1, cadastro.getOwner(), "O proprietário deve ser 1");
        assertEquals(2, cadastro.getLocation().size(), "Devem haver 2 localizações");
    }

    @Test
    void constructor2() {
        // Testa a criação de um cadastro com registro inválido
        assertThrows(IllegalArgumentException.class, () -> {
            new Cadastro(invalidRecord);
        }, "Deve lançar exceção ao criar cadastro com registro inválido");
    }

    @Test
    void handleShape1() {
        // Testa o processamento de uma forma válida
        String validShape = "MULTIPOLYGON(((0 0, 0 1, 1 1, 1 0, 0 0)))";
        Cadastro cadastro = new Cadastro(validRecord);
        assertNotNull(cadastro.getShape(), "A forma deve ser processada");
    }

    @Test
    void handleShape2() {
        // Testa o processamento de uma forma inválida
        String invalidShape = "POLYGON((0 0, 0 1, 1 1, 1 0, 0 0))";
        assertThrows(IllegalArgumentException.class, () -> {
            new Cadastro(new TestCSVRecord(new String[]{
                "1", "João", "Rua A", "10.0", "100.0", invalidShape, "1", "Lisboa", "Portugal"
            }));
        }, "Deve lançar exceção ao processar forma inválida");
    }

    @Test
    void handleLocation() {
        // Testa o processamento de localizações
        Cadastro cadastro = new Cadastro(validRecord);
        List<String> locations = cadastro.getLocation();
        assertNotNull(locations, "As localizações devem ser processadas");
        assertEquals(2, locations.size(), "Devem haver 2 localizações");
        assertEquals("Lisboa", locations.get(0), "A primeira localização deve ser Lisboa");
        assertEquals("Portugal", locations.get(1), "A segunda localização deve ser Portugal");
    }

    @Test
    void getCadastros1() {
        // Testa a leitura de cadastros de um arquivo válido
        String validPath = "valid.csv";
        List<Cadastro> cadastros = Cadastro.getCadastros(validPath);
        assertNotNull(cadastros, "A lista de cadastros deve ser criada");
        assertFalse(cadastros.isEmpty(), "A lista não deve estar vazia");
    }

    @Test
    void getCadastros2() {
        // Testa a leitura de cadastros de um arquivo inválido
        String invalidPath = "invalid.csv";
        assertThrows(Exception.class, () -> {
            Cadastro.getCadastros(invalidPath);
        }, "Deve lançar exceção ao ler arquivo inválido");
    }

    @Test
    void getCadastros3() {
        // Testa a leitura de cadastros de um arquivo vazio
        String emptyPath = "empty.csv";
        List<Cadastro> cadastros = Cadastro.getCadastros(emptyPath);
        assertNotNull(cadastros, "A lista de cadastros deve ser criada");
        assertTrue(cadastros.isEmpty(), "A lista deve estar vazia");
    }

    @Test
    void sortCadastros1() {
        // Testa a ordenação por ID
        List<Cadastro> cadastros = new ArrayList<>();
        cadastros.add(new Cadastro(new TestCSVRecord(new String[]{
            "2", "Maria", "Rua B", "20.0", "200.0", "MULTIPOLYGON(((0 0, 0 1, 1 1, 1 0, 0 0)))", "2", "Porto", "Portugal"
        })));
        cadastros.add(new Cadastro(new TestCSVRecord(new String[]{
            "1", "João", "Rua A", "10.0", "100.0", "MULTIPOLYGON(((0 0, 0 1, 1 1, 1 0, 0 0)))", "1", "Lisboa", "Portugal"
        })));
        
        List<Cadastro> sorted = Cadastro.sortCadastros(cadastros, Cadastro.SORT_BY_ID);
        assertEquals(1, sorted.get(0).getId(), "O primeiro cadastro deve ter ID 1");
        assertEquals(2, sorted.get(1).getId(), "O segundo cadastro deve ter ID 2");
    }

    @Test
    void sortCadastros2() {
        // Testa a ordenação por comprimento
        List<Cadastro> cadastros = new ArrayList<>();
        cadastros.add(new Cadastro(new TestCSVRecord(new String[]{
            "2", "Maria", "Rua B", "20.0", "200.0", "MULTIPOLYGON(((0 0, 0 1, 1 1, 1 0, 0 0)))", "2", "Porto", "Portugal"
        })));
        cadastros.add(new Cadastro(new TestCSVRecord(new String[]{
            "1", "João", "Rua A", "10.0", "100.0", "MULTIPOLYGON(((0 0, 0 1, 1 1, 1 0, 0 0)))", "1", "Lisboa", "Portugal"
        })));
        
        List<Cadastro> sorted = Cadastro.sortCadastros(cadastros, Cadastro.SORT_BY_LENGTH);
        assertEquals(10.0, sorted.get(0).getLength(), "O primeiro cadastro deve ter comprimento 10.0");
        assertEquals(20.0, sorted.get(1).getLength(), "O segundo cadastro deve ter comprimento 20.0");
    }

    @Test
    void sortCadastros3() {
        // Testa a ordenação por área
        List<Cadastro> cadastros = new ArrayList<>();
        cadastros.add(new Cadastro(new TestCSVRecord(new String[]{
            "2", "Maria", "Rua B", "20.0", "200.0", "MULTIPOLYGON(((0 0, 0 1, 1 1, 1 0, 0 0)))", "2", "Porto", "Portugal"
        })));
        cadastros.add(new Cadastro(new TestCSVRecord(new String[]{
            "1", "João", "Rua A", "10.0", "100.0", "MULTIPOLYGON(((0 0, 0 1, 1 1, 1 0, 0 0)))", "1", "Lisboa", "Portugal"
        })));
        
        List<Cadastro> sorted = Cadastro.sortCadastros(cadastros, Cadastro.SORT_BY_AREA);
        assertEquals(100.0, sorted.get(0).getArea(), "O primeiro cadastro deve ter área 100.0");
        assertEquals(200.0, sorted.get(1).getArea(), "O segundo cadastro deve ter área 200.0");
    }

    @Test
    void sortCadastros4() {
        // Testa a ordenação por proprietário
        List<Cadastro> cadastros = new ArrayList<>();
        cadastros.add(new Cadastro(new TestCSVRecord(new String[]{
            "2", "Maria", "Rua B", "20.0", "200.0", "MULTIPOLYGON(((0 0, 0 1, 1 1, 1 0, 0 0)))", "2", "Porto", "Portugal"
        })));
        cadastros.add(new Cadastro(new TestCSVRecord(new String[]{
            "1", "João", "Rua A", "10.0", "100.0", "MULTIPOLYGON(((0 0, 0 1, 1 1, 1 0, 0 0)))", "1", "Lisboa", "Portugal"
        })));
        
        List<Cadastro> sorted = Cadastro.sortCadastros(cadastros, Cadastro.SORT_BY_OWNER);
        assertEquals(1, sorted.get(0).getOwner(), "O primeiro cadastro deve ter proprietário 1");
        assertEquals(2, sorted.get(1).getOwner(), "O segundo cadastro deve ter proprietário 2");
    }

    @Test
    void toString1() {
        // Testa a representação em string
        Cadastro cadastro = new Cadastro(validRecord);
        String expected = "Cadastro{id=1, length=10.0, area=100.0, shape=MULTIPOLYGON(((0 0, 0 1, 1 1, 1 0, 0 0))), owner=1, location=[Lisboa, Portugal]}";
        assertEquals(expected, cadastro.toString(), "A representação em string deve estar correta");
    }

    @Test
    void getId() {
        Cadastro cadastro = new Cadastro(validRecord);
        assertEquals(1, cadastro.getId(), "O ID deve ser 1");
    }

    @Test
    void getLength() {
        Cadastro cadastro = new Cadastro(validRecord);
        assertEquals(10.0, cadastro.getLength(), "O comprimento deve ser 10.0");
    }

    @Test
    void getArea() {
        Cadastro cadastro = new Cadastro(validRecord);
        assertEquals(100.0, cadastro.getArea(), "A área deve ser 100.0");
    }

    @Test
    void getShape() {
        Cadastro cadastro = new Cadastro(validRecord);
        assertNotNull(cadastro.getShape(), "A forma deve ser criada");
    }

    @Test
    void getOwner() {
        Cadastro cadastro = new Cadastro(validRecord);
        assertEquals(1, cadastro.getOwner(), "O proprietário deve ser 1");
    }

    @Test
    void getLocation() {
        Cadastro cadastro = new Cadastro(validRecord);
        List<String> locations = cadastro.getLocation();
        assertNotNull(locations, "As localizações devem ser processadas");
        assertEquals(2, locations.size(), "Devem haver 2 localizações");
        assertEquals("Lisboa", locations.get(0), "A primeira localização deve ser Lisboa");
        assertEquals("Portugal", locations.get(1), "A segunda localização deve ser Portugal");
    }
}