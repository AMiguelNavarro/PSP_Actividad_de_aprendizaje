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
import javafx.stage.FileChooser;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AppControlador {

    public TextField tfURL;
    public Button btDescargar, btPararTodas, btEliminarTodas, btRutaDescarga;
    public ScrollPane spDescargas;
    public Label lbNumDescargas, lbRutaSeleccionada;
    public VBox layout;

    public int contador = 0;
    public ArrayList<DescargaControlador> listaControladoresDescarga = new ArrayList<>();

    private static final Logger logger = LogManager.getLogger(AppControlador.class);






    @FXML
    public void rutaDescarga(Event event) {

        logger.trace("Ruta de descarga seleccionada");

        FileChooser directorio = new FileChooser();
        directorio.setTitle("Seleccionar ruta de descarga");
        File fichero = directorio.showSaveDialog(btDescargar.getScene().getWindow());
        String rutaSeleccionada = fichero.getAbsolutePath();


        if (fichero == null) {
            Alertas.mostrarError("La ruta no puede estar vacia");
            return;
        }

        lbRutaSeleccionada.setText(rutaSeleccionada);

        modoInicio(false);


    }




    @FXML
    public void descargar(Event event) {


        String url = tfURL.getText();
        String rutaDescarga = lbRutaSeleccionada.getText();

        if (url.isEmpty() || validarURL(url) != true) {
            Alertas.mostrarError("Debes introducir una URL válida");
            return;
        }

        aumentarContador();
        if (contador > 5) {
            Alertas.mostrarInformacion("El número máximo de descargas es 5");
            return;
        }

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Recursos.getURL("descarga.fxml"));

            DescargaControlador controlador = new DescargaControlador(url, rutaDescarga, this);
            loader.setController(controlador);

            Parent descarga = loader.load();

            controlador.pintarURL();
            controlador.modoInicial(true);

            layout.getChildren().add(descarga);

            listaControladoresDescarga.add(controlador);

            limpiarCajaURL_PedirFoco();
            //Numero de descargas actuales
            lbNumDescargas.setText(String.valueOf(contador));

            modoInicio(true);
            lbRutaSeleccionada.setText("Selecciona la ruta de descarga");


        } catch (IOException e) {
            Alertas.mostrarError("Error al cargar la ventana de descarga " + e.getMessage());
        }

    }


    /**
     * Elimina todas las descargas del scrollPane
     */
    @FXML
    public void pararTodasLasDescargas(Event event) {


        if (listaControladoresDescarga.isEmpty()){

            logger.trace("Se paran todas las descargas, pero no hay ninguna");

            Alertas.mostrarInformacion("No hay ninguna descarga que parar");

            return;
        }

        for (DescargaControlador controlador : listaControladoresDescarga) {
            controlador.eliminarTodasLasDescargas();
        }

        contador = 0;
        lbNumDescargas.setText(String.valueOf(contador));

        Alertas.mostrarInformacion("Ser han parado todas las descargas");
    }


    /**
     * Para todas las descargas
     */
    @FXML
    public void eliminarTodasLasDescargas(Event event) {


        if (listaControladoresDescarga.isEmpty()) {

            logger.trace("Se paran todas las descargas, pero la lista de descargas está vacía");

            Alertas.mostrarInformacion("No hay ninguna descarga que eliminar");
            return;
        }

        for (DescargaControlador descargaControlador : listaControladoresDescarga) {
            descargaControlador.eliminarTodasLasDescargas();
        }

        layout.getChildren().clear();
        listaControladoresDescarga.clear();
        contador = 0;
        lbNumDescargas.setText(String.valueOf(contador));

        Alertas.mostrarInformacion("Ser han eliminado todas las descargas");
    }



    private void aumentarContador() {
        contador++;
    }

    private void limpiarCajaURL_PedirFoco() {
        tfURL.clear();
        tfURL.requestFocus();
    }

    /**
     * COmprueba si la url es válida
     * @param url
     * @return
     */
    private boolean validarURL(String url) {

        boolean respuesta;

        UrlValidator validador = new UrlValidator();
        respuesta = validador.isValid(url);

        return respuesta;
    }



    public void modoInicio(boolean activado) {
        btDescargar.setDisable(activado);
    }

}
