package com.example.sansimap;

// Importaciones para las animaciones visuales (movimiento y desvanecimiento)
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;

// Importaciones principales del sistema JavaFX
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

// Controles de la interfaz de usuario (botones, textos, etc.)
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

// Efectos visuales para los elementos (sombras)
import javafx.scene.effect.DropShadow;

// Importaciones para manejar y mostrar imágenes
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// Contenedores para organizar los elementos en la pantalla
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

// Manejo de colores y la ventana principal (Stage)
import javafx.scene.paint.Color;
import javafx.stage.Stage;

// Control del tiempo para las animaciones
import javafx.util.Duration;

// Herramientas de Java
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

// Controlador principal de la aplicación SANSI MAP
// Esta clase se encarga de:
// - Controlar el menú lateral deslizable
// - Administrar las animaciones al mover el scroll (bajar por la página)
// - Navegar entre las diferentes pantallas del sistema
public class MainController {

    // Barra de desplazamiento principal de toda la página
    @FXML
    private ScrollPane scrollPrincipal;

    // Contenedores del menú lateral izquierdo
    @FXML
    private VBox barraLateral, botoneslateral;

    // Imagen donde se mostrará el mapa de la facultad
    @FXML
    private ImageView mapaPrincipal;

    // Paneles que aparecerán con animaciones
    @FXML
    private StackPane iconoAnimado, mapaContenedor, celularContenedor;

    // Textos que aparecerán con animaciones
    @FXML
    private Label tituloAnimado, subtituloAnimado, textoInferiorAnimado;

    // Grupos de elementos (botones e info) que tendrán animaciones
    @FXML
    private HBox botonesAnimados, statsAnimados, navegacionContenedor;

    // Variable para saber si el menú lateral está visible (true) o escondido (false)
    private boolean menuAbierto = false;

    // Lista para recordar qué elementos ya se animaron
    // (Sirve para que la animación no se repita cada vez que subimos y bajamos)
    private final Set<Node> nodosAnimados = new HashSet<>();

    // Método initialize:
    // Se ejecuta automáticamente al cargar el FXML de esta ventana
    @FXML
    public void initialize() {

        // 1. Intenta cargar la imagen principal del mapa
        if (mapaPrincipal != null) {
            try {
                // Busca la ruta de la imagen en la carpeta resources
                mapaPrincipal.setImage(
                        new Image(
                                getClass().getResource("/com/example/sansimap/MAPA_DE_SANSI.png").toExternalForm()
                        )
                );
            } catch (Exception e) {
                // Mensaje si la imagen no se encuentra
                System.out.println("No se pudo cargar la imagen del mapa.");
            }
        }

        // 2. Prepara las animaciones de la página
        // Platform.runLater asegura que esto se ejecute DESPUÉS de que la ventana cargue por completo
        Platform.runLater(() -> {

            // Ejecuta las animaciones de entrada de la parte superior (con retrasos en milisegundos)
            animarEntrada(iconoAnimado, 100);
            animarEntrada(tituloAnimado, 300);
            animarEntrada(subtituloAnimado, 500);
            animarEntrada(botonesAnimados, 700);

            // Activa el efecto de iluminación al pasar el ratón por los botones
            aplicarEfectoHover(botonesAnimados);
            aplicarEfectoHover(botoneslateral);

            // 3. Detecta cuando el usuario baja o sube por la página (Scroll)
            if (scrollPrincipal != null) {
                scrollPrincipal.vvalueProperty().addListener(
                        (obs, oldVal, newVal) ->
                                // Llama a una función que decide qué animar según cuánto bajó el usuario
                                verificarVisibilidad(newVal.doubleValue())
                );
            }
        });
    }

    // Método para ir a la pantalla de "Aulas Disponibles" (Horarios)

    @FXML
    private void irAulasDisponibles(ActionEvent event) {
        cambiarEscena(event, "horario-view.fxml");



    }

    // Método para ir a la pantalla de "Búsqueda de Aulas"
    @FXML
    private void irBuscarAula(ActionEvent event) {
        cambiarEscena(event, "buscar-aula.fxml");

    }

    // Método para regresar a la pantalla de Inicio de Sesión
    @FXML
    protected void cambiarALogin(ActionEvent event) {
        cambiarEscena(event, "hello-view.fxml");

    }

    // Función general y reutilizable para cambiar de ventanas sin perder el tamaño actual
    private void cambiarEscena(ActionEvent event, String fxml) {
        try {
            // Carga el nuevo archivo FXML de la ventana que queremos abrir
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            // Obtiene la ventana (Stage) actual donde ocurrió el click
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Guarda el tamaño actual de la ventana antes del cambio
            double anchoActual = stage.getWidth();
            double altoActual = stage.getHeight();

            // Crea la nueva escena forzando que mantenga el tamaño guardado
            Scene scene = new Scene(root, anchoActual, altoActual);

            // Aplica la nueva escena a la ventana
            stage.setScene(scene);

            // Asegura que el usuario siga pudiendo maximizar/minimizar
            stage.setResizable(true);

            // Muestra el resultado final
            stage.show();

        } catch (IOException e) {
            // Muestra errores en consola si el archivo no existe
            System.err.println("Error al cargar: " + fxml);
            e.printStackTrace();
        }
    }

