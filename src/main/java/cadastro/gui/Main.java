package cadastro.gui;

/**
 * Classe principal que serve como ponto de entrada da aplicação.
 * Inicializa a interface gráfica e a torna visível.
 * 
 * @author [Nome do Autor]
 * @version 1.0
 */
public class Main {
    /**
     * Método principal que inicia a aplicação.
     *
     * @param args Argumentos da linha de comando (não utilizados)
     */
    public static void main(String[] args) {
        GUI visualizador = new GUI();
        visualizador.setVisible(true);
    }
}