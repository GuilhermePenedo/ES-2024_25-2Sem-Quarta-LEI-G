package cadastro.graph;

import cadastro.importer.Cadastro;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.MultiPolygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger logger = LoggerFactory.getLogger(PropertyGraphTest.class);
    private static final String CSV_PATH = new File("Dados/Madeira-Moodle-1.1.csv").getAbsolutePath();

    private static class TestCadastro {
        private final Cadastro cadastro;
        @SuppressWarnings("unused")
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
        logger.info("Iniciando setup do teste");
        logger.info("Caminho do arquivo CSV: {}", CSV_PATH);
        
        File csvFile = new File(CSV_PATH);
        if (!csvFile.exists()) {
            logger.error("Arquivo CSV não encontrado em: {}", CSV_PATH);
            throw new IllegalStateException("Arquivo CSV não encontrado");
        }
        
        cadastros = new ArrayList<>();
        
        // Lê o arquivo CSV
        try (FileReader reader = new FileReader(csvFile);
             CSVParser parser = CSVFormat.newFormat(';').parse(reader)) {
            
            logger.info("Arquivo CSV aberto com sucesso");
            List<CSVRecord> records = parser.getRecords();
            logger.info("Total de registros lidos: {}", records.size());
            
            if (records.size() >= 3) { // Pelo menos 2 registros + cabeçalho
                logger.info("Criando cadastros de teste");
                TestCadastro testCadastro1 = new TestCadastro(records.get(1));
                TestCadastro testCadastro2 = new TestCadastro(records.get(2));
                
                cadastros.add(testCadastro1.getCadastro());
                cadastros.add(testCadastro2.getCadastro());
                logger.info("Cadastros criados com sucesso");
            } else {
                logger.error("Arquivo CSV não contém registros suficientes");
                throw new IllegalStateException("Arquivo CSV não contém registros suficientes");
            }
        } catch (Exception e) {
            logger.error("Erro ao ler arquivo CSV", e);
            throw e;
        }
        
        graph = new PropertyGraph(cadastros);
        logger.info("Grafo criado com sucesso");
    }

    @Test
    void constructor() {
        logger.info("Executando teste constructor");
        assertNotNull(graph, "O grafo deve ser criado");
        assertEquals(2, graph.getNumberOfProperties(), "O grafo deve ter 2 propriedades");
        logger.info("Teste constructor concluído com sucesso");
    }

    @Test
    void getAdjacentProperties() {
        logger.info("Executando teste getAdjacentProperties");
        Set<Cadastro> adjacent = graph.getAdjacentProperties(cadastros.get(0));
        assertEquals(0, adjacent.size(), "Não deve haver propriedades adjacentes");
        logger.info("Teste getAdjacentProperties concluído com sucesso");
    }

    @Test
    void areAdjacent1() {
        logger.info("Executando teste areAdjacent1");
        assertFalse(graph.areAdjacent(cadastros.get(0), cadastros.get(1)), "Os cadastros não devem ser adjacentes");
        logger.info("Teste areAdjacent1 concluído com sucesso");
    }

    @Test
    void areAdjacent2() {
        logger.info("Executando teste areAdjacent2");
        assertFalse(graph.areAdjacent(cadastros.get(1), cadastros.get(0)), "Os cadastros não devem ser adjacentes");
        logger.info("Teste areAdjacent2 concluído com sucesso");
    }

    @Test
    void getNumberOfProperties() {
        logger.info("Executando teste getNumberOfProperties");
        assertEquals(2, graph.getNumberOfProperties(), "O grafo deve ter 2 propriedades");
        logger.info("Teste getNumberOfProperties concluído com sucesso");
    }

    @Test
    void getNumberOfAdjacencies() {
        logger.info("Executando teste getNumberOfAdjacencies");
        assertEquals(0, graph.getNumberOfAdjacencies(), "O grafo deve ter 0 adjacências");
        logger.info("Teste getNumberOfAdjacencies concluído com sucesso");
    }

    @Test
    void toString1() {
        logger.info("Executando teste toString1");
        String expected = String.format("PropertyGraph{properties=[%s, %s], adjacencies=[]}", 
            cadastros.get(0).toString(), 
            cadastros.get(1).toString());
        assertEquals(expected, graph.toString(), "A representação em string deve estar correta");
        logger.info("Teste toString1 concluído com sucesso");
    }
}