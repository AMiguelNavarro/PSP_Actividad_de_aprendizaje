package com.svalero.gestordescargas.controlador;

import com.svalero.gestordescargas.hilo.DescargaTask;
import com.svalero.gestordescargas.utilidades.Alertas;
import javafx.concurrent.Worker;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.MalformedURLException;


public class DescargaControlador {

    public Label lbURL, lbProgreso;
    public ProgressBar pbProgreso;
    public Button btParar, btEliminar, btCancelar, btReanudar, btIniciar;
    public CheckBox cbRutaDescarga;

    private String url, rutaDescarga;
    private DescargaTask descargaTask;
    private enum Accion {CANCELAR, ELIMINAR, ELIMINAR_TODO}
    private Accion accion;
    private AppControlador appControlador;

    private static  final  Logger logger = LogManager.getLogger(DescargaControlador.class);





    public DescargaControlador(String url, String rutaDescarga, AppControlador appControlador) {

        this.url = url;
        this.rutaDescarga = rutaDescarga;
        this.appControlador = appControlador;

    }



    @FXML
    public void iniciarDescarga(Event event) {

        boolean cambiarRutaDescarga = cbRutaDescarga.isSelected();

        /**
         * Si el usuario marca el checkbox le permite cambiar la ruta antes de iniciar la descarga
         */
        if (cambiarRutaDescarga) {

            logger.trace("Se ha seleccionado otra ruta de descarga distinta a la inicial");

            FileChooser directorio = new FileChooser();
            directorio.setTitle("Seleccionar ruta de descarga");
            File fichero = directorio.showSaveDialog(btCancelar.getScene().getWindow());
            String rutaSeleccionada = fichero.getAbsolutePath();
            try {
                descargaTask = new DescargaTask(url, rutaSeleccionada);
                logger.trace("Se inicia la descarga desde la nueva ruta");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                logger.error("URL mal formada " + e.fillInStackTrace());
            }


            if (fichero == null) {
                Alertas.mostrarError("La ruta no puede estar vacia");
                logger.trace("Ruta de descarga vacía");
                return;
            }

        } else {
            try {
                descargaTask = new DescargaTask(url, rutaDescarga);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                logger.error("URL mal formada, " + e.fillInStackTrace());
            }
        }

            lbURL.setText(url);

            modoDescarga(true);
            cbRutaDescarga.setDisable(true);

        descargaTask.stateProperty().addListener((observableValue, estadoAntiguo, estadoNuevo) -> {


            if (estadoNuevo == Worker.State.RUNNING) {

                pbProgreso.progressProperty().unbind();
                pbProgreso.progressProperty().bind(descargaTask.progressProperty());

            }


            if (estadoNuevo == Worker.State.CANCELLED) {

                switch (accion) {

                    case CANCELAR:
                        lbProgreso.setText("Descarga Cancelada");
                        pbProgreso.setStyle("-fx-accent: red;");

                        break;

                    case ELIMINAR:
                        Parent pantalla = btEliminar.getParent().getParent();
                        appControlador.layout.getChildren().remove(pantalla);

                        break;

                    case ELIMINAR_TODO:
                        logger.trace("Se eliminan todas las descargas");
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


        descargaTask.messageProperty().addListener((observableValue, valorAntiguo, valorNuevo) -> {
            lbProgreso.setText(valorNuevo);
        });

        new Thread(descargaTask).start();

    }

    @FXML
    public void reanudarDescarga(Event event) {
        descargaTask.setPausado(false);

        modoDescarga(true);

        appControlador.aumentarContador();
        appControlador.lbNumDescargas.setText(String.valueOf(appControlador.contador));

    }



    @FXML
    public void eliminarDescarga(Event event) {

        // Si el boton pulsado en la alerta es cancelar, vuelve y no elimina
        if (Alertas.mostrarConfirmación().get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) {
            logger.trace("Se cancela la eliminación de la descarga: " + url);
            return;
        }

        if (descargaTask == null || descargaTask.isDone()) {


            Parent pantalla = btEliminar.getParent().getParent();
            appControlador.layout.getChildren().remove(pantalla);

            appControlador.restarContador();
            if (appControlador.contador < 0) {
                appControlador.resetearContador();
            }
            appControlador.lbNumDescargas.setText(String.valueOf(appControlador.contador));
            return;

        } else {

            accion = Accion.ELIMINAR;
            descargaTask.cancel();

        }

        if (!descargaTask.isCancelled()) {

            appControlador.restarContador();

            if (appControlador.contador < 0) {
                appControlador.resetearContador();
            }
            appControlador.lbNumDescargas.setText(String.valueOf(appControlador.contador));
        }

        logger.trace("Se ha eliminado la descarga: " + url);

    }


    @FXML
    public void pararDescarga(Event event) {

        logger.trace("Se ha parado una descarga individual");

        descargaTask.setPausado(true);

        lbProgreso.setText("Descarga Parada --> " + Math.round(descargaTask.getProgress() * 100) + " %");

        appControlador.restarContador();
        if (appControlador.contador < 0) {
            appControlador.resetearContador();
        }
        appControlador.lbNumDescargas.setText(String.valueOf(appControlador.contador));

        modoPararDescarga(true);

    }


    @FXML
    public void cancelarDescarga(Event event) {

        logger.trace("Se ha cancelado una descarga de forma individual");

        accion = Accion.CANCELAR;

        if (!descargaTask.isCancelled()){

            appControlador.restarContador();
            if (appControlador.contador < 0) {
                appControlador.resetearContador();
            }
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

        logger.trace("Se paran todas las descargas");

        if (descargaTask == null) {
            return;
        }

        descargaTask.setPausado(true);

        lbProgreso.setText("Descarga Parada --> " + Math.round(descargaTask.getProgress() * 100) + " %");

        appControlador.contador--;
        appControlador.lbNumDescargas.setText(String.valueOf(appControlador.contador));

        modoPararDescarga(true);
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
