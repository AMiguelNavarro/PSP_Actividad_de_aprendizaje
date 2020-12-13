package com.svalero.gestordescargas.hilo;

import com.svalero.gestordescargas.controlador.AppControlador;
import com.svalero.gestordescargas.utilidades.Alertas;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DescargaTask extends Task<Void> {

    private URL url;
    private File ficheroRutaDescarga;

    private static final Logger logger = LogManager.getLogger(DescargaTask.class);







    public DescargaTask(String url, String rutaDescarga) throws MalformedURLException {

        logger.trace("Descarga creada");
        this.url = new URL(url);
        this.ficheroRutaDescarga = new File(rutaDescarga);

    }


    @Override
    protected Void  call() throws Exception {

        logger.trace("Se ha iniciado la descarga de " + url);

        updateMessage("Conectando con el servidor ...");

        URLConnection urlConnection = url.openConnection();
        int tamanioFichero = urlConnection.getContentLength();
        double progresoDescarga = 0;

        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fos = new FileOutputStream(ficheroRutaDescarga);


        byte dataBuffer [] = new byte[1024];

        int bytesLeidos;
        int totalLeido = 0;
//        int anterior = 0;
        while ((bytesLeidos = bis.read(dataBuffer,0,1024)) != -1) {

            if (isCancelled()){
                logger.trace("Descarga" + url + " cancelada/parada");
                progresoDescarga = (double) totalLeido / tamanioFichero;
                updateProgress(progresoDescarga, 1);
                updateProgress(progresoDescarga,1);
                return null;
            }

//            long tiempo = System.currentTimeMillis();
//            if (tiempo % 100 == 0) {
//                double velocidad = (double) (totalLeido-anterior) / 1048576;
//                anterior = totalLeido;
//            }

            progresoDescarga = (double) totalLeido / tamanioFichero;
            updateProgress(progresoDescarga, 1);
            updateMessage("Descargando --> " + Math.round(progresoDescarga * 100) + " %. Tamaño total archivo --> " + tamanioFichero / 1000000 + " MB");

            fos.write(dataBuffer,0, bytesLeidos);
            totalLeido += bytesLeidos;


        }

        bis.close();
        fos.close();

        logger.trace("Descarga finalizada");

        updateProgress(1,1);

        return null;
    }




    public void mensajeDescargando(int porcentaje, int tamanioFichero) {

        String mensaje;

        mensaje = "Descargando --> " + porcentaje + " % de " + tamanioFichero/1000000 + " Kb";

    }
}

