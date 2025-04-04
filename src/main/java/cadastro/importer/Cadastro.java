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
import java.util.List;

public class Cadastro {

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

            boolean isFirstRow = true;
            for (CSVRecord record : parser) {
                //Ignora o header
                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }
                cadastros.add(new Cadastro(record));
            }
            return cadastros;
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Erro ao ler o ficheiro CSV", e);
        } catch (ParseException e) {
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
