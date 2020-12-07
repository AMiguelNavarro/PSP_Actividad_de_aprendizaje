package com.svalero.gestordescargas.controlador;

import com.svalero.gestordescargas.utilidades.Alertas;
import com.svalero.gestordescargas.utilidades.R;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class App_Controlador {

    public TextField tfURL;
    public Button btDescargar;
    public ScrollPane spDescargas;
    public Label lbNumDescargas;



    @FXML
    public void descargar(Event event) {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(R.getURL("descarga.fxml"));

            Descarga_Controller controlador = new Descarga_Controller();
            loader.setController(controlador);

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            Alertas.mostrarError("Error al cargar la ventana de descarga");
        }
    }


}
