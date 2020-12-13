package com.svalero.gestordescargas.hilo;

import com.svalero.gestordescargas.controlador.AppControlador;
import com.svalero.gestordescargas.utilidades.Recursos;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class SplashScreenThread extends Thread{

    private ProgressBar pbProgreso;


    public SplashScreenThread (ProgressBar pbProgreso) {
        this.pbProgreso = pbProgreso;
    }

    @Override
    public void run() {

        for (int i=1; i < 6; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            final int progreso = i;
            Platform.runLater(() -> {
                pbProgreso.setProgress((double) progreso/4);
            });
        }
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(Recursos.getURL("inicio.fxml"));
                AppControlador controlador = new AppControlador();
                loader.setController(controlador);
                Parent root = loader.load();
                controlador.lbRutaSeleccionada.setText("Selecciona una ruta de descarga");
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

                // Capturo el stage actual para cerrarlo
                Stage myStage = (Stage) this.pbProgreso.getScene().getWindow();
                myStage.close();

                controlador.modoInicio(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
