package com.example.sansimap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class HelloController {

    // 1. Conectamos los elementos del FXML con Java
    @FXML private PasswordField campoPasswordOculto;
    @FXML private TextField campoPasswordVisible;
    @FXML private Label iconoOjo;
    @FXML private TextField campoUsuario;

    private boolean passwordVisible = false;

    @FXML
    public void initialize() {
        // 2. MAGIA: Sincronizamos el texto de ambos campos.
        // Si el usuario escribe en el oculto, se copia al visible, y viceversa.
        campoPasswordVisible.textProperty().bindBidirectional(campoPasswordOculto.textProperty());
    }

    // 3. El método que se ejecuta al hacer clic en el ojo
    @FXML
    public void alternarVistaPassword(MouseEvent event) {
        passwordVisible = !passwordVisible; // Cambiamos el estado

        if (passwordVisible) {
            // Mostrar texto, ocultar asteriscos
            campoPasswordVisible.setVisible(true);
            campoPasswordOculto.setVisible(false);
            // Opcional: Cambiar el color del ojo para que se note que está activo (rojo)
            iconoOjo.setStyle("-fx-font-size: 16; -fx-text-fill: #e3000f;");
        } else {
            // Ocultar texto, mostrar asteriscos
            campoPasswordVisible.setVisible(false);
            campoPasswordOculto.setVisible(true);
            // Volver al color gris del ojo
            iconoOjo.setStyle("-fx-font-size: 16; -fx-text-fill: #808080;");
        }
    }
    @FXML
    public void irARegistro(MouseEvent event) throws IOException {
        // 1. Cargar el FXML de registro
        CambiarVista.cambiarContenidoVista(event,"register-view.fxml");

    }
    @FXML
    public void irAForgotPassword(MouseEvent event) throws IOException {
        // 1. Cargar la nueva vista FXML
        CambiarVista.cambiarContenidoVista(event, "forgot-password-view.fxml");
    }

    @FXML
    public void iniciarSesion(ActionEvent event) {
        String usuario = campoUsuario.getText();
        String pass = campoPasswordOculto.getText();

        // 1. Validar que los campos no estén vacíos
        if (usuario == null || usuario.trim().isEmpty() ||
                pass == null || pass.trim().isEmpty()) {

            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Campos incompletos");
            alerta.setHeaderText(null);
            alerta.setContentText("Por favor, ingresa tu usuario/correo y tu contraseña.");
            alerta.showAndWait();
            return; // Detenemos la ejecución aquí
        }

        // 2. Verificar si las credenciales son correctas
        if (ManejadorUsuarios.validarCredenciales(usuario, pass)) {


            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("¡Bienvenido!");
            alerta.setHeaderText(null);
            alerta.setContentText("Inicio de sesión exitoso. ¡Bienvenido de vuelta a Sansi Map!");
            alerta.showAndWait();

            //Abrir Mapa
            CambiarVista.cambiarContenidoVista(event, "main-view.fxml");


        } else {

            // ERROR: Credenciales incorrectas
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error de inicio de sesión");
            alerta.setHeaderText(null);
            alerta.setContentText("El usuario, correo o contraseña son incorrectos. Por favor, inténtalo de nuevo.");
            alerta.showAndWait();

            // Opcional: Borrar el campo de la contraseña para que lo intente de nuevo
            campoPasswordOculto.clear();
            // Si también tienes el campo visible (passVisible), bórralo también:
            // campoPasswordVisible.clear();
        }
    }


}
