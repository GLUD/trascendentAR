package org.glud.ar;

/**
 * Created by juan on 3/17/17.
 */
public interface ARToolKitManager {
    boolean marcadorVisible(int marcadorId); //Is marker visible?
    int cargarMarcador(String config);
    int obtenerMarcador();
}
