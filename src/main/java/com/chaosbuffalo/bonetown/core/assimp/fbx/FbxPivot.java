package com.chaosbuffalo.bonetown.core.assimp.fbx;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class FbxPivot {

    public static final FbxPivot Default = new FbxPivot();

    public Matrix4f translation;
    public Matrix4f rotationOffset;
    public Matrix4f rotationPivot;
    public Matrix4f preRotation;
    public Matrix4f rotation;
    public Matrix4f postRotation;
    public Matrix4f rotationPivotInverse;
    public Matrix4f scalingOffset;
    public Matrix4f scalingPivot;
    public Matrix4f scaling;
    public Matrix4f scalingPivotInverse;
    public Matrix4f geometricTranslation;
    public Matrix4f geometricRotation;
    public Matrix4f geometricScaling;

    public Matrix4f getTransform(Vector3f trans, Quaternionf rot, Vector3f scale){
        Matrix4f transform = new Matrix4f();
        if (geometricScaling != null){
            transform.mul(geometricScaling);
        }
        if (geometricRotation != null){
            transform.mul(geometricRotation);
        }
        if (geometricTranslation != null){
            transform.mul(geometricTranslation);
        }
        if (scalingPivotInverse != null){
            transform.mul(scalingPivotInverse);
        }
        if (scale != null){
            transform.mul(new Matrix4f().scale(scale));
        } else if (scaling != null){
            transform.mul(scaling);
        }
        if (scalingPivot != null){
            transform.mul(scalingPivot);
        }
        if (scalingOffset != null){
            transform.mul(scalingOffset);
        }
        if (rotationPivotInverse != null){
            transform.mul(rotationPivotInverse);
        }
        if (postRotation != null){
            transform.mul(postRotation);
        }
        if (rot != null){
            transform.mul(new Matrix4f().rotate(rot));
        } else if (rotation != null){
            transform.mul(rotation);
        }
        if (preRotation != null){
            transform.mul(preRotation);
        }
        if (rotationPivot != null){
            transform.mul(rotationPivot);
        }
        if (rotationOffset != null){
            transform.mul(rotationOffset);
        }
        if (trans != null) {
            transform.mul(new Matrix4f().translate(trans));
        } else if (translation != null) {
            transform.mul(translation);
        }
        return transform;
    }


}
