package com.svalero.gestordescargas.controlador;

import com.svalero.gestordescargas.hilo.DescargaTask;
import com.svalero.gestordescargas.utilidades.Alertas;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.MalformedURLException;

public class Descarga_Controlador {

    public Label lbURL, lbVelocidad, lbPorcentaje;
    public ProgressBar pbProgreso;
    public Button btParar, btEliminar, btIniciar;

    private String url;
    private DescargaTask descargaTask;


    /**
     * COnstructor que recibe la URL como String
     * @param url
     */
    public Descarga_Controlador(String url) {
        this.url = url;

    }



    @FXML
    public void eliminarDescarga(Event event) {

    }


    @FXML
    public void pararDescarga(Event event) {

        // Envia una orden de cancelacion a la clase DescargaTask
        descargaTask.cancel();

    }

    @FXML
    public void iniciarDescarga(Event event){

        try {
            pintarUrl(url);

            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showSaveDialog(null);

            descargaTask = new DescargaTask(url, file);


            // Permite acceder al progreso de la tarea
            pbProgreso.progressProperty().unbind();
            // Vinculo la barra de progreso al progreso de la tarea
            pbProgreso.progressProperty().bind(descargaTask.progressProperty());



            // Cada vez que el hilo tiene un cambio lo avisa con el listener
            // Compactado con lambda
            // Permite acceder al estado de la tarea
            descargaTask.stateProperty().addListener((observableValue, estadoAntiguo, estadoNuevo) -> {

                // Aquí lo que quiero que haga cuando cambie algo de la tarea
                if (estadoNuevo == Worker.State.SUCCEEDED) {
                    Alertas.mostrarInformacion("La descarga ha finalizado");
                }

                // Si el estado es cancelado muestra la alerta
                if (estadoNuevo == Worker.State.CANCELLED) {
                    Alertas.mostrarInformacion("Descarga parada");
                }

            });

            descargaTask.messageProperty().addListener((observableValue, valorAntiguo, valorNuevo) -> {

                lbPorcentaje.setText(valorNuevo);

            });

            new Thread(descargaTask).start();

        } catch (MalformedURLException e) {

            Alertas.mostrarError("Error al iniciar la descarga del fichero");

        }

    }


    private void pintarUrl(String url) {
        lbURL.setText(url);
    }

    public void pararTodasLasDescargas() {

        descargaTask.cancel();

    }
}
