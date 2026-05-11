package com.example.sansimap;

// Importaciones necesarias de JavaFX
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Objects;

// Controlador de la ventana "Aulas Disponibles"
// Esta clase se encarga de:
// - Mostrar una imagen de fondo
// - Mostrar aulas disponibles
// - Volver al menú principal
public class AulasDisponiblesController {

    // Imagen de fondo del mapa
    @FXML
    private ImageView fondo;

    // Área de texto donde se muestran las aulas
    @FXML
    private TextArea listaAulas;

    // Método initialize:
    // Se ejecuta automáticamente al cargar el FXML
    @FXML
    public void initialize() {

        // Intenta cargar la imagen del mapa
        try {

            // Carga la imagen desde la carpeta resources
            Image img = new Image(
                    Objects.requireNonNull(
                            getClass().getResource(
                                    "/com/example/sansimap/MAPA_DE_SANSI.png"
                            )
                    ).toExternalForm()
            );

            // Coloca la imagen en el ImageView
            fondo.setImage(img);

        } catch (Exception e) {

            // Mensaje si la imagen no se encuentra
            System.out.println("No se encontró la imagen del mapa.");
        }

        // Muestra las aulas disponibles en el TextArea
        listaAulas.setText(
                """
                Aula 101
                Aula 202
                Aula 305
                Aula 410
                """
        );
    }

    // Método para volver al menú principal
    @FXML
    private void volverMenu() {

        try {

            // Obtiene la ventana actual
            Stage stage = (Stage) listaAulas.getScene().getWindow();

            // Guarda el tamaño actual de la ventana
            double ancho = stage.getWidth();
            double alto = stage.getHeight();

            // Carga el archivo FXML del menú principal
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("main-view.fxml")
            );

            // Cambia la escena actual
            stage.setScene(new Scene(loader.load()));

            // Recupera el tamaño anterior
            // para evitar cambios bruscos de tamaño
            stage.setWidth(ancho);
            stage.setHeight(alto);

        } catch (Exception e) {

            // Muestra errores en consola
            e.printStackTrace();
        }
    }
}