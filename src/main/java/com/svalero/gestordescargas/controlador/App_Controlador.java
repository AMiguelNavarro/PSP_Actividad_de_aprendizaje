package com.svalero.gestordescargas.controlador;

import com.svalero.gestordescargas.hilo.Descarga_Thread;
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
import javafx.stage.DirectoryChooser;
import org.apache.commons.validator.routines.UrlValidator;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class App_Controlador {

    public TextField tfURL;
    public Button btDescargar, btPararTodas, btEliminarTodas, btRutaDescarga;
    public ScrollPane spDescargas;
    public Label lbNumDescargas, lbRutaSeleccionada;
    public VBox layout;

    private int contador = 0;
    private ArrayList<Descarga_Controlador> listaControladoresDescarga = new ArrayList<Descarga_Controlador>();
    private Descarga_Thread descargaThread;






    @FXML
    public void rutaDescarga(Event event) {

        DirectoryChooser directorio = new DirectoryChooser();
        directorio.setTitle("Seleccionar ruta de descarga");
        File fichero = directorio.showDialog(btDescargar.getScene().getWindow());
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

            Descarga_Controlador controlador = new Descarga_Controlador(this,url);
            loader.setController(controlador);

            Parent descarga = loader.load();

            layout.getChildren().add(descarga);

            descargaThread = new Descarga_Thread(this, url, controlador.pbProgreso, controlador.lbProgreso);
            descargaThread.start();

            listaControladoresDescarga.add(controlador);

            limpiarCajaURL_PedirFoco();
            //Numero de descargas actuales
            lbNumDescargas.setText(String.valueOf(contador));


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
            Alertas.mostrarInformacion("No hay ninguna descarga que parar");
        }

        for (Descarga_Controlador controlador : listaControladoresDescarga) {
            controlador.pararTodasLasDescargas();
        }
    }


    /**
     * Para todas las descargas
     */
    @FXML
    public void eliminarTodasLasDescargas(Event event) {

        if (listaControladoresDescarga.isEmpty()) {
            Alertas.mostrarInformacion("No hay ninguna descarga que eliminar");
        }

        layout.getChildren().clear();
        listaControladoresDescarga.clear();
        contador = 0;
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
