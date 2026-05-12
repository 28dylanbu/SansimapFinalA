package com.example.sansimap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.Alert;

public class RegisterController {

    @FXML private PasswordField passOculto1, passOculto2;
    @FXML private TextField passVisible1, passVisible2;
    @FXML private Label ojo1, ojo2;
    @FXML private TextField campoNombre;
    @FXML private TextField campoCorreo;

    @FXML private Label labelLimiteNombre;
    @FXML private Label labelLimiteCorreo;
    @FXML private Label labelLimitePass;


    private boolean ojo1Activo = false;
    private boolean ojo2Activo = false; // False != false;

    @FXML
    public void initialize() {
        // Sincronizar campos de contraseña
        passVisible1.textProperty().bindBidirectional(passOculto1.textProperty());
        passVisible2.textProperty().bindBidirectional(passOculto2.textProperty());

        // Configuramos los límites para cada campo (Campo, Label, Límite)
        configurarLimiteDinamico(campoNombre, labelLimiteNombre, 30);
        configurarLimiteDinamico(campoCorreo, labelLimiteCorreo, 40);

        // Para la contraseña, aplicamos el límite al campo oculto
        configurarLimiteDinamico(passOculto1, labelLimitePass, 20);

        // Si tienes la función de mostrar contraseña (el ojo), debes asegurar
        // que el campo visible también tenga el límite para que no se desincronicen
        if (passVisible1 != null) {
            configurarLimiteDinamico(passVisible1, labelLimitePass, 20);
        }
    }

    private void configurarLimiteDinamico(TextField campoTexto, Label etiqueta, int limiteMaximo) {
        // Le añadimos un "escuchador" que se activa cada vez que el texto cambia (al teclear o borrar)
        campoTexto.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String textoAntiguo, String textoNuevo) {

                // Si el usuario superó el límite, bloqueamos la escritura regresando al texto antiguo
                if (textoNuevo.length() > limiteMaximo) {
                    campoTexto.setText(textoAntiguo);
                } else {
                    // Si está dentro del límite, actualizamos los números del texto
                    etiqueta.setText(textoNuevo.length() + "/" + limiteMaximo + " caracteres permitidos");

                    // Efecto visual pro: Si llega al límite, el texto se pone rojo
                    if (textoNuevo.length() == limiteMaximo) {
                        etiqueta.setStyle("-fx-font-size: 10; -fx-text-fill: #e3000f; -fx-padding: -10 0 0 15;");
                    } else {
                        // Si borra letras, vuelve a ser gris
                        etiqueta.setStyle("-fx-font-size: 10; -fx-text-fill: #999999; -fx-padding: -10 0 0 15;");
                    }
                }
            }
        });
    }
    @FXML
    public void alternarPass1(MouseEvent event) {
        ojo1Activo = !ojo1Activo;
        passVisible1.setVisible(ojo1Activo);
        passOculto1.setVisible(!ojo1Activo);
        ojo1.setStyle("-fx-font-size: 16; -fx-text-fill: " + (ojo1Activo ? "#e3000f;" : "#808080;"));
    }

    @FXML
    public void alternarPass2(MouseEvent event) {
        ojo2Activo = !ojo2Activo;
        passVisible2.setVisible(ojo2Activo);
        passOculto2.setVisible(!ojo2Activo);
        ojo2.setStyle("-fx-font-size: 16; -fx-text-fill: " + (ojo2Activo ? "#e3000f;" : "#808080;"));
    }

    @FXML
    public void volverAlLogin(MouseEvent event) throws IOException {
        // Cargamos la vista de login de vuelta
        CambiarVista.cambiarContenidoVista(event, "hello-view.fxml");
    }
    @FXML
    public void irAVerificacionCorreo(javafx.event.ActionEvent event) throws IOException {
        // 1. Extraer el texto que el usuario escribió
        String nombre = campoNombre.getText();
        String correo = campoCorreo.getText();
        String pass = passOculto1.getText();
        String pass2 = passOculto2.getText();

        // 2. Validar que sean iguales las contraseñas.

        if(!pass.equals(pass2)){

            String titulo, texto;
            titulo="Invalid Password";
            texto="Por favor, verifique su password.";
            mostrarAlerta(titulo, texto, Alert.AlertType.WARNING);
        }
        //De otra manera no hace nada.
        else {
            if (nombre != null && !nombre.trim().isEmpty() &&
                    correo != null && !correo.trim().isEmpty() &&
                    pass != null && !pass.trim().isEmpty()) {


                //Solución al problema de vulnerabilidad
                if (nombre.contains("|") || correo.contains("|") || pass.contains("|")) {

                    String titulo, texto;
                    titulo="Caracteres inválidos";
                    texto="Usar el símbolo '|' o '/' no esta permitido en ningún campo. Por favor, elimínalo.";
                    mostrarAlerta(titulo, texto, Alert.AlertType.ERROR);
                    return;
                }
                //ManejadorUsuarios.guardarUsuario(nombre, correo, pass);
                if (ManejadorUsuarios.usuarioYaExiste(nombre, correo)) {

                    String titulo, texto;
                    titulo="Usuario no disponible";
                    texto="El nombre de usuario o el correo electrónico ya están registrados en Sansi Map. Por favor, intenta con otros datos.";
                    mostrarAlerta(titulo, texto, Alert.AlertType.ERROR);
                    return;
                }

                // ENVIAMOS EL CORREO
                ServicioCorreo.setCodigoCorreo(true);
                boolean enviado = ServicioCorreo.enviarCorreoVerificacion(correo);
                if (enviado) {
                    //  Si se envió bien, cambiamos a la pantalla de verificaciónDeCorreo
                    //  con los datos de usuario, correo y password
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("email-verification-view.fxml"));
                    Parent root = loader.load();

                    //Envio de datos.
                    EmailVerificationController controladorDestino = loader.getController();
                    controladorDestino.recibirDatosDeRegistro(nombre, correo, pass );

                    Scene escenaActual = ((Node) event.getSource()).getScene();
                    escenaActual.setRoot(root);
                } else {
                    // Mostrar alerta de que no se pudo enviar el correo

                    String titulo, texto;
                    titulo="Error de conexión";
                    texto="No pudimos enviar el código a tu correo. Revisa tu conexión o intenta con otro correo.";
                    mostrarAlerta(titulo, texto, Alert.AlertType.ERROR);
                }
                ServicioCorreo.setCodigoCorreo(false);
            } else {

                String titulo, texto;
                titulo="Datos incompletos";
                texto="Por favor, llena todos los campos para poder registrarte.";
                mostrarAlerta(titulo, texto, Alert.AlertType.WARNING);
            }
        }
    }
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}