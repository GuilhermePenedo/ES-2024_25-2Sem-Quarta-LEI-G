package cadastro.graph;

import cadastro.importer.Cadastro;
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
    /**
     * Método principal que demonstra o uso do grafo de propriedades.
     * Carrega os cadastros de um arquivo CSV, constrói o grafo
     * e exibe informações sobre as adjacências.
     *
     * @param args Argumentos da linha de comando (não utilizados)
     * @throws Exception Se houver erro ao carregar ou processar os cadastros
     */
    public static void main(String[] args) throws Exception {
        String filePath = "Dados/Madeira-Moodle-1.1.csv";
        
        List<Cadastro> cadastros = Cadastro.getCadastros(filePath);
        
        new PropertyGraph(cadastros);
    }
}
