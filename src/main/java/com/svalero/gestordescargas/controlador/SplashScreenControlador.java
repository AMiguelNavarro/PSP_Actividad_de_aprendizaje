package com.svalero.gestordescargas.controlador;

import com.svalero.gestordescargas.hilo.SplashScreenThread;
import javafx.concurrent.Worker;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;

public class SplashScreenControlador {

    public ProgressBar pbProgreso;
    public Button btIniciar;

    private SplashScreenThread splashScreenThread;


    @FXML
    public void iniciar (Event event) {
        splashScreenThread = new SplashScreenThread(pbProgreso);
        splashScreenThread.start();
    }

}
