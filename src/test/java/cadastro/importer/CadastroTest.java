package cadastro.importer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.MultiPolygon;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de teste para Cadastro
 * 
 * Complexidade Ciclomática dos métodos:
 * - Cadastro(): 5
 * - handleId(): 2
 * - handleLength(): 2
 * - handleArea(): 2
 * - handleShape(): 3
 * - handleOwner(): 2
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
    private static final String CSV_PATH = new File("Dados/Madeira-Moodle-1.1.csv").getAbsolutePath();

    private CSVRecord validRecord;

    @BeforeEach
    void setUp() throws Exception {
        CadastroTestLogger.log("Iniciando setup do teste");
        CadastroTestLogger.log("Caminho do arquivo CSV: " + CSV_PATH);

        File csvFile = new File(CSV_PATH);
        if (!csvFile.exists()) {
            CadastroTestLogger.logError("Arquivo CSV não encontrado em: " + CSV_PATH);
            throw new IllegalStateException("Arquivo CSV não encontrado");
        }

        // Lê o arquivo CSV
        try (FileReader reader = new FileReader(csvFile);
                CSVParser parser = CSVFormat.newFormat(';').parse(reader)) {

            CadastroTestLogger.logSuccess("Arquivo CSV aberto com sucesso");
            List<CSVRecord> records = parser.getRecords();
            CadastroTestLogger.log("Total de registros lidos: " + records.size());

            if (records.size() >= 3) { // Pelo menos 2 registros + cabeçalho
                CadastroTestLogger.log("Criando registros de teste");
                validRecord = records.get(1);
                CadastroTestLogger.logSuccess("Registros criados com sucesso");
            } else {
                CadastroTestLogger.logError("Arquivo CSV não contém registros suficientes");
                throw new IllegalStateException("Arquivo CSV não contém registros suficientes");
            }
        } catch (Exception e) {
            CadastroTestLogger.logError("Erro ao ler arquivo CSV: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void tearDown() {
        CadastroTestLogger.close();
    }

    @Test
    void constructor() throws Exception {
        CadastroTestLogger.logTestStart("constructor");
        Cadastro cadastro = new Cadastro(validRecord);

        assertNotNull(cadastro, "O cadastro deve ser criado");
        assertNotNull(cadastro.getId(), "O ID deve ser definido");
        assertNotNull(cadastro.getOwner(), "O proprietário deve ser definido");
        assertNotNull(cadastro.getArea(), "A área deve ser definida");
        assertNotNull(cadastro.getLength(), "O comprimento deve ser definido");
        assertNotNull(cadastro.getShape(), "A forma deve ser definida");
        CadastroTestLogger.logSuccess("Teste constructor concluído com sucesso");
        CadastroTestLogger.logTestEnd("constructor");
    }

    @Test
    void constructorInvalid1() {
        CadastroTestLogger.logTestStart("constructorInvalid1");
        // Cria um registro CSV inválido manualmente
        String[] invalidValues = {
                "a", // id invalido
                "7343148", // random par_id
                "2", // random par_number
                "10.0", // comprimento
                "100.0", // área
                "MULTIPOLYGON(((0 0, 0 1, 1 1, 1 0, 0 0)))", // shape
                "1", // owner
                "Lisboa", // location1
                "Portugal" // location2
        };
        try (StringReader reader = new StringReader(String.join(";", invalidValues));
                CSVParser parser = CSVFormat.newFormat(';').parse(reader)) {
            CSVRecord invalidRecord = parser.getRecords().get(0);

            assertThrows(IllegalArgumentException.class, () -> {
                new Cadastro(invalidRecord);
            }, "Deve lançar exceção ao criar cadastro com registro inválido");
        } catch (Exception e) {
            fail("Erro ao criar registro CSV inválido: " + e.getMessage());
        }
        CadastroTestLogger.logSuccess("Teste constructorInvalid1 concluído com sucesso");
        CadastroTestLogger.logTestEnd("constructorInvalid1");
    }

    @Test
    void constructorInvalid2() {
        CadastroTestLogger.logTestStart("constructorInvalid2");
        // Cria um registro CSV inválido manualmente
        String[] invalidValues = {
                "1", // id
                "7343148", // random par_id
                "2", // random par_number
                "a", // comprimento invalido
                "100.0", // área
                "MULTIPOLYGON(((0 0, 0 1, 1 1, 1 0, 0 0)))", // shape
                "1", // owner
                "Lisboa", // location1
                "Portugal" // location2
        };
        try (StringReader reader = new StringReader(String.join(";", invalidValues));
                CSVParser parser = CSVFormat.newFormat(';').parse(reader)) {
            CSVRecord invalidRecord = parser.getRecords().get(0);

            assertThrows(IllegalArgumentException.class, () -> {
                new Cadastro(invalidRecord);
            }, "Deve lançar exceção ao criar cadastro com registro inválido");
        } catch (Exception e) {
            fail("Erro ao criar registro CSV inválido: " + e.getMessage());
        }
        CadastroTestLogger.logSuccess("Teste constructorInvalid2 concluído com sucesso");
        CadastroTestLogger.logTestEnd("constructorInvalid2");
    }

    @Test
    void constructorInvalid3() {
        CadastroTestLogger.logTestStart("constructorInvalid3");
        // Cria um registro CSV inválido manualmente
        String[] invalidValues = {
                "1", // id
                "7343148", // random par_id
                "2", // random par_number
                "10.0", // comprimento
                "a", // área invalida
                "MULTIPOLYGON(((0 0, 0 1, 1 1, 1 0, 0 0)))", // shape
                "1", // owner
                "Lisboa", // location1
                "Portugal" // location2
        };
        try (StringReader reader = new StringReader(String.join(";", invalidValues));
                CSVParser parser = CSVFormat.newFormat(';').parse(reader)) {
            CSVRecord invalidRecord = parser.getRecords().get(0);

            assertThrows(IllegalArgumentException.class, () -> {
                new Cadastro(invalidRecord);
            }, "Deve lançar exceção ao criar cadastro com registro inválido");
        } catch (Exception e) {
            fail("Erro ao criar registro CSV inválido: " + e.getMessage());
        }
        CadastroTestLogger.logSuccess("Teste constructorInvalid3 concluído com sucesso");
        CadastroTestLogger.logTestEnd("constructorInvalid3");
    }

    @Test
    void constructorInvalid4() {
        CadastroTestLogger.logTestStart("constructorInvalid4");
        // Cria um registro CSV inválido manualmente
        String[] invalidValues = {
                "1", // id
                "7343148", // random par_id
                "2", // random par_number
                "10.0", // comprimento
                "100.0", // área
                "MULTIPOLYGON(((0 0, 0 1, 1 1, 1 0, 0 0)))", // shape
                "a", // owner invalido
                "Lisboa", // location1
                "Portugal" // location2
        };
        try (StringReader reader = new StringReader(String.join(";", invalidValues));
                CSVParser parser = CSVFormat.newFormat(';').parse(reader)) {
            CSVRecord invalidRecord = parser.getRecords().get(0);

            assertThrows(IllegalArgumentException.class, () -> {
                new Cadastro(invalidRecord);
            }, "Deve lançar exceção ao criar cadastro com registro inválido");
        } catch (Exception e) {
            fail("Erro ao criar registro CSV inválido: " + e.getMessage());
        }
        CadastroTestLogger.logSuccess("Teste constructorInvalid4 concluído com sucesso");
        CadastroTestLogger.logTestEnd("constructorInvalid4");
    }

    @Test
    void handleId1() {
        CadastroTestLogger.logTestStart("handleId1");
        // Cria um registro CSV inválido manualmente
        String[] invalidValues = {
                "0", // id a zero
                "7343148", // random par_id
                "2", // random par_number
                "10.0", // comprimento
                "100.0", // área
                "MULTIPOLYGON(((0 0, 0 1, 1 1, 1 0, 0 0)))", // shape
                "1", // owner
                "Lisboa", // location1
                "Portugal" // location2
        };
        try (StringReader reader = new StringReader(String.join(";", invalidValues));
                CSVParser parser = CSVFormat.newFormat(';').parse(reader)) {
            CSVRecord invalidRecord = parser.getRecords().get(0);

            assertThrows(IllegalArgumentException.class, () -> {
                new Cadastro(invalidRecord);
            }, "Deve lançar exceção ao criar cadastro com registro inválido");
        } catch (Exception e) {
            fail("Erro ao criar registro CSV inválido: " + e.getMessage());
        }
        CadastroTestLogger.logSuccess("Teste handleId1 concluído com sucesso");
        CadastroTestLogger.logTestEnd("handleId1");
    }

    @Test
    void handleId2() {
        CadastroTestLogger.logTestStart("handleId2");
        // Cria um registro CSV inválido manualmente
        String[] invalidValues = {
                " ", // id vazio
                "7343148", // random par_id
                "2", // random par_number
                "10.0", // comprimento
                "100.0", // área
                "MULTIPOLYGON(((0 0, 0 1, 1 1, 1 0, 0 0)))", // shape
                "1", // owner
                "Lisboa", // location1
                "Portugal" // location2
        };
        try (StringReader reader = new StringReader(String.join(";", invalidValues));
                CSVParser parser = CSVFormat.newFormat(';').parse(reader)) {
            CSVRecord invalidRecord = parser.getRecords().get(0);

            assertThrows(IllegalArgumentException.class, () -> {
                new Cadastro(invalidRecord);
            }, "Deve lançar exceção ao criar cadastro com registro inválido");
        } catch (Exception e) {
            fail("Erro ao criar registro CSV inválido: " + e.getMessage());
        }
        CadastroTestLogger.logSuccess("Teste handleId2 concluído com sucesso");
        CadastroTestLogger.logTestEnd("handleId2");
    }

    @Test
    void handleLength1() {
        CadastroTestLogger.logTestStart("handleLength1");
        // Cria um registro CSV inválido manualmente
        String[] invalidValues = {
                "1", // id
                "7343148", // random par_id
                "2", // random par_number
                "0", // comprimento a zero
                "100.0", // área
                "MULTIPOLYGON(((0 0, 0 1, 1 1, 1 0, 0 0)))", // shape
                "1", // owner
                "Lisboa", // location1
                "Portugal" // location2
        };
        try (StringReader reader = new StringReader(String.join(";", invalidValues));
                CSVParser parser = CSVFormat.newFormat(';').parse(reader)) {
            CSVRecord invalidRecord = parser.getRecords().get(0);

            assertThrows(IllegalArgumentException.class, () -> {
                new Cadastro(invalidRecord);
            }, "Deve lançar exceção ao criar cadastro com registro inválido");
        } catch (Exception e) {
            fail("Erro ao criar registro CSV inválido: " + e.getMessage());
        }
        CadastroTestLogger.logSuccess("Teste handleLength1 concluído com sucesso");
        CadastroTestLogger.logTestEnd("handleLength1");
    }

    @Test
    void handleLength2() {
        CadastroTestLogger.logTestStart("handleLength2");
        // Cria um registro CSV inválido manualmente
        String[] invalidValues = {
                "1", // id
                "7343148", // random par_id
                "2", // random par_number
                " ", // comprimento vazio
                "100.0", // área
                "MULTIPOLYGON(((0 0, 0 1, 1 1, 1 0, 0 0)))", // shape
                "1", // owner
                "Lisboa", // location1
                "Portugal" // location2
        };
        try (StringReader reader = new StringReader(String.join(";", invalidValues));
                CSVParser parser = CSVFormat.newFormat(';').parse(reader)) {
            CSVRecord invalidRecord = parser.getRecords().get(0);

            assertThrows(IllegalArgumentException.class, () -> {
                new Cadastro(invalidRecord);
            }, "Deve lançar exceção ao criar cadastro com registro inválido");
        } catch (Exception e) {
            fail("Erro ao criar registro CSV inválido: " + e.getMessage());
        }
        CadastroTestLogger.logSuccess("Teste handleLength2 concluído com sucesso");
        CadastroTestLogger.logTestEnd("handleLength2");
    }

    @Test
    void handleArea1() {
        CadastroTestLogger.logTestStart("handleArea1");
        // Cria um registro CSV inválido manualmente
        String[] invalidValues = {
                "1", // id
                "7343148", // random par_id
                "2", // random par_number
                "10.0", // comprimento
                "0", // área a zero
                "MULTIPOLYGON(((0 0, 0 1, 1 1, 1 0, 0 0)))", // shape
                "1", // owner
                "Lisboa", // location1
                "Portugal" // location2
        };
        try (StringReader reader = new StringReader(String.join(";", invalidValues));
                CSVParser parser = CSVFormat.newFormat(';').parse(reader)) {
            CSVRecord invalidRecord = parser.getRecords().get(0);

            assertThrows(IllegalArgumentException.class, () -> {
                new Cadastro(invalidRecord);
            }, "Deve lançar exceção ao criar cadastro com registro inválido");
        } catch (Exception e) {
            fail("Erro ao criar registro CSV inválido: " + e.getMessage());
        }
        CadastroTestLogger.logSuccess("Teste handleArea1 concluído com sucesso");
        CadastroTestLogger.logTestEnd("handleArea1");
    }

    @Test
    void handleArea2() {
        CadastroTestLogger.logTestStart("handleArea2");
        // Cria um registro CSV inválido manualmente
        String[] invalidValues = {
                "1", // id
                "7343148", // random par_id
                "2", // random par_number
                "10.0", // comprimento
                " ", // área vazia
                "MULTIPOLYGON(((0 0, 0 1, 1 1, 1 0, 0 0)))", // shape
                "1", // owner
                "Lisboa", // location1
                "Portugal" // location2
        };
        try (StringReader reader = new StringReader(String.join(";", invalidValues));
                CSVParser parser = CSVFormat.newFormat(';').parse(reader)) {
            CSVRecord invalidRecord = parser.getRecords().get(0);

            assertThrows(IllegalArgumentException.class, () -> {
                new Cadastro(invalidRecord);
            }, "Deve lançar exceção ao criar cadastro com registro inválido");
        } catch (Exception e) {
            fail("Erro ao criar registro CSV inválido: " + e.getMessage());
        }
        CadastroTestLogger.logSuccess("Teste handleArea2 concluído com sucesso");
        CadastroTestLogger.logTestEnd("handleArea2");
    }

    @Test
    void handleOwner1() {
        CadastroTestLogger.logTestStart("handleOwner1");
        // Cria um registro CSV inválido manualmente
        String[] invalidValues = {
                "1", // id
                "7343148", // random par_id
                "2", // random par_number
                "10.0", // comprimento
                "100.0", // área
                "MULTIPOLYGON(((0 0, 0 1, 1 1, 1 0, 0 0)))", // shape
                "0", // owner a zero
                "Lisboa", // location1
                "Portugal" // location2
        };
        try (StringReader reader = new StringReader(String.join(";", invalidValues));
                CSVParser parser = CSVFormat.newFormat(';').parse(reader)) {
            CSVRecord invalidRecord = parser.getRecords().get(0);

            assertThrows(IllegalArgumentException.class, () -> {
                new Cadastro(invalidRecord);
            }, "Deve lançar exceção ao criar cadastro com registro inválido");
        } catch (Exception e) {
            fail("Erro ao criar registro CSV inválido: " + e.getMessage());
        }
        CadastroTestLogger.logSuccess("Teste handleOwner1 concluído com sucesso");
        CadastroTestLogger.logTestEnd("handleOwner1");
    }

    @Test
    void handleOwner2() {
        CadastroTestLogger.logTestStart("handleOwner2");
        // Cria um registro CSV inválido manualmente
        String[] invalidValues = {
                "1", // id
                "7343148", // random par_id
                "2", // random par_number
                "10.0", // comprimento
                "100.0", // área
                "MULTIPOLYGON(((0 0, 0 1, 1 1, 1 0, 0 0)))", // shape
                " ", // owner invalido
                "Lisboa", // location1
                "Portugal" // location2
        };
        try (StringReader reader = new StringReader(String.join(";", invalidValues));
                CSVParser parser = CSVFormat.newFormat(';').parse(reader)) {
            CSVRecord invalidRecord = parser.getRecords().get(0);

            assertThrows(IllegalArgumentException.class, () -> {
                new Cadastro(invalidRecord);
            }, "Deve lançar exceção ao criar cadastro com registro inválido");
        } catch (Exception e) {
            fail("Erro ao criar registro CSV inválido: " + e.getMessage());
        }
        CadastroTestLogger.logSuccess("Teste handleOwner2 concluído com sucesso");
        CadastroTestLogger.logTestEnd("handleOwner2");
    }

    @Test
    void handleShape1() throws Exception {
        CadastroTestLogger.logTestStart("handleShape1");
        Cadastro cadastro = new Cadastro(validRecord);
        assertNotNull(cadastro.getShape(), "A forma deve ser processada");
        CadastroTestLogger.logSuccess("Teste handleShape1 concluído com sucesso");
        CadastroTestLogger.logTestEnd("handleShape1");
    }

    @Test
    void handleShape2() {
        CadastroTestLogger.logTestStart("handleShape2");
        // Cria um registro CSV com forma inválida
        String[] invalidShapeValues = {
                "1", // id
                "7343148", // random par_id
                "2", // random par_number
                "10.0", // comprimento
                "100.0", // área
                "POLYGON((0 0, 0 1, 1 1, 1 0, 0 0))", // shape inválido
                "1", // owner
                "Lisboa", // location1
                "Portugal" // location2
        };
        try (StringReader reader = new StringReader(String.join(";", invalidShapeValues));
                CSVParser parser = CSVFormat.newFormat(';').parse(reader)) {
            CSVRecord invalidShapeRecord = parser.getRecords().get(0);

            assertThrows(IllegalArgumentException.class, () -> {
                new Cadastro(invalidShapeRecord);
            }, "Deve lançar exceção ao processar forma inválida");
        } catch (Exception e) {
            fail("Erro ao criar registro CSV com forma inválida: " + e.getMessage());
        }
        CadastroTestLogger.logSuccess("Teste handleShape2 concluído com sucesso");
        CadastroTestLogger.logTestEnd("handleShape2");
    }

    @Test
    void handleShape3() {
        CadastroTestLogger.logTestStart("handleShape3");
        // Cria um registro CSV com forma inválida
        String[] invalidShapeValues = {
                "1", // id
                "7343148", // random par_id
                "2", // random par_number
                "10.0", // comprimento
                "100.0", // área
                "INVALID_SHAPE", // shape inválido (não é um MULTIPOLYGON válido)
                "1", // owner
                "Lisboa", // location1
                "Portugal" // location2
        };
        try (StringReader reader = new StringReader(String.join(";", invalidShapeValues));
                CSVParser parser = CSVFormat.newFormat(';').parse(reader)) {
            CSVRecord invalidShapeRecord = parser.getRecords().get(0);

            assertThrows(org.locationtech.jts.io.ParseException.class, () -> {
                new Cadastro(invalidShapeRecord);
            }, "Deve lançar exceção ao processar forma inválida");
        } catch (Exception e) {
            fail("Erro ao criar registro CSV com forma inválida: " + e.getMessage());
        }
        CadastroTestLogger.logSuccess("Teste handleShape3 concluído com sucesso");
        CadastroTestLogger.logTestEnd("handleShape3");
    }

    @Test
    void getShape() throws Exception {
        CadastroTestLogger.logTestStart("getShape");
        Cadastro cadastro = new Cadastro(validRecord);
        MultiPolygon shape = cadastro.getShape();
        assertNotNull(shape, "A forma deve ser processada");
        CadastroTestLogger.logSuccess("Teste getShape concluído com sucesso");
        CadastroTestLogger.logTestEnd("getShape");
    }

    @Test
    void toStringTest() throws Exception {
        CadastroTestLogger.logTestStart("toString");
        Cadastro cadastro = new Cadastro(validRecord);
        String result = cadastro.toString();
        assertNotNull(result, "A representação em string não deve ser nula");
        assertTrue(result.contains("Cadastro"), "A string deve conter 'Cadastro'");
        CadastroTestLogger.logSuccess("Teste toString concluído com sucesso");
        CadastroTestLogger.logTestEnd("toString");
    }

    @Test
    void handleLocation() throws Exception {
        CadastroTestLogger.logTestStart("handleLocation");
        Cadastro cadastro = new Cadastro(validRecord);
        List<String> locations = cadastro.getLocation();
        assertNotNull(locations, "As localizações devem ser processadas");
        assertFalse(locations.isEmpty(), "A lista de localizações não deve estar vazia");
        CadastroTestLogger.logSuccess("Teste handleLocation concluído com sucesso");
        CadastroTestLogger.logTestEnd("handleLocation");
    }

    @Test
    void getCadastros1() throws Exception {
        CadastroTestLogger.logTestStart("getCadastros1");
        List<Cadastro> cadastros = Cadastro.getCadastros(CSV_PATH);
        assertNotNull(cadastros, "A lista de cadastros deve ser criada");
        assertFalse(cadastros.isEmpty(), "A lista não deve estar vazia");
        CadastroTestLogger.logSuccess("Teste getCadastros1 concluído com sucesso");
        CadastroTestLogger.logTestEnd("getCadastros1");
    }

    @Test
    void getCadastros2() {
        CadastroTestLogger.logTestStart("getCadastros2");
        assertThrows(Exception.class, () -> {
            Cadastro.getCadastros("arquivo_inexistente.csv");
        }, "Deve lançar exceção ao ler arquivo inválido");
        CadastroTestLogger.logSuccess("Teste getCadastros2 concluído com sucesso");
        CadastroTestLogger.logTestEnd("getCadastros2");
    }

    @Test
    void getCadastros3() throws Exception {
        CadastroTestLogger.logTestStart("getCadastros3");
        List<Cadastro> cadastros = Cadastro.getCadastros(CSV_PATH);
        assertNotNull(cadastros, "A lista de cadastros deve ser criada");
        CadastroTestLogger.logSuccess("Teste getCadastros3 concluído com sucesso");
        CadastroTestLogger.logTestEnd("getCadastros3");
    }

    @Test
    void sortCadastros1() throws Exception {
        CadastroTestLogger.logTestStart("sortCadastros1");
        List<Cadastro> cadastros = Cadastro.getCadastros(CSV_PATH);
        List<Cadastro> sorted = Cadastro.sortCadastros(cadastros, CadastroConstants.SORT_BY_ID);
        assertNotNull(sorted, "A lista ordenada não deve ser nula");
        CadastroTestLogger.logSuccess("Teste sortCadastros1 concluído com sucesso");
        CadastroTestLogger.logTestEnd("sortCadastros1");
    }

    @Test
    void sortCadastros2() throws Exception {
        CadastroTestLogger.logTestStart("sortCadastros2");
        List<Cadastro> cadastros = Cadastro.getCadastros(CSV_PATH);
        List<Cadastro> sorted = Cadastro.sortCadastros(cadastros, CadastroConstants.SORT_BY_LENGTH);
        assertNotNull(sorted, "A lista ordenada não deve ser nula");
        CadastroTestLogger.logSuccess("Teste sortCadastros2 concluído com sucesso");
        CadastroTestLogger.logTestEnd("sortCadastros2");
    }

    @Test
    void sortCadastros3() throws Exception {
        CadastroTestLogger.logTestStart("sortCadastros3");
        List<Cadastro> cadastros = Cadastro.getCadastros(CSV_PATH);
        List<Cadastro> sorted = Cadastro.sortCadastros(cadastros, CadastroConstants.SORT_BY_AREA);
        assertNotNull(sorted, "A lista ordenada não deve ser nula");
        CadastroTestLogger.logSuccess("Teste sortCadastros3 concluído com sucesso");
        CadastroTestLogger.logTestEnd("sortCadastros3");
    }

    @Test
    void sortCadastros4() throws Exception {
        CadastroTestLogger.logTestStart("sortCadastros4");
        List<Cadastro> cadastros = Cadastro.getCadastros(CSV_PATH);
        List<Cadastro> sorted = Cadastro.sortCadastros(cadastros, CadastroConstants.SORT_BY_OWNER);
        assertNotNull(sorted, "A lista ordenada não deve ser nula");
        CadastroTestLogger.logSuccess("Teste sortCadastros4 concluído com sucesso");
        CadastroTestLogger.logTestEnd("sortCadastros4");
    }

    @Test
    void getId() throws Exception {
        CadastroTestLogger.logTestStart("getId");
        Cadastro cadastro = new Cadastro(validRecord);
        assertNotNull(cadastro.getId(), "O ID deve ser definido");
        CadastroTestLogger.logSuccess("Teste getId concluído com sucesso");
        CadastroTestLogger.logTestEnd("getId");
    }

    @Test
    void getLength() throws Exception {
        CadastroTestLogger.logTestStart("getLength");
        Cadastro cadastro = new Cadastro(validRecord);
        assertNotNull(cadastro.getLength(), "O comprimento deve ser definido");
        CadastroTestLogger.logSuccess("Teste getLength concluído com sucesso");
        CadastroTestLogger.logTestEnd("getLength");
    }

    @Test
    void getArea() throws Exception {
        CadastroTestLogger.logTestStart("getArea");
        Cadastro cadastro = new Cadastro(validRecord);
        assertNotNull(cadastro.getArea(), "A área deve ser definida");
        CadastroTestLogger.logSuccess("Teste getArea concluído com sucesso");
        CadastroTestLogger.logTestEnd("getArea");
    }

    @Test
    void getOwner() throws Exception {
        CadastroTestLogger.logTestStart("getOwner");
        Cadastro cadastro = new Cadastro(validRecord);
        assertNotNull(cadastro.getOwner(), "O proprietário deve ser definido");
        CadastroTestLogger.logSuccess("Teste getOwner concluído com sucesso");
        CadastroTestLogger.logTestEnd("getOwner");
    }

    @Test
    void getLocation() throws Exception {
        CadastroTestLogger.logTestStart("getLocation");
        Cadastro cadastro = new Cadastro(validRecord);
        List<String> locations = cadastro.getLocation();
        assertNotNull(locations, "As localizações devem ser processadas");
        assertFalse(locations.isEmpty(), "A lista de localizações não deve estar vazia");
        CadastroTestLogger.logSuccess("Teste getLocation concluído com sucesso");
        CadastroTestLogger.logTestEnd("getLocation");
    }
}