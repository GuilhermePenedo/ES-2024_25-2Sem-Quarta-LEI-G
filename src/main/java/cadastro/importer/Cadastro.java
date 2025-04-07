package cadastro.importer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Classe que representa um cadastro de propriedade, contendo informações como
 * identificador, comprimento, área, forma geométrica, proprietário e localização.
 * 
 * @author [Lei-G]
 * @version 1.0
 */
public class Cadastro {
    private static final Logger logger = LoggerFactory.getLogger(Cadastro.class);

    /** Constante para ordenação por ID */
    public static final int SORT_BY_ID = 0;
    /** Constante para ordenação por comprimento */
    public static final int SORT_BY_LENGTH = 1;
    /** Constante para ordenação por área */
    public static final int SORT_BY_AREA = 2;
    /** Constante para ordenação por proprietário */
    public static final int SORT_BY_OWNER = 3;

    private final int id;
    private final double length;
    private final double area;
    private final MultiPolygon shape;
    private final int owner;
    private final List<String> location;

    /**
     * Constrói um objeto Cadastro a partir de um registro CSV.
     * 
     * @param record O registro CSV contendo os dados do cadastro
     * @throws ParseException Se houver erro ao processar a geometria WKT
     * @throws IllegalArgumentException Se houver erro ao converter valores numéricos
     */
    public Cadastro(CSVRecord record) throws ParseException {
        logger.debug("Iniciando criação de cadastro a partir do registro: {}", record);
        try {
            this.id = Integer.parseInt(record.get(0));
            logger.trace("ID do cadastro: {}", this.id);
            
            // Validação do comprimento
            String lengthStr = record.get(3);
            if (lengthStr == null || lengthStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Comprimento não pode ser nulo ou vazio");
            }
            this.length = Double.parseDouble(lengthStr);
            if (this.length <= 0) {
                throw new IllegalArgumentException("Comprimento deve ser maior que zero");
            }
            logger.trace("Comprimento do cadastro: {}", this.length);
            
            // Validação da área
            String areaStr = record.get(4);
            if (areaStr == null || areaStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Área não pode ser nula ou vazia");
            }
            this.area = Double.parseDouble(areaStr);
            if (this.area <= 0) {
                throw new IllegalArgumentException("Área deve ser maior que zero");
            }
            logger.trace("Área do cadastro: {}", this.area);
            
            this.shape = handleShape(record.get(5));
            logger.trace("Geometria processada com sucesso");
            
            this.owner = Integer.parseInt(record.get(6));
            logger.trace("Proprietário do cadastro: {}", this.owner);
            
            this.location = handleLocation(record);
            logger.trace("Localizações processadas: {}", this.location);
            
            logger.info("Cadastro {} criado com sucesso", this.id);
        } catch (NumberFormatException e) {
            logger.error("Erro ao converter valores numéricos do registro: {}", record, e);
            throw new IllegalArgumentException("Erro ao converter valores numéricos", e);
        }
    }

