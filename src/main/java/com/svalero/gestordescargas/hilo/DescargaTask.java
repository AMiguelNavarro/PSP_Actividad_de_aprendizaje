package com.svalero.gestordescargas.hilo;

import com.svalero.gestordescargas.utilidades.Alertas;
import javafx.concurrent.Task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DescargaTask extends Task<Integer> {

    private URL url;
    private File file;


    /**
     * Constructor que recibe la URL
     * @param url
     * @param file
     * @throws MalformedURLException
     */
    public DescargaTask(String url, File file) throws MalformedURLException {
        this.url = new URL(url);
        this.file = file;
    }


    @Override
    protected Integer call() throws Exception {

        URLConnection urlConnection = url.openConnection();
        double tamañoTotalFichero = urlConnection.getContentLength();

        BufferedInputStream in = new BufferedInputStream(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(file);

        byte dataBuffer[] = new byte[1024];

        int bytesLeidos;
        int totalLeido = 0;
        double progresoDescarga = 0;

        while ((bytesLeidos = in.read(dataBuffer, 0, 1024)) != -1) {

            progresoDescarga = ((double) totalLeido / tamañoTotalFichero);

            updateProgress(progresoDescarga,1);
            updateMessage(Math.round(progresoDescarga * 100)+ " %");

            fileOutputStream.write(dataBuffer, 0, bytesLeidos);
            totalLeido += bytesLeidos;

            // Si se pulsa parar entra en este if
            if (isCancelled()) {
                return null;
            }

        }

        // Descarga completada en barra progreso
        updateProgress(0,1);
        updateMessage("100%");

        return null;
    }
}
