package com.example.sansimap;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class InicioController {

    @FXML private ScrollPane scrollPrincipal;
    @FXML private VBox barraLateral, botoneslateral;
    @FXML private ImageView mapaPrincipal;
    @FXML private StackPane iconoAnimado, mapaContenedor, celularContenedor;
    @FXML private Label tituloAnimado, subtituloAnimado;
    @FXML private HBox botonesAnimados, statsAnimados, navegacionContenedor;

    private boolean menuAbierto = false;
    private final Set<Node> nodosAnimados = new HashSet<>();

    @FXML
    public void initialize() {
        // Carga del mapa
        if (mapaPrincipal != null) {
            try {
                mapaPrincipal.setImage(new Image(getClass().getResource("/com/example/sansimap/MAPA_DE_SANSI.png").toExternalForm()));
            } catch (Exception e) {
                System.out.println("No se pudo cargar la imagen del mapa.");
            }
        }

        // Animaciones iniciales
        Platform.runLater(() -> {
            animarEntrada(iconoAnimado, 100);
            animarEntrada(tituloAnimado, 300);
            animarEntrada(subtituloAnimado, 500);
            animarEntrada(botonesAnimados, 700);
            aplicarEfectoHover(botonesAnimados);
            aplicarEfectoHover(botoneslateral);

            if (scrollPrincipal != null) {
                scrollPrincipal.vvalueProperty().addListener((obs, oldVal, newVal) -> verificarVisibilidad(newVal.doubleValue()));
            }
        });
    }

    @FXML
    private void irBuscarAula(ActionEvent event) throws IOException {
        // Clase genérica (CambiarVista.java) para no repetir código de carga de FXML
        CambiarVista.cambiarContenidoVista(event, "buscar-aula.fxml");
    }

    @FXML
    protected void cambiarALogin(ActionEvent event) throws IOException {
        // Clase genérica (CambiarVista.java) para no repetir código de carga de FXML
        CambiarVista.cambiarContenidoVista(event, "hello-view.fxml");
    }

    // Clase genérica (CambiarVista.java) para no repetir código de carga de FXML


    // --- LÓGICA VISUAL ---

    @FXML
    void toggleMenu(ActionEvent event) {
        if (barraLateral == null) return;
        TranslateTransition transicion = new TranslateTransition(Duration.millis(350), barraLateral);
        if (menuAbierto) {
            transicion.setToX(-300);
            menuAbierto = false;
        } else {
            transicion.setToX(0);
            menuAbierto = true;
        }
        transicion.play();
    }

    private void aplicarEfectoHover(Pane contenedor) {
        if (contenedor == null) return;
        for (Node nodo : contenedor.getChildren()) {
            if (nodo instanceof Button btn) {
                DropShadow brillo = new DropShadow(20, Color.WHITE);
                btn.setOnMouseEntered(e -> { btn.setEffect(brillo); btn.setScaleX(1.05); btn.setScaleY(1.05); });
                btn.setOnMouseExited(e -> { btn.setEffect(null); btn.setScaleX(1.0); btn.setScaleY(1.0); });
            }
        }
    }

    private void animarEntrada(Node nodo, int delayMillis) {
        if (nodo == null || nodosAnimados.contains(nodo)) return;
        nodosAnimados.add(nodo);
        nodo.setOpacity(0);
        nodo.setTranslateY(40);
        FadeTransition fade = new FadeTransition(Duration.millis(800), nodo);
        fade.setToValue(1);
        fade.setDelay(Duration.millis(delayMillis));
        TranslateTransition translate = new TranslateTransition(Duration.millis(800), nodo);
        translate.setToY(0);
        translate.setDelay(Duration.millis(delayMillis));
        fade.play(); translate.play();
    }

    private void verificarVisibilidad(double scrollPos) {
        if (scrollPos > 0.15) animarEntrada(mapaContenedor, 0);
        if (scrollPos > 0.45) animarEntrada(celularContenedor, 0);
        if (scrollPos > 0.70) animarEntrada(navegacionContenedor, 0);
    }

    // Métodos para evitar errores si el FXML los llama
    @FXML void irEdificioPrincipal(ActionEvent event) { System.out.println("Edificio Principal"); }
    @FXML void irLaboratorios(ActionEvent event) { System.out.println("Laboratorios"); }
    @FXML void irAulasTeoricas(ActionEvent event) { System.out.println("Aulas Teóricas"); }
}