package cadastro.graph;

import cadastro.importer.Cadastro;
import org.locationtech.jts.geom.MultiPolygon;

import java.util.*;

/**
 * Classe que representa um grafo de propriedades, onde os vértices são cadastros
 * e as arestas representam adjacências físicas entre as propriedades.
 * 
 * @author [Nome do Autor]
 * @version 1.0
 */
public class PropertyGraph {
    
    private final List<Cadastro> cadastros;
    private final Map<Cadastro, Set<Cadastro>> adjacencyList;

    /**
     * Constrói um grafo de propriedades a partir de uma lista de cadastros.
     * 
     * @param cadastros Lista de cadastros que serão os vértices do grafo
     */
    public PropertyGraph(List<Cadastro> cadastros) {
        this.cadastros = cadastros;
        this.adjacencyList = new HashMap<>();
        buildGraph();
    }

    /**
     * Constrói o grafo verificando adjacências entre todas as propriedades.
     */
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

    /**
     * Verifica se duas propriedades são fisicamente adjacentes.
     * 
     * @param prop1 Primeira propriedade
     * @param prop2 Segunda propriedade
     * @return true se as propriedades são adjacentes, false caso contrário
     */
    private boolean arePropertiesPhysicallyAdjacent(Cadastro prop1, Cadastro prop2) {
        MultiPolygon shape1 = prop1.getShape();
        MultiPolygon shape2 = prop2.getShape();
        return shape1.intersects(shape2);
    }
    
    /**
     * Adiciona uma adjacência entre duas propriedades no grafo.
     * 
     * @param property1 Primeira propriedade
     * @param property2 Segunda propriedade
     */
    private void addAdjacency(Cadastro property1, Cadastro property2) {
        adjacencyList.computeIfAbsent(property1, k -> new HashSet<>()).add(property2);
        adjacencyList.computeIfAbsent(property2, k -> new HashSet<>()).add(property1);
    }

    /**
     * Retorna o conjunto de propriedades adjacentes a uma propriedade específica.
     * 
     * @param property A propriedade cujas adjacências serão retornadas
     * @return Conjunto de propriedades adjacentes
     */
    public Set<Cadastro> getAdjacentProperties(Cadastro property) {
        return adjacencyList.getOrDefault(property, new HashSet<>());
    }
    
    /**
     * Verifica se duas propriedades são adjacentes no grafo.
     * 
     * @param property1 Primeira propriedade
     * @param property2 Segunda propriedade
     * @return true se as propriedades são adjacentes, false caso contrário
     */
    public boolean areAdjacent(Cadastro property1, Cadastro property2) {
        return adjacencyList.containsKey(property1) && adjacencyList.get(property1).contains(property2);
    }
    
    /**
     * Retorna o número total de propriedades no grafo.
     * 
     * @return Número de propriedades
     */
    public int getNumberOfProperties() {
        return cadastros.size();
    }
    
    /**
     * Retorna o número total de adjacências no grafo.
     * 
     * @return Número de adjacências
     */
    public int getNumberOfAdjacencies() {
        int count = 0;
        for (Set<Cadastro> adjacents : adjacencyList.values()) {
            count += adjacents.size();
        }
        return count / 2;
    }

    /**
     * Retorna uma representação em string do grafo, mostrando cada propriedade
     * e suas adjacências.
     * 
     * @return String representando o grafo
     */
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