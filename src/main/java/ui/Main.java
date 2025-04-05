package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import cadastro.importer.Cadastro;

import java.util.List;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carrega os dados cadastrais
        List<Cadastro> cadastros = Cadastro.getCadastros("Dados/Madeira-Moodle-1.1.csv");

        // Cria o mapa
        MapaVisualizacao mapaVisualizacao = new MapaVisualizacao(cadastros);

        // Configura a cena
        Scene scene = new Scene(mapaVisualizacao, 800, 600);

        // Configura a janela principal
        primaryStage.setTitle("Visualização de Dados Cadastrais");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
