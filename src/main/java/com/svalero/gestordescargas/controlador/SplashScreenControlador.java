package com.svalero.gestordescargas.controlador;

import com.svalero.gestordescargas.hilo.SplashScreenThread;
import javafx.concurrent.Worker;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class SplashScreenControlador {

    public ProgressBar pbProgreso;
    public Button btIniciar;

    private SplashScreenThread splashScreenThread;

    private static final Logger logger = LogManager.getLogger(SplashScreenControlador.class);


    @FXML
    public void iniciar (Event event) {

        logger.trace("Se inicia la aplicación desde el Splash Screen");

        splashScreenThread = new SplashScreenThread(pbProgreso);
        splashScreenThread.start();
    }

}
