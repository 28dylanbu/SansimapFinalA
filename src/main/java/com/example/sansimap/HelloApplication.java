package com.example.sansimap; // Paquete principal del proyecto SansiMap

import javafx.application.Application; // Permite crear aplicaciones JavaFX
import javafx.fxml.FXMLLoader; // Permite cargar archivos FXML
import javafx.scene.Scene; // Representa el contenido visual de la ventana
import javafx.stage.Stage; // Representa la ventana principal

// Clase principal que inicia toda la aplicación
public class HelloApplication extends Application {

    // Método que se ejecuta automáticamente al iniciar JavaFX
    @Override
    public void start(Stage stage) throws Exception {

        // Crea el archivo de aulas si no existe
        GestorArchivo.crearArchivoSiNoExiste();

        // Carga la interfaz inicial desde inicio.fxml
        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("inicio.fxml"));

        // Crea la escena principal con tamaño inicial
        Scene scene = new Scene(loader.load(), 800, 600);

        // Cambia el título de la ventana
        stage.setTitle("SansiMap");

        // Coloca la escena en la ventana
        stage.setScene(scene);

        // Permite cambiar el tamaño de la ventana
        stage.setResizable(true);

        // Muestra la ventana en pantalla
        stage.show();
    }

    // Método principal que inicia el programa
    public static void main(String[] args) {

        // Inicia JavaFX
        launch(args);
    }
}