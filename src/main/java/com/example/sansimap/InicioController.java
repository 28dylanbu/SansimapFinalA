package com.example.sansimap;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class InicioController {


    @FXML private VBox barraLateral;
    @FXML private ImageView mapaPrincipal;

    private boolean menuAbierto = false;

    @FXML
    public void initialize() {
        // Lógica básica para cargar el mapa al iniciar la pantalla
        if (mapaPrincipal != null) {
            try {
                mapaPrincipal.setImage(new Image(getClass().getResource("/com/example/sansimap/MAPA_DE_SANSI.png").toExternalForm()));
            } catch (Exception e) {
                System.out.println("No se pudo cargar la imagen del mapa.");
            }
        }
    }

    @FXML
    private void irBuscarAula(ActionEvent event) throws IOException {
        CambiarVista.cambiarContenidoVista(event, "buscar-aula.fxml");
    }

    @FXML
    protected void cambiarALogin(ActionEvent event) throws IOException {
        CambiarVista.cambiarContenidoVista(event, "hello-view.fxml");
    }

    // --- LÓGICA VISUAL SIMPLIFICADA ---

    @FXML
    void toggleMenu(ActionEvent event) {
        if (barraLateral == null) return;

        // Lógica condicional simple: mover el menú de golpe cambiando su coordenada X
        if (menuAbierto) {
            barraLateral.setTranslateX(-300); // Lo esconde fuera de la pantalla
            menuAbierto = false;
        } else {
            barraLateral.setTranslateX(0); // Lo trae de vuelta a la vista
            menuAbierto = true;
        }
    }

    // Métodos para evitar errores si el FXML los llama al hacer clic
    @FXML void irEdificioPrincipal(ActionEvent event) { System.out.println("Edificio Principal"); }
    @FXML void irLaboratorios(ActionEvent event) { System.out.println("Laboratorios"); }
    @FXML void irAulasTeoricas(ActionEvent event) { System.out.println("Aulas Teóricas"); }
}