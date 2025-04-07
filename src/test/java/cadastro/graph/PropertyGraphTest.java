package cadastro.graph;

import cadastro.importer.Cadastro;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.MultiPolygon;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
 * @author Lei-G
 * @date 2024-04-06 21:30
 */
class PropertyGraphTest {
    private static final String CSV_PATH = new File("Dados/Madeira-Moodle-1.1.csv").getAbsolutePath();

    private static class TestCadastro {
        private final Cadastro cadastro;
        private final MultiPolygon shape;

        public TestCadastro(CSVRecord record) throws Exception {
            this.cadastro = new Cadastro(record);
            this.shape = cadastro.getShape();
        }

        public Cadastro getCadastro() {
            return cadastro;
        }
    }

    private PropertyGraph graph;
    private List<Cadastro> cadastros;

    @BeforeEach
    void setUp() throws Exception {
        PropertyGraphTestLogger.log("=== Iniciando setup dos testes ===");
        PropertyGraphTestLogger.log("Carregando arquivo: " + CSV_PATH);
        
        File csvFile = new File(CSV_PATH);
        if (!csvFile.exists()) {
            throw new IllegalStateException("Arquivo CSV não encontrado em: " + CSV_PATH);
        }
        
        cadastros = new ArrayList<>();
        
        try (FileReader reader = new FileReader(csvFile);
             CSVParser parser = CSVFormat.newFormat(';').parse(reader)) {
            
            List<CSVRecord> records = parser.getRecords();
            PropertyGraphTestLogger.log("Registros encontrados: " + records.size());
            
            if (records.size() >= 3) {
                TestCadastro testCadastro1 = new TestCadastro(records.get(1));
                TestCadastro testCadastro2 = new TestCadastro(records.get(2));
                
                cadastros.add(testCadastro1.getCadastro());
                cadastros.add(testCadastro2.getCadastro());
                PropertyGraphTestLogger.logSuccess("Cadastros de teste criados com sucesso");
            } else {
                throw new IllegalStateException("Arquivo CSV não contém registros suficientes");
            }
        }
        
        graph = new PropertyGraph(cadastros);
        PropertyGraphTestLogger.logSuccess("Grafo de propriedades criado com sucesso");
        PropertyGraphTestLogger.log("=== Setup concluído ===\n");
    }

    @AfterAll
    static void tearDown() {
        PropertyGraphTestLogger.close();
    }

    /**
     * Testa a criação do grafo e verifica se ele contém o número correto de propriedades.
     */
    @Test
    void constructor() {
        PropertyGraphTestLogger.logTestStart("Construtor do PropertyGraph");
        
        assertNotNull(graph, "O grafo deve ser criado");
        assertEquals(2, graph.getNumberOfProperties(), "O grafo deve ter 2 propriedades");
        PropertyGraphTestLogger.logSuccess("Grafo criado corretamente com 2 propriedades");
        
        PropertyGraphTestLogger.logTestEnd("Construtor do PropertyGraph");
    }

    /**
     * Testa a obtenção de propriedades adjacentes para um cadastro específico.
     */
    @Test
    void getAdjacentProperties() {
        PropertyGraphTestLogger.logTestStart("Obtenção de propriedades adjacentes");
        
        Set<Cadastro> adjacent = graph.getAdjacentProperties(cadastros.get(0));
        assertEquals(0, adjacent.size(), "Não deve haver propriedades adjacentes");
        PropertyGraphTestLogger.logSuccess("Verificação de adjacências realizada com sucesso");
        
        PropertyGraphTestLogger.logTestEnd("Obtenção de propriedades adjacentes");
    }

    /**
     * Testa a verificação de adjacência entre duas propriedades (primeira direção).
     */
    @Test
    void areAdjacent1() {
        PropertyGraphTestLogger.logTestStart("Verificação de adjacência (direção 1)");
        
        assertFalse(graph.areAdjacent(cadastros.get(0), cadastros.get(1)), "Os cadastros não devem ser adjacentes");
        PropertyGraphTestLogger.logSuccess("Verificação de não adjacência confirmada");
        
        PropertyGraphTestLogger.logTestEnd("Verificação de adjacência (direção 1)");
    }

    /**
     * Testa a verificação de adjacência entre duas propriedades (direção oposta).
     */
    @Test
    void areAdjacent2() {
        PropertyGraphTestLogger.logTestStart("Verificação de adjacência (direção 2)");
        
        assertFalse(graph.areAdjacent(cadastros.get(1), cadastros.get(0)), "Os cadastros não devem ser adjacentes");
        PropertyGraphTestLogger.logSuccess("Verificação de não adjacência confirmada");
        
        PropertyGraphTestLogger.logTestEnd("Verificação de adjacência (direção 2)");
    }

    /**
     * Testa a contagem total de propriedades no grafo.
     */
    @Test
    void getNumberOfProperties() {
        PropertyGraphTestLogger.logTestStart("Contagem de propriedades");
        
        assertEquals(2, graph.getNumberOfProperties(), "O grafo deve ter 2 propriedades");
        PropertyGraphTestLogger.logSuccess("Número correto de propriedades verificado");
        
        PropertyGraphTestLogger.logTestEnd("Contagem de propriedades");
    }

    /**
     * Testa a contagem total de adjacências no grafo.
     */
    @Test
    void getNumberOfAdjacencies() {
        PropertyGraphTestLogger.logTestStart("Contagem de adjacências");
        
        assertEquals(0, graph.getNumberOfAdjacencies(), "O grafo deve ter 0 adjacências");
        PropertyGraphTestLogger.logSuccess("Número correto de adjacências verificado");
        
        PropertyGraphTestLogger.logTestEnd("Contagem de adjacências");
    }

    /**
     * Testa a representação em string do grafo.
     */
    @Test
    void toString1() {
        PropertyGraphTestLogger.logTestStart("Representação em string do grafo");
        
        String expected = String.format("PropertyGraph{properties=[%s, %s], adjacencies=[]}", 
            cadastros.get(0).toString(), 
            cadastros.get(1).toString());
        assertEquals(expected, graph.toString(), "A representação em string deve estar correta");
        PropertyGraphTestLogger.logSuccess("Representação em string verificada com sucesso");
        
        PropertyGraphTestLogger.logTestEnd("Representação em string do grafo");
    }
}