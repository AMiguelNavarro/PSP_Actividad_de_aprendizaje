package com.svalero.gestordescargas;

import com.svalero.gestordescargas.controlador.AppControlador;
import com.svalero.gestordescargas.utilidades.Recursos;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    public static void main(String[] args) {
        launch();
    }


    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Recursos.getURL("inicio.fxml"));

        AppControlador controlador = new AppControlador();
        loader.setController(controlador);

        Parent root = loader.load();

        controlador.lbRutaSeleccionada.setText("Selecciona una ruta de descarga");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        controlador.modoInicio(true);

    }
}
