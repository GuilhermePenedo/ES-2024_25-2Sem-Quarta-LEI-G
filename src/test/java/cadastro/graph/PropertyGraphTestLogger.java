package cadastro.graph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe responsável por gerenciar os logs dos testes do PropertyGraph
 * 
 * @author Lei-G
 * @date 2024-04-06 21:30
 */
public class PropertyGraphTestLogger {
    private static final String LOG_FILE = "test-results/PropertyGraphTest.log";
    private static BufferedWriter writer;
    private static final String SEPARATOR = "=".repeat(80);

    static {
        try {
            // Cria o diretório se não existir
            new File("test-results").mkdirs();
            // Inicializa o writer em modo append
            writer = new BufferedWriter(new FileWriter(LOG_FILE, true));
        } catch (IOException e) {
            System.err.println("Erro ao inicializar o logger: " + e.getMessage());
        }
    }

    /**
     * Registra uma mensagem de log com timestamp
     * 
     * @param message Mensagem a ser registrada
     */
    public static void log(String message) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write(String.format("[%s] %s%n", timestamp, message));
            writer.flush();
        } catch (IOException e) {
            System.err.println("Erro ao registrar log: " + e.getMessage());
        }
    }

    /**
     * Registra o início de um teste
     * 
     * @param testName Nome do teste
     */
    public static void logTestStart(String testName) {
        try {
            writer.write(SEPARATOR + "\n");
            writer.flush();
        } catch (IOException e) {
            System.err.println("Erro ao registrar separador: " + e.getMessage());
        }
        log(String.format("Iniciando teste: %s", testName));
    }

    /**
     * Registra o fim de um teste
     * 
     * @param testName Nome do teste
     */
    public static void logTestEnd(String testName) {
        log(String.format("Finalizando teste: %s", testName));
        try {
            writer.write(SEPARATOR + "\n\n");
            writer.flush();
        } catch (IOException e) {
            System.err.println("Erro ao registrar separador: " + e.getMessage());
        }
    }

    /**
     * Registra uma mensagem de sucesso
     * 
     * @param message Mensagem de sucesso
     */
    public static void logSuccess(String message) {
        log(String.format("SUCESSO: %s", message));
    }

    /**
     * Registra uma mensagem de erro
     * 
     * @param message Mensagem de erro
     */
    public static void logError(String message) {
        log(String.format("ERRO: %s", message));
    }

    /**
     * Fecha o arquivo de log
     */
    public static void close() {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            System.err.println("Erro ao fechar o logger: " + e.getMessage());
        }
    }
} 