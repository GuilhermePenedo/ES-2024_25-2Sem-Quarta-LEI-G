package cadastro.importer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
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
 * @author Lei-G
 * @date 2024-04-06 21:30
 */
class CadastroTest {
    private static final Logger logger = LoggerFactory.getLogger(CadastroTest.class);
    private static final String CSV_PATH = new File("Dados/Madeira-Moodle-1.1.csv").getAbsolutePath();

    private GeometryFactory geometryFactory;
    private CSVRecord validRecord;
    private CSVRecord invalidRecord;

    @BeforeEach
    void setUp() throws Exception {
        geometryFactory = new GeometryFactory();
        logger.info("Iniciando setup do teste");
        logger.info("Caminho do arquivo CSV: {}", CSV_PATH);
        
        File csvFile = new File(CSV_PATH);
        if (!csvFile.exists()) {
            logger.error("Arquivo CSV não encontrado em: {}", CSV_PATH);
            throw new IllegalStateException("Arquivo CSV não encontrado");
        }
        
        // Lê o arquivo CSV
        try (FileReader reader = new FileReader(csvFile);
             CSVParser parser = CSVFormat.newFormat(';').parse(reader)) {
            
            logger.info("Arquivo CSV aberto com sucesso");
            List<CSVRecord> records = parser.getRecords();
            logger.info("Total de registros lidos: {}", records.size());
            
            if (records.size() >= 3) { // Pelo menos 2 registros + cabeçalho
                logger.info("Criando registros de teste");
                validRecord = records.get(1);
                invalidRecord = records.get(2);
                logger.info("Registros criados com sucesso");
            } else {
                logger.error("Arquivo CSV não contém registros suficientes");
                throw new IllegalStateException("Arquivo CSV não contém registros suficientes");
            }
        } catch (Exception e) {
            logger.error("Erro ao ler arquivo CSV", e);
            throw e;
        }
    }

    @Test
    void constructor() throws Exception {
        logger.info("Executando teste constructor");
        Cadastro cadastro = new Cadastro(validRecord);
        
        assertNotNull(cadastro, "O cadastro deve ser criado");
        assertNotNull(cadastro.getId(), "O ID deve ser definido");
        assertNotNull(cadastro.getOwner(), "O proprietário deve ser definido");
        assertNotNull(cadastro.getArea(), "A área deve ser definida");
        assertNotNull(cadastro.getLength(), "O comprimento deve ser definido");
        assertNotNull(cadastro.getShape(), "A forma deve ser definida");
        logger.info("Teste constructor concluído com sucesso");
    }

    @Test
    void getShape() throws Exception {
        logger.info("Executando teste getShape");
        Cadastro cadastro = new Cadastro(validRecord);
        MultiPolygon shape = cadastro.getShape();
        assertNotNull(shape, "A forma deve ser processada");
        logger.info("Teste getShape concluído com sucesso");
    }

    @Test
    void toStringTest() throws Exception {
        logger.info("Executando teste toString");
        Cadastro cadastro = new Cadastro(validRecord);
        String result = cadastro.toString();
        assertNotNull(result, "A representação em string não deve ser nula");
        assertTrue(result.contains("Cadastro"), "A string deve conter 'Cadastro'");
        logger.info("Teste toString concluído com sucesso");
    }

    @Test
    void constructorInvalid() {
        logger.info("Executando teste constructorInvalid");
        // Cria um registro CSV inválido manualmente
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
        try (StringReader reader = new StringReader(String.join(",", invalidValues));
             CSVParser parser = CSVFormat.DEFAULT.parse(reader)) {
            CSVRecord invalidRecord = parser.getRecords().get(0);
            
            assertThrows(IllegalArgumentException.class, () -> {
                new Cadastro(invalidRecord);
            }, "Deve lançar exceção ao criar cadastro com registro inválido");
        } catch (Exception e) {
            fail("Erro ao criar registro CSV inválido: " + e.getMessage());
        }
        logger.info("Teste constructorInvalid concluído com sucesso");
    }

    @Test
    void handleLocation() throws Exception {
        logger.info("Executando teste handleLocation");
        Cadastro cadastro = new Cadastro(validRecord);
        List<String> locations = cadastro.getLocation();
        assertNotNull(locations, "As localizações devem ser processadas");
        assertFalse(locations.isEmpty(), "A lista de localizações não deve estar vazia");
        logger.info("Teste handleLocation concluído com sucesso");
    }

    @Test
    void handleShape1() throws Exception {
        logger.info("Executando teste handleShape1");
        Cadastro cadastro = new Cadastro(validRecord);
        assertNotNull(cadastro.getShape(), "A forma deve ser processada");
        logger.info("Teste handleShape1 concluído com sucesso");
    }

