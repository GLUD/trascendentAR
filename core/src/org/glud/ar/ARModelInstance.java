package org.glud.ar;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Created by glud on 10/05/17.
 */
public class ARModelInstance extends ModelInstance{

    public boolean visible = false;

    /*
    Contructores
    Constructors
     */
    public ARModelInstance(Model model) {
        super(model);
    }

    public ARModelInstance(Model model, String nodeId, boolean mergeTransform) {
        super(model, nodeId, mergeTransform);
    }

    public ARModelInstance(Model model, Matrix4 transform, String nodeId, boolean mergeTransform) {
        super(model, transform, nodeId, mergeTransform);
    }

    public ARModelInstance(Model model, String nodeId, boolean parentTransform, boolean mergeTransform) {
        super(model, nodeId, parentTransform, mergeTransform);
    }

    public ARModelInstance(Model model, Matrix4 transform, String nodeId, boolean parentTransform, boolean mergeTransform) {
        super(model, transform, nodeId, parentTransform, mergeTransform);
    }

    public ARModelInstance(Model model, String nodeId, boolean recursive, boolean parentTransform, boolean mergeTransform) {
        super(model, nodeId, recursive, parentTransform, mergeTransform);
    }

    public ARModelInstance(Model model, Matrix4 transform, String nodeId, boolean recursive, boolean parentTransform, boolean mergeTransform) {
        super(model, transform, nodeId, recursive, parentTransform, mergeTransform);
    }

    public ARModelInstance(Model model, Matrix4 transform, String nodeId, boolean recursive, boolean parentTransform, boolean mergeTransform, boolean shareKeyframes) {
        super(model, transform, nodeId, recursive, parentTransform, mergeTransform, shareKeyframes);
    }

    public ARModelInstance(Model model, String... rootNodeIds) {
        super(model, rootNodeIds);
    }

    public ARModelInstance(Model model, Matrix4 transform, String... rootNodeIds) {
        super(model, transform, rootNodeIds);
    }

    public ARModelInstance(Model model, Array<String> rootNodeIds) {
        super(model, rootNodeIds);
    }

    public ARModelInstance(Model model, Matrix4 transform, Array<String> rootNodeIds) {
        super(model, transform, rootNodeIds);
    }

    public ARModelInstance(Model model, Matrix4 transform, Array<String> rootNodeIds, boolean shareKeyframes) {
        super(model, transform, rootNodeIds, shareKeyframes);
    }

    public ARModelInstance(Model model, Vector3 position) {
        super(model, position);
    }

    public ARModelInstance(Model model, float x, float y, float z) {
        super(model, x, y, z);
    }

    public ARModelInstance(Model model, Matrix4 transform) {
        super(model, transform);
    }

    public ARModelInstance(ModelInstance copyFrom) {
        super(copyFrom);
    }

    public ARModelInstance(ModelInstance copyFrom, Matrix4 transform) {
        super(copyFrom, transform);
    }

    public ARModelInstance(ModelInstance copyFrom, Matrix4 transform, boolean shareKeyframes) {
        super(copyFrom, transform, shareKeyframes);
    }
}
