package org.glud.trascendentAR;

import com.badlogic.gdx.utils.ArrayMap;

/**
 * Created by juan on 3/17/17.
 */
public interface ARToolKitManager {
    boolean markerVisible(String markerName); //Is marker visible?
    boolean arIsRunning();
    ArrayMap<String,Integer> getMarkers();
    float[] getProjectionMatrix();
    float[] getTransformMatrix(String markerName);
}
