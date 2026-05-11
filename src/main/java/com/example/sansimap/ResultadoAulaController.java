package com.example.sansimap;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import java.io.IOException;

public class ResultadoAulaController {

    @FXML private Label infoAula;
    @FXML private HBox  contenedorFotos;
    @FXML private ImageView fotoUbicacion; //

    public void setDatos(String codigo) {
        DatoAula aula = GestorArchivo.buscarAula(codigo);

        if (aula != null) {
            infoAula.setText(
                    "Aula: "      + aula.codigo    + "\n" +
                            "Ubicación: " + aula.ubicacion + "\n" +
                            "Piso: "      + aula.piso
            );

            if (aula.ubicacionfoto != null) {
                java.net.URL url = getClass().getResource("/imagenes/" + aula.ubicacionfoto);
                if (url != null) {
                    fotoUbicacion.setImage(new Image(url.toExternalForm()));
                    fotoUbicacion.setVisible(true);
                }
            } else {
                fotoUbicacion.setVisible(false);
            }

            cargarFotos(aula);
        } else {
            infoAula.setText("Aula: " + codigo + "\nInformación no disponible");
            fotoUbicacion.setVisible(false);
        }
    }

    private void cargarFotos(DatoAula aula) {
        contenedorFotos.getChildren().clear();

        // Fotos propias del aula
        for (String foto : aula.fotos) {
            agregarFoto(foto.trim(), foto.replace("_", " ").replace(".jpg", ""));
        }

        // Foto del edificio compartido
        if (aula.edificio != null) {
            agregarFoto(aula.edificio + ".jpg", "Edificio");
        }
    }

    private void agregarFoto(String nombreArchivo, String referencia) {
        String ruta = "/imagenes/" + nombreArchivo;
        try {
            Image img = new Image(getClass().getResource(ruta).toExternalForm());

            ImageView iv = new ImageView(img);
            iv.setFitWidth(180);
            iv.setFitHeight(130);
            iv.setPreserveRatio(true);

            Label ref = new Label(referencia);
            ref.setAlignment(Pos.CENTER);
            ref.setMaxWidth(180);
            ref.setStyle("-fx-text-fill: white;");

            VBox panel = new VBox(5, iv, ref);
            panel.setAlignment(Pos.CENTER);
            contenedorFotos.getChildren().add(panel);

        } catch (Exception e) {
            // foto no encontrada, se omite
        }
    }

    @FXML
    private void volverBuscar(MouseEvent event) throws IOException{ cambiarVista(event,"buscar-aula.fxml"); }

    private void cambiarVista(MouseEvent event, String fxml)throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = loader.load();

        // 3. Crear la nueva escena y ponerla en la ventana
        Scene escenaActual = ((Node) (event.getSource())).getScene();
        escenaActual.setRoot(root);

    }
}
