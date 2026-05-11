package com.example.sansimap; // Paquete principal del proyecto SansiMap

// Importaciones necesarias de JavaFX
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

// Controlador encargado de cambiar la contraseña del usuario
public class ResetPasswordController {

    // Campos de contraseña ocultos
    @FXML
    private PasswordField passOculto1, passOculto2;

    // Campos de contraseña visibles
    @FXML
    private TextField passVisible1, passVisible2;

    // Íconos de ojo para mostrar u ocultar contraseña
    @FXML
    private Label ojo1, ojo2;

    // Guarda el correo del usuario actual
    private String emailUsuario;

    // Estados de visibilidad de cada contraseña
    private boolean ojo1Activo = false;
    private boolean ojo2Activo = false;

    // Método que se ejecuta al cargar la ventana
    @FXML
    public void initialize() {

        // Sincroniza texto entre campos visibles y ocultos
        passVisible1.textProperty()
                .bindBidirectional(passOculto1.textProperty());

        passVisible2.textProperty()
                .bindBidirectional(passOculto2.textProperty());
    }

    // Muestra u oculta la primera contraseña
    @FXML
    public void alternarPass1(MouseEvent event) {

        ojo1Activo = !ojo1Activo;

        // Cambia visibilidad de campos
        passVisible1.setVisible(ojo1Activo);

        passOculto1.setVisible(!ojo1Activo);

        // Cambia color del icono
        ojo1.setStyle(
                "-fx-font-size: 16; -fx-text-fill: "
                        + (ojo1Activo ? "#e3000f;" : "#808080;")
        );
    }

    // Muestra u oculta la segunda contraseña
    @FXML
    public void alternarPass2(MouseEvent event) {

        ojo2Activo = !ojo2Activo;

        // Cambia visibilidad de campos
        passVisible2.setVisible(ojo2Activo);

        passOculto2.setVisible(!ojo2Activo);

        // Cambia color del icono
        ojo2.setStyle(
                "-fx-font-size: 16; -fx-text-fill: "
                        + (ojo2Activo ? "#e3000f;" : "#808080;")
        );
    }

    // Regresa a la pantalla de inicio de sesión
    @FXML
    public void volverAlLogin(MouseEvent event)
            throws IOException {

        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource("hello-view.fxml")
                );

        Parent root = loader.load();

        // Mantiene la misma ventana y tamaño
        Scene escenaActual =
                ((Node) event.getSource()).getScene();

        escenaActual.setRoot(root);
    }

    // Recibe el correo del usuario
    public void recibirEmail(String email) {

        this.emailUsuario = email;
    }

    // Cambia la contraseña del usuario
    @FXML
    public void corregirContrasena(
            javafx.event.ActionEvent event
    ) throws IOException {

        // Obtiene contraseñas ingresadas
        String p1 = passOculto1.getText();

        String p2 = passOculto2.getText();

        // Verifica campos vacíos
        if (p1.isEmpty() || p2.isEmpty()) {

            mostrarAlerta(
                    "Campos vacíos",
                    "Por favor ingresa la nueva contraseña."
            );

            return;
        }

        // Verifica coincidencia de contraseñas
        if (!p1.equals(p2)) {

            mostrarAlerta(
                    "Error",
                    "Las contraseñas no coinciden."
            );

            return;
        }

        // Actualiza contraseña en archivo
        if (ManejadorUsuarios.actualizarPassword(
                emailUsuario,
                p1
        )) {

            // Mensaje de éxito
            Alert exito =
                    new Alert(Alert.AlertType.INFORMATION);

            exito.setTitle("¡Éxito!");

            exito.setHeaderText(null);

            exito.setContentText(
                    "Tu contraseña ha sido actualizada correctamente."
            );

            exito.showAndWait();

            // Regresa al login
            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource("hello-view.fxml")
                    );

            Parent root = loader.load();

            Scene escenaActual =
                    ((Node) event.getSource()).getScene();

            escenaActual.setRoot(root);

        } else {

            // Error al actualizar contraseña
            mostrarAlerta(
                    "Error",
                    "No se pudo actualizar la contraseña."
            );
        }
    }

    // Muestra alertas reutilizables
    private void mostrarAlerta(
            String titulo,
            String mensaje
    ) {

        Alert alerta =
                new Alert(Alert.AlertType.WARNING);

        alerta.setTitle(titulo);

        alerta.setHeaderText(null);

        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}