package cadastro.importer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Classe que representa um cadastro de propriedade, contendo informações como
 * identificador, comprimento, área, forma geométrica, proprietário e
 * localização.
 * 
 * @author [Lei-G]
 * @version 1.0
 */
public class Cadastro {
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
     * @throws ParseException           Se houver erro ao processar a geometria WKT
     * @throws IllegalArgumentException Se houver erro ao converter valores
     *                                  numéricos
     */
    public Cadastro(CSVRecord record) throws ParseException {
        try {
            this.id = handleId(record.get(CadastroConstants.ID_INDEX));
            this.length = handleLength(record.get(CadastroConstants.LENGTH_INDEX));
            this.area = handleArea(record.get(CadastroConstants.AREA_INDEX));
            this.shape = handleShape(record.get(CadastroConstants.SHAPE_INDEX));
            this.owner = handleOwner(record.get(CadastroConstants.OWNER_INDEX));
            this.location = handleLocation(record);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(CadastroConstants.NUMBER_CONVERSION_ERROR, e);
        }
    }

    /**
     * Verifica o campo id da propriedade.
     * 
     * @param record A string contendo a id
     * @return O ID da propriedade do Cadastro correspondente
     * @throws IllegalArgumentException Se o id for null ou se for menor ou igual
     *                                  a zero
     */
    private int handleId(String idStr) {
        if (idStr == null || idStr.trim().isEmpty()) {
            throw new IllegalArgumentException("ID" + CadastroConstants.NULL_OR_EMPTY_ERROR);
        }
        int id = Integer.parseInt(idStr);
        if (id <= 0) {
            throw new IllegalArgumentException("ID" + CadastroConstants.ZERO_OR_NEGATIVE_ERROR);
        }
        return id;
    }

    /**
     * Verifica o campo length da propriedade.
     * 
     * @param record A string contendo a length
     * @return A Length da propriedade do Cadastro correspondente
     * @throws IllegalArgumentException Se o comprimento for null ou se for menor ou
     *                                  igual a zero
     */
    private double handleLength(String lengthStr) {
        if (lengthStr == null || lengthStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Comprimento" + CadastroConstants.NULL_OR_EMPTY_ERROR);
        }
        double length = Double.parseDouble(lengthStr);
        if (length <= 0) {
            throw new IllegalArgumentException("Comprimento" + CadastroConstants.ZERO_OR_NEGATIVE_ERROR);
        }
        return length;
    }

    /**
     * Verifica o campo area da propriedade.
     * 
     * @param record A string contendo a area
     * @return A Área da propriedade do Cadastro correspondente
     * @throws IllegalArgumentException Se a área for null ou se for menor ou igual
     *                                  a zero
     */
    private double handleArea(String areaStr) {
        if (areaStr == null || areaStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Área" + CadastroConstants.NULL_OR_EMPTY_ERROR);
        }
        double area = Double.parseDouble(areaStr);
        if (area <= 0) {
            throw new IllegalArgumentException("Área" + CadastroConstants.ZERO_OR_NEGATIVE_ERROR);
        }
        return area;
    }

    /**
     * Processa a string WKT (Well-Known Text) para criar um objeto MultiPolygon.
     * 
     * @param record A string WKT contendo a geometria
     * @return O objeto MultiPolygon correspondente
     * @throws ParseException           Se houver erro ao processar a geometria
     * @throws IllegalArgumentException Se a geometria não for um MultiPolygon
     */
    private MultiPolygon handleShape(String record) throws ParseException {
        try {
            WKTReader reader = new WKTReader();
            Geometry geometry = reader.read(record);
            if (geometry instanceof MultiPolygon multiPolygon) {
                return multiPolygon;
            } else {
                throw new IllegalArgumentException(record + CadastroConstants.INVALID_GEOMETRY_ERROR);
            }
        } catch (ParseException e) {
            throw e;
        }
    }

    /**
     * Verifica o campo owner da propriedade.
     * 
     * @param record A string contendo a owner
     * @return O Owner da propriedade do Cadastro correspondente
     * @throws IllegalArgumentException Se o owner for null ou se for menor ou igual
     *                                  a zero
     */
    private int handleOwner(String ownerStr) {
        if (ownerStr == null || ownerStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner" + CadastroConstants.NULL_OR_EMPTY_ERROR);
        }
        int owner = Integer.parseInt(ownerStr);
        if (owner <= 0) {
            throw new IllegalArgumentException("Owner" + CadastroConstants.ZERO_OR_NEGATIVE_ERROR);
        }
        return owner;
    }

    /**
     * Processa as localizações do registro CSV, removendo valores "NA".
     * 
     * @param record O registro CSV contendo as localizações
     * @return Lista de localizações processadas
     */
    private List<String> handleLocation(CSVRecord record) {
        return record.stream()
                .skip(CadastroConstants.LOCATION_START_INDEX)
                .filter(s -> !s.equals(CadastroConstants.NA_VALUE))
                .toList();
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

        try (Reader in = new FileReader(path);
                CSVParser parser = CSVFormat.newFormat(';').parse(in)) {

            List<CSVRecord> records = parser.getRecords();
            int totalRecords = records.size();
            int skippedRecords = 0;

            for (int i = 1; i < totalRecords; i++) {
                try {
                    Cadastro cadastro = new Cadastro(records.get(i));
                    cadastros.add(cadastro);
                } catch (IllegalArgumentException | ParseException e) {
                    skippedRecords++;
                }
            }

            if (cadastros.isEmpty()) {
                throw new IllegalStateException(CadastroConstants.EMPTY_FILE_ERROR);
            }

            System.out.println("Total de cadastros: " + cadastros.size());  
            System.out.println("Total de registros ignorados: " + skippedRecords);
            return cadastros;
        } catch (IOException e) {
            throw new Exception(CadastroConstants.FILE_READ_ERROR, e);
        }
    }

    /**
     * Ordena uma lista de cadastros de acordo com o critério especificado.
     * 
     * @param cadastros A lista de cadastros a ser ordenada
     * @param sortType  O tipo de ordenação (ID, comprimento, área ou proprietário)
     * @return A lista de cadastros ordenada
     * @throws Exception Se o tipo de ordenação for inválido
     */
    public static List<Cadastro> sortCadastros(List<Cadastro> cadastros, int sortType) throws Exception {
        switch (sortType) {
            case CadastroConstants.SORT_BY_ID:
                cadastros.sort(Comparator.comparingInt(Cadastro::getId));
                break;
            case CadastroConstants.SORT_BY_LENGTH:
                cadastros.sort(Comparator.comparingDouble(Cadastro::getLength));
                break;
            case CadastroConstants.SORT_BY_AREA:
                cadastros.sort(Comparator.comparingDouble(Cadastro::getArea));
                break;
            case CadastroConstants.SORT_BY_OWNER:
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
