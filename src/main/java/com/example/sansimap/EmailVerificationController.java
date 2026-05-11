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
import javafx.stage.Stage;

import java.io.IOException;

// Controlador encargado de verificar el código enviado al correo del usuario
public class EmailVerificationController {

    @FXML private TextField box1, box2, box3, box4, box5, box6; // Campos donde se ingresa el código de verificación

    // Variables temporales para almacenar los datos del registro
    private String usuarioTemporal;
    private String correoTemporal;
    private String passwordTemporal;

    // Recibe los datos enviados desde RegisterController
    public void recibirDatosDeRegistro(String nombre, String correo, String pass) {

        usuarioTemporal = nombre;
        correoTemporal = correo;
        passwordTemporal = pass;
    }

    // Método que se ejecuta automáticamente al cargar la vista
    @FXML
    public void initialize() {

        // Configura el comportamiento de los cuadros del código
        configurarCuadrosDeCodigo(box1, box2, box3, box4, box5, box6);
    }

    // Verifica si el código ingresado es correcto
    public void verificarCodigoIngresado(javafx.event.ActionEvent event) throws IOException {

        // Une los 6 dígitos ingresados
        String codigoIngresado =
                box1.getText() +
                        box2.getText() +
                        box3.getText() +
                        box4.getText() +
                        box5.getText() +
                        box6.getText();

        // Valida el código usando ServicioCorreo
        int resultadoValidacion = ServicioCorreo.validarCodigo(codigoIngresado);

        // 1 = correcto | -1 = expirado | 0 = incorrecto
        if (resultadoValidacion == 1) {

            // Guarda el usuario registrado
            ManejadorUsuarios.guardarUsuario(
                    usuarioTemporal,
                    correoTemporal,
                    passwordTemporal
            );

            // Muestra alerta de éxito
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("¡Registro Exitoso!");
            alerta.setHeaderText(null);
            alerta.setContentText("El registro se a completado con exito");
            alerta.showAndWait();

            // Redirige al login
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("hello-view.fxml")
            );

            Parent root = loader.load();

            Scene escenaActual =
                    ((Node) (event.getSource())).getScene();

            escenaActual.setRoot(root);

        } else if (resultadoValidacion == -1) {

            // Alerta de código expirado
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Código Expirado");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "Han pasado más de 5 minutos. Por seguridad, el código ha expirado."
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

    // Configura los cuadros del código para aceptar solo números
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

            // Retrocede al cuadro anterior con Backspace
            cuadro.setOnKeyPressed(event -> {

                if (event.getCode() == KeyCode.BACK_SPACE
                        && cuadro.getText().isEmpty()
                        && index > 0) {

                    cuadros[index - 1].requestFocus();
                }
            });
        }
    }

    // Regresa a la pantalla de login
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
}
