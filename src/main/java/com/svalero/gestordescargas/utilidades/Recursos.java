package com.svalero.gestordescargas.utilidades;

import java.io.File;
import java.net.URL;

public class Recursos {

    /**
     * Devuelve la URL donde se encuentra el fxml
     * @param nombre
     * @return
     */
    public static URL getURL(String nombre) {
        return Thread.currentThread().getContextClassLoader().getResource("interfaz" + File.separator + nombre);
    }

}
