package cadastro.graph;

import cadastro.importer.Cadastro;
import org.locationtech.jts.geom.MultiPolygon;

import java.util.*;

public class PropertyGraph {
    
    private final List<Cadastro> cadastros;
    private final Map<Cadastro, Set<Cadastro>> adjacencyList;

    public PropertyGraph(List<Cadastro> cadastros) {
        this.cadastros = cadastros;
        this.adjacencyList = new HashMap<>();
        buildGraph();
    }

    private void buildGraph() {
        for (int i = 0; i < cadastros.size(); i++) {
            for (int j = i + 1; j < cadastros.size(); j++) {
                Cadastro prop1 = cadastros.get(i);
                Cadastro prop2 = cadastros.get(j);
                
                if (arePropertiesPhysicallyAdjacent(prop1, prop2)) {
                    addAdjacency(prop1, prop2);
                }
            }
        }
    }

    private boolean arePropertiesPhysicallyAdjacent(Cadastro prop1, Cadastro prop2) {
        MultiPolygon shape1 = prop1.getShape();
        MultiPolygon shape2 = prop2.getShape();
        return shape1.intersects(shape2);
    }
    
    private void addAdjacency(Cadastro property1, Cadastro property2) {
        adjacencyList.computeIfAbsent(property1, k -> new HashSet<>()).add(property2);
        adjacencyList.computeIfAbsent(property2, k -> new HashSet<>()).add(property1);
    }

    public Set<Cadastro> getAdjacentProperties(Cadastro property) {
        return adjacencyList.getOrDefault(property, new HashSet<>());
    }
    
    public boolean areAdjacent(Cadastro property1, Cadastro property2) {
        return adjacencyList.containsKey(property1) && adjacencyList.get(property1).contains(property2);
    }
    
    public int getNumberOfProperties() {
        return cadastros.size();
    }
    
    public int getNumberOfAdjacencies() {
        int count = 0;
        for (Set<Cadastro> adjacents : adjacencyList.values()) {
            count += adjacents.size();
        }
        return count / 2;
    }

    @Override
    public String toString() {
        String resultado = "";
        for (Cadastro cadastro : cadastros) {
            String propriedade  = "Propriedade " + cadastro.getId() + " (Área: " + cadastro.getArea() + ", Proprietário: " + cadastro.getOwner() + ")";
            Set<Cadastro> adjacentes = getAdjacentProperties(cadastro);
            if (!adjacentes.isEmpty()) {
                propriedade += "\n  Adjacente a: ";
                for (Cadastro adj : adjacentes) {
                    propriedade += adj.getId() + ", ";
                }
            }
            resultado += propriedade + "\n";
        }
        return resultado;
    }

}