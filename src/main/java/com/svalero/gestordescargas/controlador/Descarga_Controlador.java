package com.svalero.gestordescargas.controlador;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class Descarga_Controlador {

    public Label lbURL, lbVelocidad, lbProgreso;
    public ProgressBar pbProgreso;
    public Button btParar, btEliminar, btCancelar;

    private String url;
    private App_Controlador appControlador;


    /**
     * Constructor que recibe la url como string y el controlador inicial (Para poder conseguir la ruta de descarga)
     * @param appControlador
     * @param url
     */
    public Descarga_Controlador(App_Controlador appControlador, String url) {
        this.url = url;
        this.appControlador = appControlador;

    }



    @FXML
    public void eliminarDescarga(Event event) {

        //TODO metodo eliminar descarga de la ventana (debe pararla antes)

    }


    @FXML
    public void pararDescarga(Event event) {

        // TODO metodo para parar la descarga

    }


    @FXML
    public void cancelarDescarga(Event event) {

        // TODO metodo para cancelar la descarga

    }



    private void pintarUrl(String url) {
        lbURL.setText(url);
    }

    public void pararTodasLasDescargas() {

        // TODO metodo que pare todas las descargas

    }
}
