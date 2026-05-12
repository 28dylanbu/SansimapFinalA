package com.example.sansimap;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CambiarVista {
    //Con Evento
    public static void cambiarContenidoVista(Event event, String fxml) {
        try {
            // 1. Cargar el nuevo diseño (root)
            FXMLLoader loader = new FXMLLoader(CambiarVista.class.getResource(fxml));
            Parent nuevoRoot = loader.load();

            // 2. Obtener la escena actual desde el evento
            Scene escenaActual = ((Node) event.getSource()).getScene();

            // 3. Cambiar solo el contenido principal
            escenaActual.setRoot(nuevoRoot);

            // 4. Asegurar que la ventana siga en pantalla completa si así estaba
            Stage stage = (Stage) escenaActual.getWindow();
            if (stage.isFullScreen()) {
                stage.setFullScreen(true);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar la vista: " + fxml);
        }
    }
    public static void cambiarContenidoVista(String fxml){

    }
}
