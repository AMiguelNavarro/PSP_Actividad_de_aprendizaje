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

}
