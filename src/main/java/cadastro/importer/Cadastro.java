package cadastro.importer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

<<<<<<< Updated upstream
public class Cadastro {
=======
public class Cadastro{
    private static final Logger logger = LoggerFactory.getLogger(Cadastro.class);
>>>>>>> Stashed changes

    public static final int SORT_BY_ID = 0;
    public static final int SORT_BY_LENGTH = 1;
    public static final int SORT_BY_AREA = 2;
    public static final int SORT_BY_OWNER = 3;

    private final int id;
    private final double length;
    private final double area;
    private final MultiPolygon shape;
    private final int owner;
    private final List<String> location;

    Cadastro(CSVRecord record) throws ParseException {
        this.id = Integer.parseInt(record.get(0));
        this.length = Double.parseDouble(record.get(3));
        this.area = Double.parseDouble(record.get(4));
        this.shape = handleShape(record.get(5));
        this.owner = Integer.parseInt(record.get(6));
        this.location = handleLocation(record);
    }

    private MultiPolygon handleShape(String record) throws ParseException {
        WKTReader reader = new WKTReader();
        Geometry geometry = reader.read(record);
        if (geometry instanceof MultiPolygon multiPolygon) {
            return multiPolygon;
        }else{
            throw new IllegalArgumentException(record + " is not not a multipolygon");
        }
    }

    private List<String> handleLocation(CSVRecord record) {
        return record.stream().skip(7).filter(s -> !s.equals("NA")).toList();
    }

    public static List<Cadastro> getCadastros(String path) throws Exception {
        List<Cadastro> cadastros = new ArrayList<>();

        try (Reader in = new FileReader(path);
             CSVParser parser = CSVFormat.newFormat(';').parse(in)) {

            List<CSVRecord> records = parser.getRecords();
            for(int i = 1; i < records.size(); i++) {
                cadastros.add(new Cadastro(records.get(i)));
            }
            return cadastros;
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Erro ao ler o ficheiro CSV", e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

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
