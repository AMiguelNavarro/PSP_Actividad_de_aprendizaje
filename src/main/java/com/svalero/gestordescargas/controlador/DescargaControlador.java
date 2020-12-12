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
import java.util.ArrayList;

public class DescargaControlador {

    public Label lbURL, lbVelocidad, lbProgreso;
    public ProgressBar pbProgreso;
    public Button btParar, btEliminar, btCancelar, btReanudar, btIniciar;

    private String url, rutaDescarga;
    private DescargaTask descargaTask;
    private enum Accion {PARAR, CANCELAR, ELIMINAR, ELIMINAR_TODO}
    private Accion accion;
    private AppControlador appControlador;






    public DescargaControlador(String url, String rutaDescarga, AppControlador appControlador) {

        this.url = url;
        this.rutaDescarga = rutaDescarga;
        this.appControlador = appControlador;

    }



    @FXML
    public void iniciarDescarga(Event event) {

        lbURL.setText(url);

        modoDescarga(true);

        try {

            descargaTask = new DescargaTask(url, rutaDescarga);

            descargaTask.stateProperty().addListener((observableValue, estadoAntiguo, estadoNuevo) -> {



                if (estadoNuevo == Worker.State.RUNNING) {

                    pbProgreso.progressProperty().unbind();
                    pbProgreso.progressProperty().bind(descargaTask.progressProperty());

                }





                if (estadoNuevo == Worker.State.CANCELLED) {

                    switch (accion){

                        case PARAR:
                            lbProgreso.setText("Descarga Parada --> " + Math.round(descargaTask.getProgress() * 100) + " %");

                            appControlador.contador--;
                            appControlador.lbNumDescargas.setText(String.valueOf(appControlador.contador));

                            break;

                        case CANCELAR:
                            lbProgreso.setText("Descarga Cancelada");
                            pbProgreso.setStyle("-fx-accent: red;");

                            appControlador.contador--;
                            appControlador.lbNumDescargas.setText(String.valueOf(appControlador.contador));

                            break;

                        case ELIMINAR_TODO:
                            //TODO log progreso
                            break;

                        case ELIMINAR:
                            break;
                    }



                }



                if (estadoNuevo == Worker.State.SUCCEEDED) {

                    Alertas.mostrarInformacion("La descarga ha finalizado");
                    lbProgreso.setText("Descarga finalizada!! --> 100 %");
                    pbProgreso.setStyle("-fx-accent: green;");

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

        // TODO que se pueda reanudar la descarga

    }



    @FXML
    public void eliminarDescarga(Event event) {

        //TODO metodo eliminar descarga de la ventana (debe pararla antes)
        accion = Accion.ELIMINAR;
        descargaTask.cancel();

    }


    @FXML
    public void pararDescarga(Event event) {

        accion = Accion.PARAR;
        descargaTask.cancel();

        modoPararDescarga(true);

    }


    @FXML
    public void cancelarDescarga(Event event) {

        accion = Accion.CANCELAR;
        descargaTask.cancel();

        //TODO si se para la descarga primero y luego se cancela, no actualiza la barra de progreso ni el texto, pero si cambia el modo a modoCancelarDescarga
        modoCancelarDescarga(true);

    }


    public void eliminarTodasLasDescargas() {

        accion = Accion.ELIMINAR_TODO;

        if (descargaTask == null) {
            return;
        }
        descargaTask.cancel();
    }


    public void pintarURL() {
        lbURL.setText(url);
    }

    public void modoInicial(boolean activado) {
        btParar.setDisable(activado);
        btReanudar.setDisable(activado);
        btCancelar.setDisable(activado);

        btIniciar.setDisable(!activado);
        btEliminar.setDisable(!activado);
    }


    public void modoDescarga (boolean activado) {
        btIniciar.setDisable(activado);
        btReanudar.setDisable(activado);

        btParar.setDisable(!activado);
        btCancelar.setDisable(!activado);
        btEliminar.setDisable(!activado);
    }

    public void modoPararDescarga (boolean activado) {
        btReanudar.setDisable(!activado);

        btParar.setDisable(activado);
    }

    public void modoCancelarDescarga (boolean activado) {

        btIniciar.setDisable(activado);
        btReanudar.setDisable(activado);
        btParar.setDisable(activado);
    }

}
