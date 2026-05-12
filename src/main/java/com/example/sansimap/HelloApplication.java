package com.example.sansimap;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        GestorArchivo.crearArchivoSiNoExiste();

        try {
            // 1. Cargar el nuevo archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("inicio.fxml"));
            Parent root = loader.load();

            // 2. Obtener el Stage (la ventana) a partir del evento

            // 3. Crear y configurar la nueva escena
            Scene scene = new Scene(root);
            stage.setTitle("SansiMap");
            stage.setScene(scene);

            // 4. Activar el modo de pantalla completa
            stage.setMaximized(true);

            // Opcional: Configurar el mensaje que aparece al entrar (el "hint")
            // stage.setFullScreenExitHint("Presiona ESC para salir de pantalla completa");

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}