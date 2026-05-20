package com.example.sansimap;

// Importaciones para las animaciones visuales (movimiento y desvanecimiento)
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;

// Importaciones  principales del sistema JavaFX
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
import javafx.scene.web.WebView;
import javafx.stage.Stage;

// Control del tiempo para las animaciones
import javafx.util.Duration;

// Herramientas de Java
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


// Matias, josé y carlos
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
    private Pane contenedorMapa; // Reemplazamos el ImageView

    private WebView webViewMapa;
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

    //NUEVO!
    // Variables para guardar la posición del ratón al arrastrar
    private double ultimaPosX;
    private double ultimaPosY;
    // Definimos los límites como constantes arriba en tu clase, para que no haya zoom infinito
    private static final double ZOOM_MAXIMO = 4.5; // No dejar que se acerque más de 4.5x
    private static final double ZOOM_MINIMO = 0.6; // No dejar que se aleje más de 0.6x

    // Método initialize:
    // Se ejecuta automáticamente al cargar el FXML de esta ventana
    @FXML
    public void initialize() {

        // 1. INICIALIZAR EL MOTOR WEB PARA EL MAPA
        webViewMapa = new WebView();

        // Le damos un tamaño base grande para el campus
        webViewMapa.setPrefSize(1200, 800);

        // IMPORTANTE: Hacemos el WebView "transparente" a los clics del ratón.
        // Así, los clics los detecta el Pane (contenedorMapa) para poder arrastrarlo.
        webViewMapa.setMouseTransparent(true);

        // Desactivamos el menú contextual (clic derecho del navegador)
        webViewMapa.setContextMenuEnabled(false);

        // 2. CARGAR EL SVG
        java.net.URL svgUrl = getClass().getResource("/com/example/sansimap/MAPA.svg");

        if (svgUrl != null) {

            // - background-color: #FBEAE3 funde el fondo con del mapa.
            // - margin: 0; padding: 0 quita los bordes blancos.
            // - object-fit: contain; hace que el mapa crezca al máximo sin deformarse.
            String html = "<html>"
                    + "<body style='margin: 0; padding: 0; background-color: #FBEAE3; overflow: hidden; display: flex; justify-content: center; align-items: center; height: 100vh; width: 100vw;'>"
                    + "<img src='" + svgUrl.toExternalForm() + "' style='width: 100%; height: 100%; object-fit: contain;' />"
                    + "</body>"
                    + "</html>";

            // HTML en lugar de cargar solo el archivo
            webViewMapa.getEngine().loadContent(html);

            contenedorMapa.getChildren().add(webViewMapa);

            // Centrar inicialmente el contenedor
            contenedorMapa.setTranslateX(50);
            contenedorMapa.setTranslateY(50);
        } else {
            System.err.println("Error: Archivo SVG no encontrado.");
        }

        // --- LÓGICA DE ARRASTRE (PANEO) ---
        contenedorMapa.setOnMousePressed(event -> {
            ultimaPosX = event.getSceneX();
            ultimaPosY = event.getSceneY();
        });

        contenedorMapa.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - ultimaPosX;
            double deltaY = event.getSceneY() - ultimaPosY;
            contenedorMapa.setTranslateX(contenedorMapa.getTranslateX() + deltaX);
            contenedorMapa.setTranslateY(contenedorMapa.getTranslateY() + deltaY);
            ultimaPosX = event.getSceneX();
            ultimaPosY = event.getSceneY();
        });

        // --- LÓGICA DE ZOOM CON LA RUEDA DEL RATÓN ---
        contenedorMapa.setOnScroll(event -> {
            event.consume(); // Bloqueamos el scroll normal de la página

            double factor = event.getDeltaY() > 0 ? 1.1 : 0.9;
            double escalaActual = contenedorMapa.getScaleX();
            double nuevaEscala = escalaActual * factor;

            // Verificamos nuestros límites de seguridad
            if (nuevaEscala >= ZOOM_MINIMO && nuevaEscala <= ZOOM_MAXIMO) {

                // 1. Obtenemos las coordenadas exactas locales del ratón
                double mouseX = event.getX();
                double mouseY = event.getY();

                // 2. LA FÓRMULA MÁGICA CORREGIDA Y PERFECTA
                // Calculamos cuánto "sobra" de tamaño al crecer o achicarse
                double ajusteX = mouseX * (escalaActual - nuevaEscala);
                double ajusteY = mouseY * (escalaActual - nuevaEscala);

                // 3. Compensamos el movimiento moviendo el mapa en dirección contraria
                contenedorMapa.setTranslateX(contenedorMapa.getTranslateX() + ajusteX);
                contenedorMapa.setTranslateY(contenedorMapa.getTranslateY() + ajusteY);

                // 4. Aplicamos el crecimiento a todo el contenedor
                // (El WebView mantendrá los vectores nítidos automáticamente)
                contenedorMapa.setScaleX(nuevaEscala);
                contenedorMapa.setScaleY(nuevaEscala);
            }
        });

        // ... (Aquí mantienes el resto de tu código de animaciones y menú lateral)
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

    // Métodos enlazados a los botones "+" y "-" de la pantalla
    @FXML
    void zoomIn(ActionEvent event) {
        aplicarZoom(1.2); // Acercar un 20%
    }

    @FXML
    void zoomOut(ActionEvent event) {
        aplicarZoom(0.8); // Alejar un 20%
    }

    // El motor para los botones en pantalla
    private void aplicarZoom(double factor) {
        double escalaActual = contenedorMapa.getScaleX();
        double nuevaEscala = escalaActual * factor;

        if (nuevaEscala >= ZOOM_MINIMO && nuevaEscala <= ZOOM_MAXIMO) {

            // Calculamos el centro exacto del contenedor
            double centroX = contenedorMapa.getBoundsInLocal().getWidth() / 2;
            double centroY = contenedorMapa.getBoundsInLocal().getHeight() / 2;

            // Usamos la misma fórmula matemática de compensación
            double ajusteX = centroX * (escalaActual - nuevaEscala);
            double ajusteY = centroY * (escalaActual - nuevaEscala);

            contenedorMapa.setTranslateX(contenedorMapa.getTranslateX() + ajusteX);
            contenedorMapa.setTranslateY(contenedorMapa.getTranslateY() + ajusteY);

            contenedorMapa.setScaleX(nuevaEscala);
            contenedorMapa.setScaleY(nuevaEscala);
        }
    }

}