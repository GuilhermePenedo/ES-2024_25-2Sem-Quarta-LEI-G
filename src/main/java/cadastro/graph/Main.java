package cadastro.graph;

import cadastro.importer.Cadastro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Classe principal para demonstração do grafo de propriedades.
 * Carrega cadastros de um arquivo CSV e constrói um grafo
 * representando as adjacências entre as propriedades.
 * 
 * @author [Lei-G]
 * @version 1.0
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * Método principal que demonstra o uso do grafo de propriedades.
     * Carrega os cadastros de um arquivo CSV, constrói o grafo
     * e exibe informações sobre as adjacências.
     *
     * @param args Argumentos da linha de comando (não utilizados)
     * @throws Exception Se houver erro ao carregar ou processar os cadastros
     */
    public static void main(String[] args) throws Exception {
        logger.info("Iniciando aplicação");
        
        String filePath = "Dados/Madeira-Moodle-1.1.csv";
        logger.info("Carregando cadastros do arquivo: {}", filePath);
        
        List<Cadastro> cadastros = Cadastro.getCadastros(filePath);
        logger.info("Total de cadastros carregados: {}", cadastros.size());
        
        // cadastros.forEach(cadastro -> logger.info("Cadastro: {}", cadastro));
        PropertyGraph graph = new PropertyGraph(cadastros);
        logger.info("Grafo de propriedades: {}", graph);
        
        logger.info("Aplicação finalizada com sucesso");
    }
}
