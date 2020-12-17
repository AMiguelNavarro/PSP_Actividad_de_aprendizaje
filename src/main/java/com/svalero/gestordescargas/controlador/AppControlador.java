package com.svalero.gestordescargas.controlador;

import com.svalero.gestordescargas.utilidades.Alertas;
import com.svalero.gestordescargas.utilidades.Recursos;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;

public class AppControlador {

    public TextField tfURL;
    public Button btDescargar, btPararTodas, btEliminarTodas, btRutaDescarga, btHistorial;
    public Label lbNumDescargas, lbRutaSeleccionada;
    public VBox layout;
    public TextArea taHistorial;

    public int contador = 0;
    public ArrayList<DescargaControlador> listaControladoresDescarga = new ArrayList<>();

    private static final Logger logger = LogManager.getLogger(AppControlador.class);

    // Se declara aquí para poder utilizarla en los logger
    private String rutaDescarga;






    @FXML
    public void rutaDescarga(Event event) {

        FileChooser directorio = new FileChooser();
        directorio.setTitle("Seleccionar ruta de descarga");
        File fichero = directorio.showSaveDialog(btDescargar.getScene().getWindow());

        if (fichero == null) {
            Alertas.mostrarError("La ruta no puede estar vacia");
            logger.trace("Se cancela la ventana de selección de ruta de descarga");
            return;
        }

        String rutaSeleccionada = fichero.getAbsolutePath();
        rutaDescarga = rutaSeleccionada;

        lbRutaSeleccionada.setText(rutaSeleccionada);

        modoInicio(false);

        logger.trace("Ruta de descarga seleccionada: " + rutaSeleccionada);


    }




    @FXML
    public void descargar(Event event) {


        String url = tfURL.getText();
        String rutaDescarga = lbRutaSeleccionada.getText();

        if (url.isEmpty() || validarURL(url) != true) {
            Alertas.mostrarError("Debes introducir una URL válida");

            logger.trace("Se pulsa el botón de descarga pero no se ha introducido ninguna URL válida");
            return;
        }

        aumentarContador();

        if (contador > 5) {
            Alertas.mostrarInformacion("El número máximo de descargas es 5");

            logger.trace("Se intenta descargar un archivo pero no se puede ya que hay 5 decargas pendientes");
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
            logger.trace("Se añade una descarga");

            listaControladoresDescarga.add(controlador);
            logger.trace("Se añade un controlador a la lista de controladores");

            limpiarCajaURL_PedirFoco();
            //Numero de descargas actuales
            lbNumDescargas.setText(String.valueOf(contador));

            modoInicio(true);
            lbRutaSeleccionada.setText("Selecciona la ruta de descarga");


        } catch (IOException e) {
            Alertas.mostrarError("Error al cargar la ventana de descarga " + e.getMessage());

            logger.error("Error al añadir la descarga cargando la ventana " + e.fillInStackTrace());
        }

    }


    /**
     * Elimina todas las descargas del scrollPane
     */
    @FXML
    public void pararTodasLasDescargas(Event event) {


        if (listaControladoresDescarga.isEmpty()){

            Alertas.mostrarInformacion("No hay ninguna descarga que parar");

            logger.trace("Se intentan parar todas las descargas, pero no hay ninguna");

            return;
        }

        for (DescargaControlador controlador : listaControladoresDescarga) {
            controlador.pararTodasLasDescargas();
        }

        contador = 0;
        logger.trace("Reseteo del contador de descargas a 0");
        lbNumDescargas.setText(String.valueOf(contador));

        Alertas.mostrarInformacion("Ser han parado todas las descargas");

    }


    /**
     * Para todas las descargas
     */
    @FXML
    public void eliminarTodasLasDescargas(Event event) {

        // Si el boton pulsado en la alerta es cancelar, vuelve y no elimina
        if (Alertas.mostrarConfirmación().get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) {
            logger.trace("Se cancela la eliminación de la descarga: " + rutaDescarga);
            return;
        }


        if (listaControladoresDescarga.isEmpty()) {

            logger.trace("Se intentan eliminar todas las descargas, pero la lista de descargas está vacía");

            Alertas.mostrarInformacion("No hay ninguna descarga que eliminar");
            return;
        }

        for (DescargaControlador descargaControlador : listaControladoresDescarga) {
            descargaControlador.eliminarTodasLasDescargas();
        }

        layout.getChildren().clear();
        listaControladoresDescarga.clear();
        contador = 0;
        logger.trace("Reseteo del contador de descargas a 0");
        lbNumDescargas.setText(String.valueOf(contador));

        Alertas.mostrarInformacion("Ser han eliminado todas las descargas");
    }




    @FXML
    public void verHistorial (Event event) {

        try {
            FileInputStream fileInputStream = new FileInputStream("ejemplolog4j.log");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

            String linea;

            while ((linea = bufferedReader.readLine()) != null) {
                taHistorial.appendText(linea);
                taHistorial.appendText("\n");

            }

            logger.trace("Se visualiza el historial de la aplicación");

            fileInputStream.close();
            bufferedReader.close();

        } catch (FileNotFoundException e) {
            Alertas.mostrarError("Error al buscar el fichero que contiene el historial");
            logger.error("No se encuentra el fichero de historial " + e.fillInStackTrace());
        } catch (IOException e) {
            logger.error("No se puede leer el fichero de historial correctamente " +e.fillInStackTrace());
            Alertas.mostrarError("Error al leer el fichero que contiene el historial");
        }

    }








    //public para poder usarlo en el controlador de descarga
    public void aumentarContador() {
        contador++;
    }
    //public para poder usarlo en el controlador de descarga
    protected void restarContador() {
        contador--;
    }
    //public para poder usarlo en el controlador de descarga
    public void resetearContador () {
        contador = 0;
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
