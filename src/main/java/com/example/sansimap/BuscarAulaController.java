package com.example.sansimap;

// Importaciones necesarias de JavaFX
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

// Controlador de la ventana "Buscar Aula"
// Esta clase permite:
// - Buscar un aula ingresada por el usuario
// - Enviar el dato a otra ventana
// - Volver al menú principal
public class BuscarAulaController {

    // Campo de texto donde el usuario escribe el aula
    @FXML
    private TextField inputAula;

    // Método que se ejecuta al presionar el botón buscar
    @FXML
    private void buscarAula(javafx.event.ActionEvent event) {

        // Obtiene el texto ingresado por el usuario
        String aula = inputAula.getText();

        // Validación básica:
        // evita búsquedas vacías
        if (aula == null || aula.isEmpty()) {

            System.out.println("Ingrese un aula");
            return;
        }

        // Clase genérica (CambiarVista.java) para no repetir código de carga de FXML
        CambiarVista.cambiarContenidoVista(event,"resultado-aula.fxml");
    }

    // Método para volver al menú principal
    @FXML
    private void volverMenu() {

        try {

            // Obtiene la ventana actual
            Stage stage = (Stage) inputAula.getScene().getWindow();

            // Guarda el tamaño actual
            double ancho = stage.getWidth();
            double alto = stage.getHeight();

            // Carga el archivo FXML del menú principal
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("main-view.fxml")
            );

            // Carga la interfaz
            Parent root = loader.load();

            // Cambia la escena actual
            stage.setScene(new Scene(root));

            // Mantiene el tamaño anterior
            stage.setWidth(ancho);
            stage.setHeight(alto);

        } catch (Exception e) {

            // Muestra errores en consola
            e.printStackTrace();
        }
    }
}