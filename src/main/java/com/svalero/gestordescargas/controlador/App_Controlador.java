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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class App_Controlador {

    public TextField tfURL;
    public Button btDescargar;
    public ScrollPane spDescargas;
    public Label lbNumDescargas;
    public VBox layout;

    private int contador = 0;



    @FXML
    public void descargar(Event event) {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(R.getURL("descarga.fxml"));

            Descarga_Controlador controlador = new Descarga_Controlador();
            loader.setController(controlador);

            Parent descarga = loader.load();

            layout.getChildren().add(descarga);

        } catch (IOException e) {
            Alertas.mostrarError("Error al cargar la ventana de descarga " + e.getMessage());
        }


    }



    public void aumentarContador() {
        contador++;
    }

}
