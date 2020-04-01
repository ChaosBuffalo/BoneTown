package com.chaosbuffalo.bonetown.core.bonemf;

import org.joml.Vector4d;

public class BoneMFNodeFrame {

    private Vector4d rotation;
    private Vector4d scale;
    private Vector4d translation;

    public BoneMFNodeFrame(){

    }

    public void setTranslation(Vector4d translation) {
        this.translation = translation;
    }

    public void setRotation(Vector4d rotation) {
        this.rotation = rotation;
    }

    public void setScale(Vector4d scale) {
        this.scale = scale;
    }

    public Vector4d getRotation() {
        return rotation;
    }

    public Vector4d getScale() {
        return scale;
    }

    public Vector4d getTranslation() {
        return translation;
    }
}
