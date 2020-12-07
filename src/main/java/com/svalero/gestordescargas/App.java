package com.svalero.gestordescargas;

import com.svalero.gestordescargas.controlador.App_Controlador;
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

        App_Controlador controlador = new App_Controlador();
        loader.setController(controlador);

        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }
}
