package org.glud.trascendentAR;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by juan on 4/3/17.
 */
public class ARCamera extends Camera {

    /** the field of view of the height, in degrees **/
    public float fieldOfView = 67;

    /** Constructs a new {@link ARCamera} with the given field of view and viewport size. The aspect ratio is derived from
     * the viewport size.
     *
     * @param fieldOfViewY the field of view of the height, in degrees, the field of view for the width will be calculated
     *           according to the aspect ratio.
     * @param viewportWidth the viewport width
     * @param viewportHeight the viewport height */
    public ARCamera(float fieldOfViewY, float viewportWidth, float viewportHeight) {
        this.fieldOfView = fieldOfViewY;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        update();
    }

    final Vector3 tmp = new Vector3();

    @Override
    public void update () {
        update(true);
    }

    @Override
    public void update (boolean updateFrustum) {
        view.setToLookAt(position, tmp.set(position).add(direction), up);
        combined.set(projection);
        Matrix4.mul(combined.val, view.val);

        if (updateFrustum) {
            invProjectionView.set(combined);
            Matrix4.inv(invProjectionView.val);
            frustum.update(invProjectionView);
        }
    }
}
