package com.svalero.gestordescargas.hilo;

import com.svalero.gestordescargas.controlador.App_Controlador;
import com.svalero.gestordescargas.utilidades.Alertas;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Descarga_Thread  extends  Thread{

    private App_Controlador appControlador;
    private File rutaDescarga;
    private String url;
    private ProgressBar pbProgreso;
    private Label lbProgreso;


    public Descarga_Thread(App_Controlador appControlador, String url, ProgressBar pbProgreso, Label lbProgreso) throws MalformedURLException {

        this.appControlador = appControlador;
        this.url = url;
        this.pbProgreso = pbProgreso;
        this.lbProgreso = lbProgreso;

    }


    @Override
    public void run() {

        rutaDescarga = new File(appControlador.lbRutaSeleccionada.getText());

        System.out.println(rutaDescarga);   // Coge la ruta correctamente
        System.out.println(url); // Coge la URL correctamente
        try {
            URL url = new URL(this.url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            long tamanioFichero = urlConnection.getContentLength();
            String nombreFichero = "descarga";


            BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
            FileOutputStream fileOutputStream = new FileOutputStream(rutaDescarga + File.separator + nombreFichero);
            byte dataBuffer[] = new byte[1024];
            int bytesLeidos;
            int totalLeido = 0;

            while ((bytesLeidos = in.read(dataBuffer,0,1024)) != -1) {
                fileOutputStream.write(dataBuffer,0,bytesLeidos);
                System.out.println("------Descargando");

                totalLeido += bytesLeidos;

                int finalTotalLeido = totalLeido;
                Platform.runLater(() -> {
                    pbProgreso.setProgress((double) finalTotalLeido /tamanioFichero);
                    lbProgreso.setText(finalTotalLeido * 100 / tamanioFichero + " %. Tamaño total del archivo --> " + tamanioFichero);
                });

            }

            System.out.println("Descargado");



        } catch (IOException ioe) {
            ioe.getMessage();
        }



    }
}
