package com.example.sansimap;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class HorarioController {

    @FXML private VBox contenedorHorarios;
    @FXML private Label lblAula;
    @FXML private TextField txtBusquedaAula;

    @FXML
    private void procesarBusqueda(ActionEvent event) {
        String aulaABuscar = txtBusquedaAula.getText().trim();

        if (aulaABuscar.isEmpty()) {
            lblAula.setText("Por favor, ingrese un aula");
            return;
        }

        contenedorHorarios.getChildren().clear();
        buscarEnArchivo(aulaABuscar);
    }

    private void buscarEnArchivo(String codigo) {
        try (BufferedReader br = new BufferedReader(new FileReader("cursos_horarios.txt"))) {
            String linea;
            boolean encontrado = false;

            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");

                if (partes.length >= 1 && partes[0].equalsIgnoreCase(codigo)) {
                    encontrado = true;

                    // ESTA ES LA VERIFICACIÓN DE SEGURIDAD:
                    if (lblAula != null) {
                        lblAula.setText("Horarios - Aula " + partes[0].toUpperCase());
                    }

                    for (int i = 1; i < partes.length; i++) {
                        generarFilaVisual(partes[i]);
                    }
                    break;
                }
            }

            if (!encontrado && lblAula != null) {
                lblAula.setText("Aula no encontrada");
            }

        } catch (IOException e) {
            if (lblAula != null) lblAula.setText("Error de archivo");
            e.printStackTrace();
        }
    }

    private void generarFilaVisual(String datosDia) {
        String[] subPartes = datosDia.split(",");
        if (subPartes.length < 2) return;

        // Fila principal del día
        HBox filaDia = new HBox(15);
        filaDia.setAlignment(Pos.CENTER_LEFT);
        filaDia.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        Label lblDia = new Label(subPartes[0]);
        lblDia.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0a1f44; -fx-min-width: 80;");

        HBox contenedoresHoras = new HBox(10);
        for (int i = 1; i < subPartes.length; i++) {
            Label lblHora = new Label(subPartes[i]);
            lblHora.setStyle("-fx-background-color: #e8f0fe; -fx-padding: 5 12; -fx-background-radius: 8; -fx-text-fill: #123a7a; -fx-font-weight: bold;");
            contenedoresHoras.getChildren().add(lblHora);
        }

        filaDia.getChildren().addAll(lblDia, contenedoresHoras);
        contenedorHorarios.getChildren().add(filaDia);
    }

    @FXML
    private void volverMenu(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("main-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 800));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
