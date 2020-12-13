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
    private enum Accion {PARAR, CANCELAR, ELIMINAR, ELIMINAR_TODO, PARAR_TODO}
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
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }


            if (fichero == null) {
                Alertas.mostrarError("La ruta no puede estar vacia");
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

        descargaTask.stateProperty().addListener((observableValue, estadoAntiguo, estadoNuevo) -> {


            if (estadoNuevo == Worker.State.RUNNING) {

                pbProgreso.progressProperty().unbind();
                pbProgreso.progressProperty().bind(descargaTask.progressProperty());

            }


            if (estadoNuevo == Worker.State.CANCELLED) {

                switch (accion) {

                    case PARAR:
                        lbProgreso.setText("Descarga Parada --> " + Math.round(descargaTask.getProgress() * 100) + " %");

                        appControlador.contador--;
                        appControlador.lbNumDescargas.setText(String.valueOf(appControlador.contador));

                        break;

                    case CANCELAR:
                        lbProgreso.setText("Descarga Cancelada");
                        pbProgreso.setStyle("-fx-accent: red;");

                        break;

                    case ELIMINAR_TODO:
                        logger.trace("Se han eliminado y cancelado todas las descargas");
                        break;

                    case PARAR_TODO:
                        logger.trace("Se han parado todas las descargas");
                        lbProgreso.setText("Descarga Parada --> " + Math.round(descargaTask.getProgress() * 100) + " %");
                        break;

                    case ELIMINAR:
                        Parent pantalla = btEliminar.getParent().getParent();
                        appControlador.layout.getChildren().remove(pantalla);

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

        // TODO que se pueda reanudar la descarga

    }



    @FXML
    public void eliminarDescarga(Event event) {

        logger.trace("Se ha eliminado una descarga individual");

        // Si el boton pulsado en la alerta es cancelar, vuelve y no elimina
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

        logger.trace("Se ha parado una descarga individual");

        accion = Accion.PARAR;
        descargaTask.cancel();

        modoPararDescarga(true);

    }


    @FXML
    public void cancelarDescarga(Event event) {

        logger.trace("Se ha cancelado una descarga de forma individual");

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

        logger.trace("Se procede a eliminar todas las descargas");

        accion = Accion.ELIMINAR_TODO;

        if (descargaTask == null) {
            return;
        }
        descargaTask.cancel();
    }





    public void pararTodasLasDescargas() {

        logger.trace("Se procede a parar todas las descargas");

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
