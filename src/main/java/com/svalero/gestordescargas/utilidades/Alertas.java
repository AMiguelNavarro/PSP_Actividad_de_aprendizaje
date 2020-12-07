package com.svalero.gestordescargas.utilidades;

import javafx.scene.control.Alert;

public class Alertas {

    /**
     * Muestra una alerta de error
     * @param mensaje
     */
    public static void mostrarError(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setContentText(mensaje);
        alerta.show();

    }


    /**
     * Muestra alerta de información
     * @param mensaje
     */
    public static void mostrarInformacion(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setContentText(mensaje);
        alerta.show();

    }

}
