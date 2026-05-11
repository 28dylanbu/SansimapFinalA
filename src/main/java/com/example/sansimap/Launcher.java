package com.example.sansimap; // Paquete principal del proyecto SansiMap

// Importa la clase base de JavaFX
import javafx.application.Application;

// Clase auxiliar que inicia la aplicación JavaFX
public class Launcher {

    // Método principal del programa
    public static void main(String[] args) {

        // Inicia la aplicación usando HelloApplication
        Application.launch(
                HelloApplication.class,
                args
        );
    }
}