    /**
     * Processa a string WKT (Well-Known Text) para criar um objeto MultiPolygon.
     * 
     * @param record A string WKT contendo a geometria
     * @return O objeto MultiPolygon correspondente
     * @throws ParseException Se houver erro ao processar a geometria
     * @throws IllegalArgumentException Se a geometria não for um MultiPolygon
     */
    private MultiPolygon handleShape(String record) throws ParseException {
        logger.debug("Processando geometria WKT: {}", record);
        try {
            WKTReader reader = new WKTReader();
            Geometry geometry = reader.read(record);
            if (geometry instanceof MultiPolygon multiPolygon) {
                logger.debug("Geometria processada com sucesso como MultiPolygon");
                return multiPolygon;
            } else {
                String errorMsg = record + " não é um MultiPolygon";
                logger.error(errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }
        } catch (ParseException e) {
            logger.error("Erro ao processar geometria WKT: {}", record, e);
            throw e;
        }
    }

    /**
     * Processa as localizações do registro CSV, removendo valores "NA".
     * 
     * @param record O registro CSV contendo as localizações
     * @return Lista de localizações processadas
     */
    private List<String> handleLocation(CSVRecord record) {
        logger.debug("Processando localizações do registro");
        List<String> locations = record.stream()
                .skip(7)
                .filter(s -> !s.equals("NA"))
                .toList();
        logger.debug("Localizações processadas: {}", locations);
        return locations;
    }

    /**
     * Lê um arquivo CSV e retorna uma lista de cadastros.
     * 
     * @param path O caminho do arquivo CSV
     * @return Lista de cadastros lidos do arquivo
     * @throws Exception Se houver erro ao ler ou processar o arquivo
     */
    public static List<Cadastro> getCadastros(String path) throws Exception {
        List<Cadastro> cadastros = new ArrayList<>();
        logger.info("Iniciando leitura do arquivo CSV: {}", path);

        try (Reader in = new FileReader(path);
             CSVParser parser = CSVFormat.newFormat(';').parse(in)) {

            List<CSVRecord> records = parser.getRecords();
            int totalRecords = records.size();
            int skippedRecords = 0;
            
            for(int i = 1; i < totalRecords; i++) {
                try {
                    Cadastro cadastro = new Cadastro(records.get(i));
                    cadastros.add(cadastro);
                } catch (IllegalArgumentException | ParseException e) {
                    skippedRecords++;
                    logger.warn("Registro {} ignorado devido a erro: {}", i, e.getMessage());
                }
            }
            
            logger.info("Processamento concluído. Total de registros: {}, Registros válidos: {}, Registros ignorados: {}", 
                totalRecords - 1, cadastros.size(), skippedRecords);
            
            if (cadastros.isEmpty()) {
                logger.error("Nenhum registro válido encontrado no arquivo");
                throw new IllegalStateException("Nenhum registro válido encontrado no arquivo");
            }
            
            return cadastros;
        } catch (IOException e) {
            logger.error("Erro ao ler o arquivo CSV: {}", path, e);
            throw new Exception("Erro ao ler o ficheiro CSV", e);
        }
    }

    /**
     * Ordena uma lista de cadastros de acordo com o critério especificado.
     * 
     * @param cadastros A lista de cadastros a ser ordenada
     * @param sortType O tipo de ordenação (ID, comprimento, área ou proprietário)
     * @return A lista de cadastros ordenada
     * @throws Exception Se o tipo de ordenação for inválido
     */
    public static List<Cadastro> sortCadastros(List<Cadastro> cadastros, int sortType) throws Exception {
        switch (sortType){
            case SORT_BY_ID:
                cadastros.sort(Comparator.comparingInt(Cadastro::getId));
                break;
            case SORT_BY_LENGTH:
                cadastros.sort(Comparator.comparingDouble(Cadastro::getLength));
                break;
            case SORT_BY_AREA:
                cadastros.sort(Comparator.comparingDouble(Cadastro::getArea));
                break;
            case SORT_BY_OWNER:
                cadastros.sort(Comparator.comparingInt(Cadastro::getOwner));
                break;
        }
        return cadastros;
    }

    /**
     * Retorna uma representação em string do cadastro.
     * 
     * @return String contendo os dados do cadastro
     */
    @Override
    public String toString() {
        return "Cadastro{" +
                "id=" + id +
                ", length=" + length +
                ", area=" + area +
                ", shape=" + shape +
                ", owner=" + owner +
                ", location=" + location +
                '}';
    }

    /**
     * Retorna o ID do cadastro.
     * 
     * @return O ID do cadastro
     */
    public int getId() {
        return id;
    }

    /**
     * Retorna o comprimento do cadastro.
     * 
     * @return O comprimento do cadastro
     */
    public double getLength() {
        return length;
    }

    /**
     * Retorna a área do cadastro.
     * 
     * @return A área do cadastro
     */
    public double getArea() {
        return area;
    }

    /**
     * Retorna a forma geométrica do cadastro.
     * 
     * @return O objeto MultiPolygon representando a forma
     */
    public MultiPolygon getShape() {
        return shape;
    }

    /**
     * Retorna o ID do proprietário do cadastro.
     * 
     * @return O ID do proprietário
     */
    public int getOwner() {
        return owner;
    }

    /**
     * Retorna a lista de localizações do cadastro.
     * 
     * @return Lista de localizações
     */
    public List<String> getLocation() {
        return location;
    }
}
