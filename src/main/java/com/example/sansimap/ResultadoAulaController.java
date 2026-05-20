package com.example.sansimap;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import java.io.InputStream;

public class ResultadoAulaController {

    // =====================================================================
    // VARIABLES VINCULADAS AL DISEÑO (FXML)
    // Estas variables nos permiten modificar lo que se ve en la pantalla.
    // =====================================================================

    // Contenedor vertical (arriba hacia abajo) donde pondremos los textos de información.
    @FXML private VBox contenedorInfo;

    // Visor de imagen para el mapa grande que va en el centro.
    @FXML private ImageView fotoUbicacion;

    // Contenedor horizontal (de izquierda a derecha) donde pondremos las fotos del aula.
    @FXML private HBox contenedorFotos;

    // Visor de imagen pequeño que muestra el edificio en la barra blanca izquierda.
    @FXML private ImageView imgEdificio;

    // El fondo negro que cubre toda la pantalla cuando queremos hacer "Zoom" a una foto.
    @FXML private StackPane capaZoom;

    // El visor de imagen gigante que aparece por encima del fondo negro.
    @FXML private ImageView imagenAmpliada;

    // =====================================================================
    // MÉTODO DE INICIALIZACIÓN
    // Se ejecuta automáticamente en cuanto se abre esta pantalla.
    // =====================================================================
    @FXML
    public void initialize() {
        // Recuperamos el código del aula que el usuario buscó en la pantalla anterior.
        String codigo = BuscarAulaController.aulaSeleccionada;

        if (codigo != null) {
            // Buscamos los datos completos del aula usando nuestro gestor de archivos.
            DatoAula aula = GestorArchivo.buscarAula(codigo);

            if (aula != null) {

                // 1. DIBUJAR LA INFORMACIÓN (Textos)
                agregarDatoInfo("Código del Aula", aula.codigo);
                agregarDatoInfo("Ubicación", aula.ubicacion);
                // Solo agregamos el piso si realmente hay algo escrito en el archivo txt.
                if (aula.piso != null && !aula.piso.trim().isEmpty()) {
                    agregarDatoInfo("Piso", aula.piso);
                }

                // 2. CARGAR LA IMAGEN DEL EDIFICIO (Barra lateral)
                if (aula.edificio != null && !aula.edificio.trim().isEmpty()) {
                    String nombreEdificio = aula.edificio.trim();
                    // Si el txt no dice ".jpg", se lo agregamos automáticamente para evitar errores.
                    if (!nombreEdificio.toLowerCase().endsWith(".jpg") && !nombreEdificio.toLowerCase().endsWith(".png")) {
                        nombreEdificio += ".jpg";
                    }
                    cargarImagen("/imagenes/" + nombreEdificio, imgEdificio);
                    configurarInteraccionImagen(imgEdificio); // Le damos la habilidad de hacer zoom al darle clic
                }

                // 3. CARGAR EL MAPA DE UBICACIÓN (Centro)
                if (aula.ubicacionfoto != null && !aula.ubicacionfoto.trim().isEmpty()) {
                    cargarImagen("/imagenes/" + aula.ubicacionfoto.trim(), fotoUbicacion);
                    fotoUbicacion.setVisible(true); // Lo hacemos visible
                    configurarInteraccionImagen(fotoUbicacion); // Le damos la habilidad de hacer zoom
                }

                // 4. CARGAR LA GALERÍA DE FOTOS (Bucle para procesar múltiples imágenes)
                // Verificamos que el aula tenga fotos y que la lista no esté vacía.
                if (aula.fotos != null && aula.fotos.length > 0) {
                    contenedorFotos.getChildren().clear(); // Limpiamos la galería por si había fotos viejas

                    // INICIO DEL BUCLE:
                    // Este bucle 'for each' recorre la lista de fotos del aula paso a paso.
                    // En cada paso, la variable 'f' guarda el nombre de una foto (ej: "617_int.jpg").
                    for (String f : aula.fotos) {
                        String nombreFoto = f.trim(); // .trim() borra espacios accidentales (" foto.jpg " -> "foto.jpg")

                        if (!nombreFoto.isEmpty()) {
                            ImageView iv = new ImageView(); // Creamos un cuadro en blanco para la foto
                            cargarImagen("/imagenes/" + nombreFoto, iv); // Llenamos el cuadro con la imagen

                            // Aumentamos el alto para que sean más grandes y menos cuadradas.
                            // Al usar preserveRatio(true), el ancho se ajusta solo manteniendo la proporción real.
                            iv.setFitHeight(260);
                            iv.setPreserveRatio(true);

                            // Le ponemos una sombra elegante y cambiamos el cursor a "manito"
                            iv.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 15, 0, 0, 5); -fx-cursor: hand;");

                            configurarInteraccionImagen(iv); // Le damos la habilidad de hacer zoom al darle clic

                            // Metemos la foto ya configurada en el contenedor horizontal (HBox)
                            contenedorFotos.getChildren().add(iv);
                        }
                    }
                }
            }
        }

