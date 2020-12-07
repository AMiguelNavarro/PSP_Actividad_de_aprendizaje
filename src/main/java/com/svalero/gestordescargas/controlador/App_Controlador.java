package com.svalero.gestordescargas.controlador;

import com.svalero.gestordescargas.utilidades.Alertas;
import com.svalero.gestordescargas.utilidades.Recursos;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

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

        String url = tfURL.getText();
        limpiarCajaURL_PedirFoco();


        if (contador == 5) {
            Alertas.mostrarInformacion("El número máximo de descargas es 5");
            return;
        }

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Recursos.getURL("descarga.fxml"));

            Descarga_Controlador controlador = new Descarga_Controlador(url);
            loader.setController(controlador);

            Parent descarga = loader.load();

            layout.getChildren().add(descarga);

            aumentarContador();

        } catch (IOException e) {
            Alertas.mostrarError("Error al cargar la ventana de descarga " + e.getMessage());
        }


    }



    private void aumentarContador() {
        contador++;
    }

    private void limpiarCajaURL_PedirFoco() {
        tfURL.clear();
        tfURL.requestFocus();
    }

}
