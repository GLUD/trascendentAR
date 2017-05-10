package org.glud.ar;

import com.badlogic.gdx.utils.ArrayMap;

/**
 * Created by juan on 3/17/17.
 */
public interface ARToolKitManager {
    boolean markerVisible(int markerId); //Is marker visible?
    boolean markerVisible(String markerName); //Is marker visible?
    int getMarker(String config);
    ArrayMap<String,Integer> getMarkers();
    float[] getProjectionMatrix();
    float[] getTransformMatrix(int marcadorId);
}