        // =====================================================================
        // LÓGICA DE ROTACIÓN (Exclusiva para cuando la imagen está ampliada)
        // =====================================================================
        // Solo la 'imagenAmpliada' (la que está en el fondo negro) reacciona al clic derecho.
        imagenAmpliada.setOnMouseClicked(event -> {
            // Si el botón presionado fue el derecho (SECONDARY)...
            if (event.getButton() == MouseButton.SECONDARY) {
                // Tomamos la rotación actual y le sumamos 90 grados.
                imagenAmpliada.setRotate(imagenAmpliada.getRotate() + 90);

                // consume() evita que este clic derecho "traspase" la imagen y cierre el fondo negro.
                event.consume();
            }
        });
    }

    // =====================================================================
    // MÉTODOS AUXILIARES Y DE INTERACCIÓN
    // =====================================================================

    /**
     * Crea textos bonitos para la barra lateral y los mete en la caja.
     */
    private void agregarDatoInfo(String titulo, String valor) {
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 13px; -fx-text-fill: #6c757d; -fx-font-weight: bold;");

        Label lblValor = new Label(valor);
        lblValor.setStyle("-fx-font-size: 16px; -fx-text-fill: #212529;");
        lblValor.setWrapText(true);

        VBox caja = new VBox(2, lblTitulo, lblValor);
        contenedorInfo.getChildren().add(caja);
    }

    /**
     * Le enseña a una imagen qué hacer cuando el ratón pasa por encima o le hace clic.
     * OJO: Aquí ya no hay rotación, solo abrir el zoom.
     */
    private void configurarInteraccionImagen(ImageView iv) {
        // Cuando el ratón ENTRA: La imagen crece un 3% y la sombra se hace más oscura.
        iv.setOnMouseEntered(e -> {
            iv.setScaleX(1.03);
            iv.setScaleY(1.03);
            iv.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.7), 20, 0, 0, 8); -fx-cursor: hand;");
        });

        // Cuando el ratón SALE: La imagen vuelve a su tamaño y sombra original.
        iv.setOnMouseExited(e -> {
            iv.setScaleX(1.0);
            iv.setScaleY(1.0);
            iv.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 15, 0, 0, 5); -fx-cursor: hand;");
        });

        // Cuando le hacen CLIC:
        iv.setOnMouseClicked(e -> {
            // Si fue un clic Izquierdo (PRIMARY), abrimos la pantalla oscura de Zoom.
            if (e.getButton() == MouseButton.PRIMARY) {
                abrirZoom(iv.getImage());
            }
        });
    }

    /**
     * Activa el fondo negro y pone la imagen seleccionada en el centro, bien grande.
     */
    private void abrirZoom(Image imagen) {
        if (imagen != null) {
            imagenAmpliada.setImage(imagen);
            imagenAmpliada.setRotate(0); // Nos aseguramos de que empiece derecha (sin rotar)
            capaZoom.setVisible(true);   // Encendemos el fondo negro
            capaZoom.toFront();          // Lo traemos al frente de todo para que tape la aplicación
        }
    }

    /**
     * Apaga el fondo negro y nos devuelve a la aplicación normal.
     * Se activa cuando hacemos clic izquierdo en el FXML sobre el StackPane oscuro.
     */
    @FXML
    private void cerrarZoom(MouseEvent event) {
        // Solo cerramos si el usuario hizo clic Izquierdo.
        // Si hace clic derecho, lo ignoramos para que no interfiera con la rotación.
        if (event.getButton() == MouseButton.PRIMARY) {
            capaZoom.setVisible(false);    // Apagamos el fondo negro
            imagenAmpliada.setImage(null); // Vaciamos la imagen para ahorrar memoria RAM
        }
    }

    /**
     * Lee la imagen desde las carpetas del proyecto y la carga de forma segura.
     */
    private void cargarImagen(String ruta, ImageView view) {
        if (view == null) return;
        try {
            // Buscamos el archivo de imagen en la ruta indicada
            InputStream is = getClass().getResourceAsStream(ruta);
            if (is != null) {
                view.setImage(new Image(is)); // Si existe, la colocamos en el visor
            } else {
                System.err.println("❌ No se encontró la imagen en: " + ruta);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error cargando imagen " + ruta + ": " + e.getMessage());
        }
    }

    /**
     * Regresa a la ventana anterior para buscar otra aula.
     */
    @FXML
    private void volverBuscar(MouseEvent event) {
        CambiarVista.cambiarContenidoVista(event, "buscar-aula.fxml");
    }
}