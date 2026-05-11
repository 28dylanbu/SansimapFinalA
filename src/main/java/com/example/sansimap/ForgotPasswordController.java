package com.example.sansimap; // Paquete principal del proyecto SansiMap

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

// Controlador encargado de recuperar la contraseña mediante correo
public class ForgotPasswordController {

    @FXML private TextField emailField; // Campo donde el usuario escribe su correo

    // Regresa a la pantalla de inicio de sesión
    @FXML
    public void volverAlLogin(MouseEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("hello-view.fxml")
        );

        Parent root = loader.load();

        Scene escenaActual =
                ((Node) (event.getSource())).getScene();

        escenaActual.setRoot(root);
    }

    // Envía el código de recuperación al correo ingresado
    @FXML
    public void enviarCodigo(javafx.event.ActionEvent event) throws IOException {

        // Obtiene el correo ingresado
        String correo = emailField.getText();

        // Verifica si el correo existe en el sistema
        if (!ManejadorUsuarios.validarEmail(correo)) {

            // Muestra alerta si el correo no existe
            Alert alerta = new Alert(Alert.AlertType.WARNING);

            alerta.setTitle("Not found email");
            alerta.setHeaderText(null);

            alerta.setContentText(
                    "No se encontró el email."
            );

            alerta.showAndWait();

        } else {

            // Activa el modo de recuperación de contraseña
            ServicioCorreo.setCodigoPassword(true);

            // Envía el correo de verificación
            boolean enviado =
                    ServicioCorreo.enviarCorreoVerificacion(correo);

            // Si el correo fue enviado correctamente
            if (enviado) {

                // Carga la pantalla para ingresar el código
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource(
                                "forgot-password-code-view.fxml"
                        )
                );

                Parent root = loader.load();

                // Envía el correo al siguiente controller
                ForgotPasswordCodeController controladorDestino =
                        loader.getController();

                controladorDestino.recibirEmail(correo);

                // Cambia la vista actual
                Scene escenaActual =
                        ((Node) event.getSource()).getScene();

                escenaActual.setRoot(root);

            } else {

                // Muestra alerta si ocurrió un error al enviar
                Alert alerta = new Alert(Alert.AlertType.ERROR);

                alerta.setTitle("Error de conexión");
                alerta.setHeaderText(null);

                alerta.setContentText(
                        "No pudimos enviar el código a tu correo."
                );

                alerta.showAndWait();
            }

            // Desactiva el modo de código de correo
            ServicioCorreo.setCodigoCorreo(false);
        }
    }
}