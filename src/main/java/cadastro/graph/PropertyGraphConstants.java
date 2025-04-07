package cadastro.graph;

/**
 * Classe que armazena todas as constantes utilizadas na classe PropertyGraph.
 * 
 * @author [Lei-G]
 * @version 1.0
 */
public class PropertyGraphConstants {
    // Mensagens de erro para validação
    public static final String NULL_CADASTROS_ERROR = "Lista de cadastros não pode ser nula";
    public static final String EMPTY_CADASTROS_ERROR = "Lista de cadastros não pode estar vazia";
    public static final String NULL_ELEMENTS_ERROR = "Lista de cadastros não pode conter elementos nulos";
    public static final String NULL_PROPERTY_ERROR = "Propriedades não podem ser nulas";
    public static final String NULL_SHAPE_ERROR = "Shape não pode ser nulo";
    public static final String TOPOLOGY_ERROR = "Erro durante a análise topológica: ";
    public static final String GRAPH_BUILD_ERROR = "Erro durante a construção do grafo: ";
    public static final String ADJACENCY_ERROR = "Erro durante a análise de adjacência: ";

    // Constantes para formatação de strings
    public static final String GRAPH_STRING_FORMAT = "PropertyGraph{properties=[%s], adjacencies=[]}";
    public static final String PROPERTY_SEPARATOR = ", ";
} 