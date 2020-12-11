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

import java.net.MalformedURLException;

public class DescargaControlador {

    public Label lbURL, lbVelocidad, lbProgreso;
    public ProgressBar pbProgreso;
    public Button btParar, btEliminar, btCancelar, btReanudar, btIniciar;

    private String url, rutaDescarga;
    private DescargaTask descargaTask;





    public DescargaControlador(String url, String rutaDescarga) {

        this.url = url;
        this.rutaDescarga = rutaDescarga;


    }



    @FXML
    public void iniciarDescarga(Event event) {

        lbURL.setText(url);
        // TODO iniciar descarga
        try {



            descargaTask = new DescargaTask(url, rutaDescarga);

            descargaTask.stateProperty().addListener((observableValue, estadoAntiguo, estadoNuevo) -> {



                if (estadoNuevo == Worker.State.RUNNING) {

                    pbProgreso.progressProperty().unbind();
                    pbProgreso.progressProperty().bind(descargaTask.progressProperty());

                }

                if (estadoNuevo == Worker.State.CANCELLED) {

                    lbProgreso.setText("Descarga Parada --> " + Math.round(descargaTask.getProgress() * 100) + " %");

                }

                if (estadoNuevo == Worker.State.SUCCEEDED) {

                    Alertas.mostrarInformacion("La descarga ha finalizado");
                    lbProgreso.setText("Descarga finalizada!! --> 100 %");

                }
                
            });


            descargaTask.messageProperty().addListener((observableValue, valorAntiguo, valorNuevo) -> {lbProgreso.setText(valorNuevo);});

            new Thread(descargaTask).start();

        } catch (MalformedURLException e) {
            Alertas.mostrarError("La URL no es válida");
        }
    }

    @FXML
    public void reanudarDescarga(Event event) {

    }



    @FXML
    public void eliminarDescarga(Event event) {

        //TODO metodo eliminar descarga de la ventana (debe pararla antes)

    }


    @FXML
    public void pararDescarga(Event event) {

        descargaTask.cancel();

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
