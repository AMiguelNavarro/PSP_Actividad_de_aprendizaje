package com.svalero.gestordescargas.controlador;

import com.svalero.gestordescargas.hilo.DescargaTask;
import com.svalero.gestordescargas.utilidades.Alertas;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
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
    private enum Accion {PARAR, CANCELAR, ELIMINAR, ELIMINAR_TODO, PARAR_TODO}
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

//                            appControlador.contador--;
//                            appControlador.lbNumDescargas.setText(String.valueOf(appControlador.contador));

                            break;

                        case ELIMINAR_TODO:
                            //TODO log progreso
                            break;

                        case PARAR_TODO:
                            //TODO log progreso
                            lbProgreso.setText("Descarga Parada --> " + Math.round(descargaTask.getProgress() * 100) + " %");
                            break;

                        case ELIMINAR:
                            Parent pantalla = btEliminar.getParent().getParent();
                            appControlador.layout.getChildren().remove(pantalla);

//                            appControlador.contador--;
//                            appControlador.lbNumDescargas.setText(String.valueOf(appControlador.contador));
                            break;
                    }



                }



                if (estadoNuevo == Worker.State.SUCCEEDED) {

                    modoDescargaFinalizada(true);
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

        // Si el boton pulsado en la alerta es cancelar, vuelve y no elimina la moto
        if (Alertas.mostrarConfirmación().get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) {
            return;
        }

        if (!descargaTask.isCancelled()){

            appControlador.contador--;
            appControlador.lbNumDescargas.setText(String.valueOf(appControlador.contador));

        } else {

            appControlador.contador++;

        }

        if (descargaTask == null || descargaTask.isDone()) {


            Parent pantalla = btEliminar.getParent().getParent();
            appControlador.layout.getChildren().remove(pantalla);

            appControlador.contador--;
            appControlador.lbNumDescargas.setText(String.valueOf(appControlador.contador));

        } else {

            accion = Accion.ELIMINAR;
            descargaTask.cancel();

        }

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

        if (!descargaTask.isCancelled()){

            appControlador.contador--;
            appControlador.lbNumDescargas.setText(String.valueOf(appControlador.contador));

        } else {

            lbProgreso.setText("Descarga Cancelada");
            pbProgreso.setStyle("-fx-accent: red;");

        }

        descargaTask.cancel();

        modoCancelarDescarga(true);

    }


    public void eliminarTodasLasDescargas() {

        accion = Accion.ELIMINAR_TODO;

        if (descargaTask == null) {
            return;
        }
        descargaTask.cancel();
    }





    public void pararTodasLasDescargas() {

        accion = Accion.PARAR_TODO;

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

    public void modoDescargaFinalizada (boolean activado) {
        btParar.setDisable(activado);
        btCancelar.setDisable(activado);
    }

}
