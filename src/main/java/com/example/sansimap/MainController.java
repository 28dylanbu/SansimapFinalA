package com.example.sansimap; // Paquete principal del proyecto SansiMap

// Importaciones de animaciones JavaFX
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;

// Importaciones principales de JavaFX
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

// Controles JavaFX
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

// Efectos visuales
import javafx.scene.effect.DropShadow;

// Imágenes
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// Layouts
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

// Colores y ventana
import javafx.scene.paint.Color;
import javafx.stage.Stage;

// Tiempo de animaciones
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

// Controlador principal del menú y pantalla principal
public class MainController {

    // Scroll principal de la ventana
    @FXML
    private ScrollPane scrollPrincipal;

    // Barra lateral y contenedor de botones
    @FXML
    private VBox barraLateral, botoneslateral;

    // Imagen principal del mapa
    @FXML
    private ImageView mapaPrincipal;

    // Contenedores animados
    @FXML
    private StackPane iconoAnimado,
            mapaContenedor,
            celularContenedor;

    // Textos animados
    @FXML
    private Label tituloAnimado,
            subtituloAnimado,
            textoInferiorAnimado;

    // Contenedores de botones y navegación
    @FXML
    private HBox botonesAnimados,
            statsAnimados,
            navegacionContenedor;

    // Estado del menú lateral
    private boolean menuAbierto = false;

    // Guarda nodos ya animados para evitar repetir animaciones
    private final Set<Node> nodosAnimados =
            new HashSet<>();

    // Método que se ejecuta al cargar la ventana
    @FXML
    public void initialize() {

        // Carga imagen del mapa principal
        if (mapaPrincipal != null) {

            try {

                mapaPrincipal.setImage(
                        new Image(
                                getClass()
                                        .getResource(
                                                "/com/example/sansimap/MAPA_DE_SANSI.png"
                                        )
                                        .toExternalForm()
                        )
                );

            } catch (Exception e) {

                System.out.println(
                        "No se pudo cargar la imagen del mapa."
                );
            }
        }

        // Ejecuta animaciones después de cargar la interfaz
        Platform.runLater(() -> {

            // Animaciones iniciales
            animarEntrada(iconoAnimado, 100);

            animarEntrada(tituloAnimado, 300);

            animarEntrada(subtituloAnimado, 500);

            animarEntrada(botonesAnimados, 700);

            // Efectos hover
            aplicarEfectoHover(botonesAnimados);

            aplicarEfectoHover(botoneslateral);

            // Detecta movimiento del scroll
            if (scrollPrincipal != null) {

                scrollPrincipal.vvalueProperty().addListener(
                        (obs, oldVal, newVal) ->
                                verificarVisibilidad(
                                        newVal.doubleValue()
                                )
                );
            }
        });
    }

    // Abre la ventana de aulas disponibles
    @FXML
    private void irAulasDisponibles(
            ActionEvent event
    ) {

        cambiarEscena(event, "horario-view.fxml");
    }

    // Abre la ventana de búsqueda de aulas
    @FXML
    private void irBuscarAula(
            ActionEvent event
    ) {

        cambiarEscena(event, "buscar-aula.fxml");
    }

    // Regresa a la ventana de login
    @FXML
    protected void cambiarALogin(
            ActionEvent event
    ) {

        cambiarEscena(event, "hello-view.fxml");
    }

    // Método reutilizable para cambiar escenas
    private void cambiarEscena(
            ActionEvent event,
            String fxml
    ) {

        try {

            // Carga el archivo FXML
            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(fxml)
                    );

            Parent root = loader.load();

            // Obtiene la ventana actual
            Stage stage =
                    (Stage)
                            ((Node) event.getSource())
                                    .getScene()
                                    .getWindow();

            // ===== CORRECCIÓN IMPORTANTE =====
            // Mantiene el tamaño actual de la ventana
            double anchoActual =
                    stage.getWidth();

            double altoActual =
                    stage.getHeight();

            // Crea nueva escena manteniendo tamaño
            Scene scene =
                    new Scene(
                            root,
                            anchoActual,
                            altoActual
                    );

            // Cambia escena
            stage.setScene(scene);

            // Permite redimensionar
            stage.setResizable(true);

            // Actualiza ventana
            stage.show();

        } catch (IOException e) {

            System.err.println(
                    "Error al cargar: " + fxml
            );

            e.printStackTrace();
        }
    }

    // Abre o cierra el menú lateral
    @FXML
    void toggleMenu(ActionEvent event) {

        if (barraLateral == null) {

            return;
        }

        // Animación de movimiento
        TranslateTransition transicion =
                new TranslateTransition(
                        Duration.millis(350),
                        barraLateral
                );

        // Cierra menú
        if (menuAbierto) {

            transicion.setToX(-300);

            menuAbierto = false;

        }

        // Abre menú
        else {

            transicion.setToX(0);

            menuAbierto = true;
        }

        transicion.play();
    }

    // Agrega efecto hover a botones
    private void aplicarEfectoHover(
            Pane contenedor
    ) {

        if (contenedor == null) {

            return;
        }

        // Recorre nodos del contenedor
        for (Node nodo : contenedor.getChildren()) {

            // Verifica si es botón
            if (nodo instanceof Button btn) {

                DropShadow brillo =
                        new DropShadow(
                                20,
                                Color.WHITE
                        );

                // Efecto al entrar mouse
                btn.setOnMouseEntered(e -> {

                    btn.setEffect(brillo);

                    btn.setScaleX(1.05);

                    btn.setScaleY(1.05);
                });

                // Efecto al salir mouse
                btn.setOnMouseExited(e -> {

                    btn.setEffect(null);

                    btn.setScaleX(1.0);

                    btn.setScaleY(1.0);
                });
            }
        }
    }

    // Animación de entrada suave
    private void animarEntrada(
            Node nodo,
            int delayMillis
    ) {

        // Evita repetir animaciones
        if (nodo == null
                || nodosAnimados.contains(nodo)) {

            return;
        }

        nodosAnimados.add(nodo);

        // Estado inicial
        nodo.setOpacity(0);

        nodo.setTranslateY(40);

        // Animación de opacidad
        FadeTransition fade =
                new FadeTransition(
                        Duration.millis(800),
                        nodo
                );

        fade.setToValue(1);

        fade.setDelay(
                Duration.millis(delayMillis)
        );

        // Animación de movimiento
        TranslateTransition translate =
                new TranslateTransition(
                        Duration.millis(800),
                        nodo
                );

        translate.setToY(0);

        translate.setDelay(
                Duration.millis(delayMillis)
        );

        // Inicia animaciones
        fade.play();

        translate.play();
    }

    // Activa animaciones según el scroll
    private void verificarVisibilidad(
            double scrollPos
    ) {

        if (scrollPos > 0.15) {

            animarEntrada(
                    mapaContenedor,
                    0
            );
        }

        if (scrollPos > 0.45) {

            animarEntrada(
                    celularContenedor,
                    0
            );
        }

        if (scrollPos > 0.70) {

            animarEntrada(
                    navegacionContenedor,
                    0
            );
        }
    }

    // Métodos temporales para botones futuros
    @FXML
    void irEdificioPrincipal(ActionEvent event) {

        System.out.println(
                "Edificio Principal"
        );
    }

    @FXML
    void irLaboratorios(ActionEvent event) {

        System.out.println(
                "Laboratorios"
        );
    }

    @FXML
    void irAulasTeoricas(ActionEvent event) {

        System.out.println(
                "Aulas Teóricas"
        );
    }
}