package com.example.sansimap; // Paquete principal del proyecto SansiMap

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
        // Obtenemos el texto del campo de búsqueda y quitamos espacios en blanco
        String aulaABuscar = txtBusquedaAula.getText().trim();

        // Validamos que el campo no esté vacío para evitar búsquedas inútiles
        if (aulaABuscar.isEmpty()) {
            lblAula.setText("Por favor, ingrese un aula");
            return;
        }

        // Limpiamos los resultados anteriores antes de mostrar los nuevos
        contenedorHorarios.getChildren().clear();
        buscarEnArchivo(aulaABuscar);
    }

    private void buscarEnArchivo(String codigo) {
        // Abrimos el archivo de texto para leerlo línea por línea
        try (BufferedReader br = new BufferedReader(new FileReader("cursos_horarios.txt"))) {
            String linea;
            boolean encontrado = false;

            while ((linea = br.readLine()) != null) {
                // Separamos los datos usando el punto y coma como delimitador
                String[] partes = linea.split(";");

                // Verificamos si la primera columna coincide con el código del aula buscada
                if (partes.length >= 1 && partes[0].equalsIgnoreCase(codigo)) {
                    encontrado = true;

                    // Si se encuentra, actualizamos el título en la interfaz
                    if (lblAula != null) {
                        lblAula.setText("Horarios - Aula " + partes[0].toUpperCase());
                    }

                    // Iteramos por las demás partes (días y horas) para crear las filas visuales
                    for (int i = 1; i < partes.length; i++) {
                        generarFilaVisual(partes[i]);
                    }
                    break;
                }
            }

            // Avisamos al usuario si después de leer todo el archivo no hubo coincidencias
            if (!encontrado && lblAula != null) {
                lblAula.setText("Aula no encontrada");
            }

        } catch (IOException e) {
            // Manejamos errores en caso de que el archivo no exista o sea inaccesible
            if (lblAula != null) lblAula.setText("Error de archivo");
            e.printStackTrace();
        }
    }

    private void generarFilaVisual(String datosDia) {
        // Cada día tiene sus horas separadas por coma
        String[] subPartes = datosDia.split(",");
        if (subPartes.length < 2) return;

        // Creamos un contenedor horizontal para cada fila de día con su diseño
        HBox filaDia = new HBox(15);
        filaDia.setAlignment(Pos.CENTER_LEFT);
        filaDia.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        // Creamos la etiqueta para el nombre del día
        Label lblDia = new Label(subPartes[0]);
        lblDia.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0a1f44; -fx-min-width: 80;");

        // Creamos un contenedor para todas las horas de ese día específico
        HBox contenedoresHoras = new HBox(10);
        for (int i = 1; i < subPartes.length; i++) {
            Label lblHora = new Label(subPartes[i]);
            // Estilo para cada etiqueta de hora individual
            lblHora.setStyle("-fx-background-color: #e8f0fe; -fx-padding: 5 12; -fx-background-radius: 8; -fx-text-fill: #123a7a; -fx-font-weight: bold;");
            contenedoresHoras.getChildren().add(lblHora);
        }

        // Agregamos todo al contenedor principal de la fila
        filaDia.getChildren().addAll(lblDia, contenedoresHoras);
        contenedorHorarios.getChildren().add(filaDia);
    }

    @FXML
    private void volverMenu(ActionEvent event) {
        try {
            // Cargamos la vista principal para volver al menú
            Parent root = FXMLLoader.load(getClass().getResource("main-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 800));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}