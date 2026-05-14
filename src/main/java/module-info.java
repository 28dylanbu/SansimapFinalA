module com.example.sansimap {

    // Librerías principales de JavaFX
    requires javafx.controls;
    requires javafx.fxml;

    // Librería Swing usada en HorarioApp
    requires javafx.swing;

    // Librería para envío de correos
    requires java.mail;

    // Librerías de escritorio de Java
    requires java.desktop;
    requires javafx.web;

    // Permite que JavaFX acceda a los controladores FXML
    opens com.example.sansimap to javafx.fxml;

    // Exporta el paquete principal del proyecto
    exports com.example.sansimap;
}