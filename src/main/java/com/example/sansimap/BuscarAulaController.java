package com.example.sansimap;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class BuscarAulaController {

    @FXML
    private TextField inputAula;

    // Variable para pasar el dato sin tocar CambiarVista
    public static String aulaSeleccionada;

    @FXML
    private void buscarAula(ActionEvent event) {
        if (inputAula == null) return;

        String texto = inputAula.getText();

        if (texto != null && !texto.trim().isEmpty()) {
            // Guardamos el código
            aulaSeleccionada = texto.trim();
            System.out.println("🔎 Buscando aula: " + aulaSeleccionada);

            // Usamos tu método original de 2 parámetros
            CambiarVista.cambiarContenidoVista(event, "resultado-aula.fxml");
        }
    }

    @FXML
    private void volverMenu() {
        try {
            Stage stage = (Stage) inputAula.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}