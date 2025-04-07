package cadastro.gui;

/**
 * Classe que armazena todas as constantes utilizadas na interface gráfica.
 * 
 * @author [Lei-G]
 * @version 1.0
 */
public class GUIConstants {
    // Constantes para configuração da janela
    public static final String WINDOW_TITLE = "Gestão de Propriedades";
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;
    public static final int SHAPE_WINDOW_SIZE = 600;

    // Constantes para botões
    public static final String BROWSE_BUTTON_TEXT = "Procurar";
    public static final String IMPORT_BUTTON_TEXT = "Importar";
    public static final String SHOW_MORE_BUTTON_TEXT = "Mais";
    public static final String SHOW_SHAPE_BUTTON_TEXT = "Mostrar shape";

    // Constantes para rótulos
    public static final String FILE_SELECTION_LABEL = "Selecione o arquivo CSV:";

    // Constantes para mensagens de erro e aviso
    public static final String ERROR_TITLE = "Erro";
    public static final String WARNING_TITLE = "Aviso";
    public static final String FILE_NOT_FOUND_ERROR = "Arquivo selecionado não existe";
    public static final String SELECT_FILE_WARNING = "Por favor, selecione um arquivo CSV primeiro.";
    public static final String EMPTY_FILE_ERROR = "Nenhum cadastro foi importado do arquivo";
    public static final String EMPTY_LIST_ERROR = "Nenhum cadastro para ";
    public static final String NULL_CADASTRO_ERROR = "Cadastro não pode ser nulo";

    // Constantes para formatação
    public static final String CADASTRO_INFO_FORMAT = "<html>Id: %s<br>Proprietário: %s<br>Área: %s<br>Comprimento: %s</html>";
    public static final String SHAPE_WINDOW_TITLE = "Shape - ";

    // Constantes para ordenação
    public static final String[] SORT_BUTTON_LABELS = {
        "Sort by ID",
        "Sort by Length",
        "Sort by Area",
        "Sort by Owner"
    };

    // Constante para carregamento de cadastros
    public static final int DEFAULT_CADASTROS_LOAD = 5000;
} 