package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import cadastro.importer.Cadastro;

import java.util.List;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carrega os dados cadastrais
        List<Cadastro> cadastros = Cadastro.getCadastros("Dados/Madeira-Moodle-1.1.csv");

        // Carrega o FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mapa.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);
        
        // Obtém o controlador e configura os cadastros
        MapaController controller = loader.getController();
        controller.setCadastros(cadastros);

        // Configura a janela principal
        primaryStage.setTitle("Visualização de Dados Cadastrais");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
