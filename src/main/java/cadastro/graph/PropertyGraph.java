package cadastro.graph;

import cadastro.importer.Cadastro;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.TopologyException;

import java.util.*;

/**
 * Classe que representa um grafo de propriedades, onde os vértices são cadastros
 * e as arestas representam adjacências físicas entre as propriedades.
 * 
 * @author [Lei-G]
 * @version 1.0
 */
public class PropertyGraph {
    private final List<Cadastro> cadastros;
    private final Map<Cadastro, Set<Cadastro>> adjacencyList;

    /**
     * Constrói um grafo de propriedades a partir de uma lista de cadastros.
     * 
     * @param cadastros Lista de cadastros que serão os vértices do grafo
     * @throws IllegalArgumentException se a lista de cadastros for nula ou vazia
     */
    public PropertyGraph(List<Cadastro> cadastros) {
        if (cadastros == null) {
            throw new IllegalArgumentException(PropertyGraphConstants.NULL_CADASTROS_ERROR);
        }
        if (cadastros.isEmpty()) {
            throw new IllegalArgumentException(PropertyGraphConstants.EMPTY_CADASTROS_ERROR);
        }
        if (cadastros.contains(null)) {
            throw new IllegalArgumentException(PropertyGraphConstants.NULL_ELEMENTS_ERROR);
        }

        this.cadastros = cadastros;
        this.adjacencyList = new HashMap<>();
        buildGraph();
    }

    /**
     * Constrói o grafo verificando adjacências entre todas as propriedades.
     * @throws TopologyException se ocorrer um erro durante a análise topológica
     */
    private void buildGraph() {
        try {
            for (int i = 0; i < cadastros.size(); i++) {
                for (int j = i + 1; j < cadastros.size(); j++) {
                    Cadastro prop1 = cadastros.get(i);
                    Cadastro prop2 = cadastros.get(j);
                    
                    if (arePropertiesPhysicallyAdjacent(prop1, prop2)) {
                        addAdjacency(prop1, prop2);
                    }
                }
            }
        } catch (TopologyException e) {
            throw new IllegalStateException(PropertyGraphConstants.GRAPH_BUILD_ERROR + e.getMessage(), e);
        }
    }

    /**
     * Verifica se duas propriedades são fisicamente adjacentes.
     * 
     * @param prop1 Primeira propriedade
     * @param prop2 Segunda propriedade
     * @return true se as propriedades são adjacentes, false caso contrário
     * @throws IllegalArgumentException se alguma das propriedades for nula
     * @throws TopologyException se ocorrer um erro durante a análise topológica
     */
    private boolean arePropertiesPhysicallyAdjacent(Cadastro prop1, Cadastro prop2) {
        if (prop1 == null || prop2 == null) {
            throw new IllegalArgumentException(PropertyGraphConstants.NULL_PROPERTY_ERROR);
        }

        try {
            MultiPolygon shape1 = prop1.getShape();
            MultiPolygon shape2 = prop2.getShape();
            
            if (shape1 == null || shape2 == null) {
                return false;
            }

            return shape1.touches(shape2) || 
                   (shape1.intersects(shape2) && !shape1.within(shape2) && !shape2.within(shape1));
        } catch (TopologyException e) {
            throw new IllegalStateException(PropertyGraphConstants.ADJACENCY_ERROR + e.getMessage(), e);
        }
    }
    
    /**
     * Adiciona uma adjacência entre duas propriedades no grafo.
     * 
     * @param property1 Primeira propriedade
     * @param property2 Segunda propriedade
     * @throws IllegalArgumentException se alguma das propriedades for nula
     */
    private void addAdjacency(Cadastro property1, Cadastro property2) {
        if (property1 == null || property2 == null) {
            throw new IllegalArgumentException(PropertyGraphConstants.NULL_PROPERTY_ERROR);
        }

        adjacencyList.computeIfAbsent(property1, k -> new HashSet<>()).add(property2);
        adjacencyList.computeIfAbsent(property2, k -> new HashSet<>()).add(property1);
    }

    /**
     * Retorna o conjunto de propriedades adjacentes a uma propriedade específica.
     * 
     * @param property A propriedade cujas adjacências serão retornadas
     * @return Conjunto de propriedades adjacentes
     * @throws IllegalArgumentException se a propriedade for nula
     */
    public Set<Cadastro> getAdjacentProperties(Cadastro property) {
        if (property == null) {
            throw new IllegalArgumentException(PropertyGraphConstants.NULL_PROPERTY_ERROR);
        }

        return Collections.unmodifiableSet(adjacencyList.getOrDefault(property, new HashSet<>()));
    }
    
    /**
     * Verifica se duas propriedades são adjacentes no grafo.
     * 
     * @param property1 Primeira propriedade
     * @param property2 Segunda propriedade
     * @return true se as propriedades são adjacentes, false caso contrário
     * @throws IllegalArgumentException se alguma das propriedades for nula
     */
    public boolean areAdjacent(Cadastro property1, Cadastro property2) {
        if (property1 == null || property2 == null) {
            throw new IllegalArgumentException(PropertyGraphConstants.NULL_PROPERTY_ERROR);
        }

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
        StringBuilder sb = new StringBuilder();
        sb.append("PropertyGraph{properties=[");
        for (int i = 0; i < cadastros.size(); i++) {
            sb.append(cadastros.get(i).toString());
            if (i < cadastros.size() - 1) {
                sb.append(PropertyGraphConstants.PROPERTY_SEPARATOR);
            }
        }
        sb.append("], adjacencies=[]}");
        return sb.toString();
    }
}