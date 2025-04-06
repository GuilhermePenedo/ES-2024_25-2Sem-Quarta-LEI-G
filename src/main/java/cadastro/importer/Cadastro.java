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
import java.util.List;

public class Cadastro {
    private static final Logger logger = LoggerFactory.getLogger(Cadastro.class);

    private final int id;
    private final double length;
    private final double area;
    private final MultiPolygon shape;
    private final int owner;
    private final List<String> location;

    Cadastro(CSVRecord record) throws ParseException {
        logger.debug("Iniciando criação de cadastro a partir do registro: {}", record);
        try {
            this.id = Integer.parseInt(record.get(0));
            logger.trace("ID do cadastro: {}", this.id);
            
            this.length = Double.parseDouble(record.get(3));
            logger.trace("Comprimento do cadastro: {}", this.length);
            
            this.area = Double.parseDouble(record.get(4));
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

    private List<String> handleLocation(CSVRecord record) {
        logger.debug("Processando localizações do registro");
        List<String> locations = record.stream()
                .skip(7)
                .filter(s -> !s.equals("NA"))
                .toList();
        logger.debug("Localizações processadas: {}", locations);
        return locations;
    }

    public static List<Cadastro> getCadastros(String path) throws Exception {
        List<Cadastro> cadastros = new ArrayList<>();
        logger.info("Iniciando leitura do arquivo CSV: {}", path);

        try (Reader in = new FileReader(path);
             CSVParser parser = CSVFormat.newFormat(';').parse(in)) {

            List<CSVRecord> records = parser.getRecords();
            for(int i = 1; i < records.size(); i++) {
                cadastros.add(new Cadastro(records.get(i)));
            }
            
            logger.info("Leitura do arquivo CSV concluída. Total de cadastros: {}, Total de linhas processadas: {}", 
                    cadastros.size(), rowCount);
            return cadastros;
        } catch (IOException e) {
            logger.error("Erro ao ler o arquivo CSV: {}", path, e);
            throw new Exception("Erro ao ler o ficheiro CSV", e);
        } catch (ParseException e) {
            logger.error("Erro ao processar geometria", e);
            throw new RuntimeException(e);
        }
    }

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

    public int getId() {
        return id;
    }

    public double getLength() {
        return length;
    }

    public double getArea() {
        return area;
    }

    public MultiPolygon getShape() {
        return shape;
    }

    public int getOwner() {
        return owner;
    }

    public List<String> getLocation() {
        return location;
    }
}
