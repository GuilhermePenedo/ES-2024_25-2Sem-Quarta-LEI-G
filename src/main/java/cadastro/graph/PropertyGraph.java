package cadastro.graph;

import cadastro.importer.Cadastro;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.TopologyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Classe que representa um grafo de propriedades, onde os vértices são cadastros
 * e as arestas representam adjacências físicas entre as propriedades.
 * 
 * @author [Lei-G]
 * @version 1.0
 */
public class PropertyGraph {
    private static final Logger logger = LoggerFactory.getLogger(PropertyGraph.class);
    
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
            logger.error("Lista de cadastros não pode ser nula");
            throw new IllegalArgumentException("Lista de cadastros não pode ser nula");
        }
        if (cadastros.isEmpty()) {
            logger.error("Lista de cadastros não pode estar vazia");
            throw new IllegalArgumentException("Lista de cadastros não pode estar vazia");
        }
        if (cadastros.contains(null)) {
            logger.error("Lista de cadastros não pode conter elementos nulos");
            throw new IllegalArgumentException("Lista de cadastros não pode conter elementos nulos");
        }

        logger.info("Iniciando construção do grafo de propriedades");
        this.cadastros = cadastros;
        this.adjacencyList = new HashMap<>();
        buildGraph();
        logger.info("Grafo construído com sucesso. Total de propriedades: {}", cadastros.size());
    }

    /**
     * Constrói o grafo verificando adjacências entre todas as propriedades.
     * @throws TopologyException se ocorrer um erro durante a análise topológica
     */
    private void buildGraph() {
        logger.debug("Iniciando construção das adjacências");
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
            logger.debug("Construção das adjacências concluída");
        } catch (TopologyException e) {
            logger.error("Erro durante a análise topológica: {}", e.getMessage());
            throw new IllegalStateException("Erro durante a construção do grafo: " + e.getMessage(), e);
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
            logger.error("Propriedades não podem ser nulas");
            throw new IllegalArgumentException("Propriedades não podem ser nulas");
        }

        try {
            MultiPolygon shape1 = prop1.getShape();
            MultiPolygon shape2 = prop2.getShape();
            
            if (shape1 == null || shape2 == null) {
                logger.error("Forma geométrica não pode ser nula para as propriedades {} e {}", 
                    prop1.getId(), prop2.getId());
                return false;
            }

            boolean adjacent = shape1.touches(shape2) || 
                             (shape1.intersects(shape2) && !shape1.within(shape2) && !shape2.within(shape1));
            
            if (adjacent) {
                logger.debug("Propriedades {} e {} são adjacentes", prop1.getId(), prop2.getId());
                logger.trace("Geometria 1: {}", shape1);
                logger.trace("Geometria 2: {}", shape2);
            }
            return adjacent;
        } catch (TopologyException e) {
            logger.error("Erro durante a análise topológica entre propriedades {} e {}: {}", 
                prop1.getId(), prop2.getId(), e.getMessage());
            throw new IllegalStateException("Erro durante a análise de adjacência: " + e.getMessage(), e);
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
            logger.error("Propriedades não podem ser nulas ao adicionar adjacência");
            throw new IllegalArgumentException("Propriedades não podem ser nulas");
        }

        logger.debug("Adicionando adjacência entre propriedades {} e {}", property1.getId(), property2.getId());
        adjacencyList.computeIfAbsent(property1, _ -> new HashSet<>()).add(property2);
        adjacencyList.computeIfAbsent(property2, _ -> new HashSet<>()).add(property1);
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
            logger.error("Propriedade não pode ser nula ao obter adjacências");
            throw new IllegalArgumentException("Propriedade não pode ser nula");
        }

        logger.debug("Obtendo propriedades adjacentes para {}", property.getId());
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
            logger.error("Propriedades não podem ser nulas ao verificar adjacência");
            throw new IllegalArgumentException("Propriedades não podem ser nulas");
        }

        logger.debug("Verificando adjacência entre propriedades {} e {}", property1.getId(), property2.getId());
        return adjacencyList.containsKey(property1) && adjacencyList.get(property1).contains(property2);
    }
    
    /**
     * Retorna o número total de propriedades no grafo.
     * 
     * @return Número de propriedades
     */
    public int getNumberOfProperties() {
        logger.debug("Obtendo número total de propriedades");
        return cadastros.size();
    }
    
    /**
     * Retorna o número total de adjacências no grafo.
     * 
     * @return Número de adjacências
     */
    public int getNumberOfAdjacencies() {
        logger.debug("Obtendo número total de adjacências");
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
        logger.debug("Gerando representação em string do grafo");
        StringBuilder sb = new StringBuilder();
        sb.append("PropertyGraph{properties=[");
        for (int i = 0; i < cadastros.size(); i++) {
            sb.append(cadastros.get(i).toString());
            if (i < cadastros.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("], adjacencies=[]}");
        return sb.toString();
    }
}