    // Método para abrir o esconder el menú lateral (efecto cortina)
    @FXML
    void toggleMenu(ActionEvent event) {

        // Si la barra no existe, detiene el proceso
        if (barraLateral == null) {
            return;
        }

        // Prepara la animación de movimiento lateral (dura 350 milisegundos)
        TranslateTransition transicion = new TranslateTransition(Duration.millis(350), barraLateral);

        if (menuAbierto) {
            // Si está abierto: lo empuja hacia la izquierda (-300px) para esconderlo
            transicion.setToX(-300);
            menuAbierto = false;
        } else {
            // Si está cerrado: lo trae a la posición original (0) para mostrarlo
            transicion.setToX(0);
            menuAbierto = true;
        }

        // Ejecuta la animación del menú
        transicion.play();
    }

    // Función para crear un efecto de brillo y crecimiento en los botones (Hover)
    private void aplicarEfectoHover(Pane contenedor) {

        // Si el contenedor está vacío, detiene el proceso
        if (contenedor == null) {
            return;
        }

        // Revisa uno por uno los elementos dentro del contenedor
        for (Node nodo : contenedor.getChildren()) {

            // Pregunta si el elemento actual es un Botón
            if (nodo instanceof Button btn) {

                // Crea una sombra de color blanco
                DropShadow brillo = new DropShadow(20, Color.WHITE);

                // ¿Qué pasa cuando el ratón ENTRA al botón?
                btn.setOnMouseEntered(e -> {
                    btn.setEffect(brillo); // Enciende la sombra
                    btn.setScaleX(1.05);   // Lo hace 5% más ancho
                    btn.setScaleY(1.05);   // Lo hace 5% más alto
                });

                // ¿Qué pasa cuando el ratón SALE del botón?
                btn.setOnMouseExited(e -> {
                    btn.setEffect(null);   // Apaga la sombra
                    btn.setScaleX(1.0);    // Vuelve al ancho normal
                    btn.setScaleY(1.0);    // Vuelve al alto normal
                });
            }
        }
    }

    // Función para hacer que los elementos aparezcan suavemente desde abajo
    private void animarEntrada(Node nodo, int delayMillis) {

        // Si el elemento no existe o ya fue animado antes, no hace nada
        if (nodo == null || nodosAnimados.contains(nodo)) {
            return;
        }

        // Registra este elemento para no volver a animarlo en el futuro
        nodosAnimados.add(nodo);

        // Estado inicial: lo hace totalmente invisible (0) y lo baja 40 píxeles
        nodo.setOpacity(0);
        nodo.setTranslateY(40);

        // 1. Animación de Desvanecimiento (Aparecer de invisible a visible)
        FadeTransition fade = new FadeTransition(Duration.millis(800), nodo);
        fade.setToValue(1); // Destino: 100% visible
        fade.setDelay(Duration.millis(delayMillis)); // Tiempo de espera antes de iniciar

        // 2. Animación de Movimiento (Subir a su posición original)
        TranslateTransition translate = new TranslateTransition(Duration.millis(800), nodo);
        translate.setToY(0); // Destino: Posición original (0)
        translate.setDelay(Duration.millis(delayMillis));

        // Ejecuta ambas animaciones al mismo tiempo
        fade.play();
        translate.play();
    }

    // Función que decide qué partes animar dependiendo de cuánto bajó el Scroll
    private void verificarVisibilidad(double scrollPos) {

        // Si bajó un 15%, aparece el mapa
        if (scrollPos > 0.15) {
            animarEntrada(mapaContenedor, 0);
        }

        // Si bajó un 45%, aparece la sección del celular
        if (scrollPos > 0.45) {
            animarEntrada(celularContenedor, 0);
        }

        // Si bajó un 70%, aparece el cuadro de navegación visual inferior
        if (scrollPos > 0.70) {
            animarEntrada(navegacionContenedor, 0);
        }
    }

    // ==========================================
    // SECCIÓN DE BOTONES TEMPORALES (POR HACER)
    // ==========================================

    // Al presionar "Edificio Principal" en el panel lateral
    @FXML
    void irEdificioPrincipal(ActionEvent event) {
        System.out.println("Edificio Principal");
    }

    // Al presionar "Laboratorios"
    @FXML
    void irLaboratorios(ActionEvent event) {
        System.out.println("Laboratorios");
    }

    // Al presionar "Aulas Teóricas"
    @FXML
    void irAulasTeoricas(ActionEvent event) {
        System.out.println("Aulas Teóricas");
    }
}