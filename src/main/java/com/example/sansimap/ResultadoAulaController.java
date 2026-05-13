package com.example.sansimap;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.input.MouseEvent;
import java.io.InputStream;

public class ResultadoAulaController {

    @FXML private Label infoAula;
    @FXML private ImageView fotoUbicacion; // Para 607_ubi.png
    @FXML private HBox contenedorFotos;   // Para 607_aula.jpg
    @FXML private ImageView imgEdificio;   // Añade este fx:id en tu FXML para 607_edif.jpg

    @FXML
    public void initialize() {
        String codigo = BuscarAulaController.aulaSeleccionada;

        if (codigo != null) {
            DatoAula aula = GestorArchivo.buscarAula(codigo);

            if (aula != null) {
                // 1. Información de Texto
                StringBuilder sb = new StringBuilder();
                sb.append("📍 DETALLES DEL AULA\n\n");
                sb.append("Código: ").append(aula.codigo).append("\n");
                sb.append("Ubicación: ").append(aula.ubicacion).append("\n");
                if (aula.piso != null && !aula.piso.isEmpty()) {
                    sb.append("Piso: ").append(aula.piso).append("\n");
                }
                infoAula.setText(sb.toString());

                // 2. Cargar Imagen del Edificio (607_edif.jpg)
                if (aula.edificio != null && aula.edificio.toLowerCase().endsWith(".jpg")) {
                    cargarImagen("/imagenes/" + aula.edificio, imgEdificio);
                }

                // 3. Cargar Mapa de Ubicación (607_ubi.png)
                if (aula.ubicacionfoto != null) {
                    cargarImagen("/imagenes/" + aula.ubicacionfoto, fotoUbicacion);
                    fotoUbicacion.setVisible(true);
                }

                // 4. Cargar Galería de Fotos (607_aula.jpg)
                if (aula.fotos != null) {
                    contenedorFotos.getChildren().clear();
                    for (String f : aula.fotos) {
                        ImageView iv = new ImageView();
                        cargarImagen("/imagenes/" + f.trim(), iv);
                        iv.setFitHeight(180);
                        iv.setPreserveRatio(true);
                        contenedorFotos.getChildren().add(iv);
                    }
                }
            }
        }
    }

    private void cargarImagen(String ruta, ImageView view) {
        if (view == null) return;
        InputStream is = getClass().getResourceAsStream(ruta);
        if (is != null) {
            view.setImage(new Image(is));
        } else {
            System.err.println("❌ No se encontró: " + ruta);
        }
    }

    @FXML
    private void volverBuscar(MouseEvent event) {
        CambiarVista.cambiarContenidoVista(event, "buscar-aula.fxml");
    }
}