    @Test
    void handleShape2() {
        logger.info("Executando teste handleShape2");
        // Cria um registro CSV com forma inválida
        String[] invalidShapeValues = {
            "1", // id
            "João", // nome
            "Rua A", // endereço
            "10.0", // comprimento
            "100.0", // área
            "POLYGON((0 0, 0 1, 1 1, 1 0, 0 0))", // shape inválido
            "1", // owner
            "Lisboa", // location1
            "Portugal" // location2
        };
        try (StringReader reader = new StringReader(String.join(",", invalidShapeValues));
             CSVParser parser = CSVFormat.DEFAULT.parse(reader)) {
            CSVRecord invalidShapeRecord = parser.getRecords().get(0);
            
            assertThrows(org.locationtech.jts.io.ParseException.class, () -> {
                new Cadastro(invalidShapeRecord);
            }, "Deve lançar exceção ao processar forma inválida");
        } catch (Exception e) {
            fail("Erro ao criar registro CSV com forma inválida: " + e.getMessage());
        }
        logger.info("Teste handleShape2 concluído com sucesso");
    }

    @Test
    void getCadastros1() throws Exception {
        logger.info("Executando teste getCadastros1");
        List<Cadastro> cadastros = Cadastro.getCadastros(CSV_PATH);
        assertNotNull(cadastros, "A lista de cadastros deve ser criada");
        assertFalse(cadastros.isEmpty(), "A lista não deve estar vazia");
        logger.info("Teste getCadastros1 concluído com sucesso");
    }

    @Test
    void getCadastros2() {
        logger.info("Executando teste getCadastros2");
        assertThrows(Exception.class, () -> {
            Cadastro.getCadastros("arquivo_inexistente.csv");
        }, "Deve lançar exceção ao ler arquivo inválido");
        logger.info("Teste getCadastros2 concluído com sucesso");
    }

    @Test
    void getCadastros3() throws Exception {
        logger.info("Executando teste getCadastros3");
        List<Cadastro> cadastros = Cadastro.getCadastros(CSV_PATH);
        assertNotNull(cadastros, "A lista de cadastros deve ser criada");
        logger.info("Teste getCadastros3 concluído com sucesso");
    }

    @Test
    void sortCadastros1() throws Exception {
        logger.info("Executando teste sortCadastros1");
        List<Cadastro> cadastros = Cadastro.getCadastros(CSV_PATH);
        List<Cadastro> sorted = Cadastro.sortCadastros(cadastros, Cadastro.SORT_BY_ID);
        assertNotNull(sorted, "A lista ordenada não deve ser nula");
        logger.info("Teste sortCadastros1 concluído com sucesso");
    }

    @Test
    void sortCadastros2() throws Exception {
        logger.info("Executando teste sortCadastros2");
        List<Cadastro> cadastros = Cadastro.getCadastros(CSV_PATH);
        List<Cadastro> sorted = Cadastro.sortCadastros(cadastros, Cadastro.SORT_BY_LENGTH);
        assertNotNull(sorted, "A lista ordenada não deve ser nula");
        logger.info("Teste sortCadastros2 concluído com sucesso");
    }

    @Test
    void sortCadastros3() throws Exception {
        logger.info("Executando teste sortCadastros3");
        List<Cadastro> cadastros = Cadastro.getCadastros(CSV_PATH);
        List<Cadastro> sorted = Cadastro.sortCadastros(cadastros, Cadastro.SORT_BY_AREA);
        assertNotNull(sorted, "A lista ordenada não deve ser nula");
        logger.info("Teste sortCadastros3 concluído com sucesso");
    }

    @Test
    void sortCadastros4() throws Exception {
        logger.info("Executando teste sortCadastros4");
        List<Cadastro> cadastros = Cadastro.getCadastros(CSV_PATH);
        List<Cadastro> sorted = Cadastro.sortCadastros(cadastros, Cadastro.SORT_BY_OWNER);
        assertNotNull(sorted, "A lista ordenada não deve ser nula");
        logger.info("Teste sortCadastros4 concluído com sucesso");
    }

    @Test
    void getId() throws Exception {
        logger.info("Executando teste getId");
        Cadastro cadastro = new Cadastro(validRecord);
        assertNotNull(cadastro.getId(), "O ID deve ser definido");
        logger.info("Teste getId concluído com sucesso");
    }

    @Test
    void getLength() throws Exception {
        logger.info("Executando teste getLength");
        Cadastro cadastro = new Cadastro(validRecord);
        assertNotNull(cadastro.getLength(), "O comprimento deve ser definido");
        logger.info("Teste getLength concluído com sucesso");
    }

    @Test
    void getArea() throws Exception {
        logger.info("Executando teste getArea");
        Cadastro cadastro = new Cadastro(validRecord);
        assertNotNull(cadastro.getArea(), "A área deve ser definida");
        logger.info("Teste getArea concluído com sucesso");
    }

    @Test
    void getOwner() throws Exception {
        logger.info("Executando teste getOwner");
        Cadastro cadastro = new Cadastro(validRecord);
        assertNotNull(cadastro.getOwner(), "O proprietário deve ser definido");
        logger.info("Teste getOwner concluído com sucesso");
    }

    @Test
    void getLocation() throws Exception {
        logger.info("Executando teste getLocation");
        Cadastro cadastro = new Cadastro(validRecord);
        List<String> locations = cadastro.getLocation();
        assertNotNull(locations, "As localizações devem ser processadas");
        assertFalse(locations.isEmpty(), "A lista de localizações não deve estar vazia");
        logger.info("Teste getLocation concluído com sucesso");
    }
}