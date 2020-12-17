package com.svalero.gestordescargas.hilo;


import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DescargaTask extends Task<Void> {

    private URL url;
    private File ficheroRutaDescarga;
    private boolean pausado;

    private static final Logger logger = LogManager.getLogger(DescargaTask.class);



    public DescargaTask(String url, String rutaDescarga) throws MalformedURLException {

        logger.trace("Descarga creada");
        this.url = new URL(url);
        this.ficheroRutaDescarga = new File(rutaDescarga);

    }

    public boolean isPausado() { return pausado;}

    public void setPausado(boolean pausado) {

        this.pausado = pausado;
        if (pausado) {
            logger.trace("Se duerme el hilo hasta que se reanude la descarga");
        } else {
            logger.trace("Se reanuda el hilo de descarga");
        }
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

        while ((bytesLeidos = bis.read(dataBuffer,0,1024)) != -1) {

            if (pausado) {
                Thread.sleep(1000);
                continue;
            }

            if (isCancelled()){
                logger.trace("Descarga" + url + " cancelada/parada");
                progresoDescarga = (double) totalLeido / tamanioFichero;
                updateProgress(progresoDescarga, 1);
                updateProgress(progresoDescarga,1);
                return null;
            }

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

}

