package com.example.sansimap; // Paquete principal del proyecto SansiMap

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

// Controlador encargado de verificar el código para recuperar contraseña
public class ForgotPasswordCodeController {

    @FXML private TextField box1, box2, box3, box4, box5, box6; // Campos del código de verificación

    @FXML private static String emailField; // Correo temporal del usuario

    // Método que se ejecuta automáticamente al cargar la vista
    @FXML
    public void initialize() {

        // Configura el comportamiento de los cuadros del código
        configurarCuadrosDeCodigo(box1, box2, box3, box4, box5, box6);
    }

    // Verifica si el código ingresado es correcto
    public void verificarCodigoIngresado(javafx.event.ActionEvent event) throws IOException {

        // Une todos los dígitos ingresados
        String codigoIngresado =
                box1.getText() +
                        box2.getText() +
                        box3.getText() +
                        box4.getText() +
                        box5.getText() +
                        box6.getText();

        // Valida el código
        int resultadoValidacion = ServicioCorreo.validarCodigo(codigoIngresado);

        // 1 = correcto | -1 = expirado | 0 = incorrecto
        if (resultadoValidacion == 1) {

            // Carga la pantalla para cambiar contraseña
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("reset-password-view.fxml")
            );

            Parent root = loader.load();

            // Envía el correo al siguiente controller
            ResetPasswordController controladorDestino =
                    loader.getController();

            controladorDestino.recibirEmail(emailField);

            // Cambia la vista actual sin cambiar tamaño de ventana
            Scene escenaActual =
                    ((Node) event.getSource()).getScene();

            escenaActual.setRoot(root);

        } else if (resultadoValidacion == -1) {

            // Alerta de código expirado
            Alert alerta = new Alert(Alert.AlertType.WARNING);

            alerta.setTitle("Código Expirado");
            alerta.setHeaderText(null);

            alerta.setContentText(
                    "Han pasado más de 5 minutos. El código ha expirado."
            );

            alerta.showAndWait();

        } else {

            // Alerta de código incorrecto
            Alert alerta = new Alert(Alert.AlertType.ERROR);

            alerta.setTitle("Código incorrecto");
            alerta.setHeaderText(null);

            alerta.setContentText(
                    "Verifique que el código ingresado sea correcto."
            );

            alerta.showAndWait();
        }
    }

    // Configura los cuadros para aceptar solo números
    private void configurarCuadrosDeCodigo(TextField... cuadros) {

        for (int i = 0; i < cuadros.length; i++) {

            final int index = i;

            TextField cuadro = cuadros[i];

            // Permite solo un número por cuadro
            cuadro.setTextFormatter(new TextFormatter<>(change -> {

                if (change.getText().matches("[0-9]*")
                        && change.getControlNewText().length() <= 1) {

                    return change;
                }

                return null;
            }));

            // Cambia automáticamente al siguiente cuadro
            cuadro.textProperty().addListener(
                    (observable, oldValue, newValue) -> {

                        if (newValue.length() == 1
                                && index < cuadros.length - 1) {

                            cuadros[index + 1].requestFocus();
                        }
                    }
            );

            // Retrocede con Backspace
            cuadro.setOnKeyPressed(event -> {

                if (event.getCode() == KeyCode.BACK_SPACE
                        && cuadro.getText().isEmpty()
                        && index > 0) {

                    cuadros[index - 1].requestFocus();
                }
            });
        }
    }

    // Regresa a la pantalla para corregir correo
    @FXML
    public void corregirCorreo(MouseEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("forgot-password-view.fxml")
        );

        Parent root = loader.load();

        Scene escenaActual =
                ((Node) event.getSource()).getScene();

        escenaActual.setRoot(root);
    }

    // Regresa al login principal
    @FXML
    public void volverAlLogin(MouseEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("hello-view.fxml")
        );

        Parent root = loader.load();

        Scene escenaActual =
                ((Node) event.getSource()).getScene();

        escenaActual.setRoot(root);
    }

    // Recibe el correo enviado desde el controller anterior
    public void recibirEmail(String email) {

        this.emailField = email;
    }
}