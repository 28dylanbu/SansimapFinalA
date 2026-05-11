package com.example.sansimap;// Paquete principal del proyecto SansiMap

import javafx.fxml.FXML; // Permite conectar elementos FXML con Java
import javafx.scene.control.Alert; // Permite mostrar ventanas de alerta
import javafx.scene.control.Label; // Permite usar etiquetas de texto
import javafx.scene.control.PasswordField; // Campo de contraseña oculto
import javafx.scene.control.TextField; // Campo de texto normal
import javafx.scene.input.MouseEvent; // Detecta eventos del mouse
import javafx.fxml.FXMLLoader; // Permite cargar archivos FXML
import javafx.scene.Parent; // Nodo raíz de la interfaz
import javafx.scene.Scene; // Representa la escena actual
import javafx.scene.Node; // Nodo base de JavaFX
import java.io.IOException; // Manejo de errores de carga

// Controlador encargado del inicio de sesión y navegación principal
public class HelloController {

    @FXML private PasswordField campoPasswordOculto; // Campo de contraseña oculto

    @FXML private TextField campoPasswordVisible; // Campo de contraseña visible

    @FXML private Label iconoOjo; // Ícono para mostrar u ocultar contraseña

    @FXML private TextField campoUsuario; // Campo de usuario o correo

    private boolean passwordVisible = false; // Estado de visibilidad de contraseña

    // Método que se ejecuta automáticamente al cargar la vista
    @FXML
    public void initialize() {

        // Sincroniza ambos campos de contraseña
        campoPasswordVisible.textProperty()
                .bindBidirectional(
                        campoPasswordOculto.textProperty()
                );

        // Oculta inicialmente el campo visible
        campoPasswordVisible.setVisible(false);
    }

    // Cambia entre mostrar u ocultar la contraseña
    @FXML
    public void alternarVistaPassword(MouseEvent event) {

        passwordVisible = !passwordVisible;

        // Muestra la contraseña
        if (passwordVisible) {

            campoPasswordVisible.setVisible(true);

            campoPasswordOculto.setVisible(false);

            iconoOjo.setStyle(
                    "-fx-font-size: 16; -fx-text-fill: #e3000f;"
            );

        } else {

            // Oculta la contraseña
            campoPasswordVisible.setVisible(false);

            campoPasswordOculto.setVisible(true);

            iconoOjo.setStyle(
                    "-fx-font-size: 16; -fx-text-fill: #808080;"
            );
        }
    }

    // Abre la ventana de registro
    @FXML
    public void irARegistro(MouseEvent event) throws IOException {

        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource("register-view.fxml")
                );

        Parent root = loader.load();

        Scene escenaActual =
                ((Node) (event.getSource())).getScene();

        escenaActual.setRoot(root);
    }

    // Abre la ventana de recuperación de contraseña
    @FXML
    public void irAForgotPassword(MouseEvent event) throws IOException {

        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource(
                                "forgot-password-view.fxml"
                        )
                );

        Parent root = loader.load();

        Scene escenaActual =
                ((Node) (event.getSource())).getScene();

        escenaActual.setRoot(root);
    }

    // Verifica las credenciales e inicia sesión
    @FXML
    public void iniciarSesion(javafx.event.ActionEvent event) {

        String usuario = campoUsuario.getText();

        String pass = campoPasswordOculto.getText();

        // Verifica que los campos no estén vacíos
        if (usuario == null || usuario.trim().isEmpty()
                || pass == null || pass.trim().isEmpty()) {

            Alert alerta =
                    new Alert(Alert.AlertType.WARNING);

            alerta.setTitle("Campos incompletos");

            alerta.setHeaderText(null);

            alerta.setContentText(
                    "Por favor, ingresa tu usuario y contraseña."
            );

            alerta.showAndWait();

            return;
        }

        // Valida las credenciales
        if (ManejadorUsuarios.validarCredenciales(usuario, pass)) {

            // Alerta de inicio exitoso
            Alert alerta =
                    new Alert(Alert.AlertType.INFORMATION);

            alerta.setTitle("¡Bienvenido!");

            alerta.setHeaderText(null);

            alerta.setContentText(
                    "Inicio de sesión exitoso en SansiMap."
            );

            alerta.showAndWait();

            // Abre el menú principal
            try {

                FXMLLoader loader =
                        new FXMLLoader(
                                getClass().getResource(
                                        "main-view.fxml"
                                )
                        );

                Parent root = loader.load();

                Scene escenaActual =
                        ((Node) event.getSource()).getScene();

                escenaActual.setRoot(root);

            } catch (Exception e) {

                e.printStackTrace();
            }

        } else {

            // Alerta de error de inicio
            Alert alerta =
                    new Alert(Alert.AlertType.ERROR);

            alerta.setTitle("Error de inicio de sesión");

            alerta.setHeaderText(null);

            alerta.setContentText(
                    "Usuario o contraseña incorrectos."
            );

            alerta.showAndWait();

            // Limpia la contraseña
            campoPasswordOculto.clear();

            campoPasswordVisible.clear();
        }
    }
}