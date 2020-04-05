package com.chaosbuffalo.bonetown.core.animation;

import org.joml.Matrix4d;

import static com.chaosbuffalo.bonetown.core.animation.AnimationFrame.MAX_JOINTS;

public class Pose implements IPose {

    private final Matrix4d[] jointMatrices;

    public Pose() {
        jointMatrices = new Matrix4d[MAX_JOINTS];
        for (int i = 0; i < MAX_JOINTS; i++){
            jointMatrices[i] = new Matrix4d();
        }
    }
    @Override
    public Matrix4d[] getJointMatrices() {
        return jointMatrices;
    }

    @Override
    public Matrix4d getJointMatrix(int index) {
        return jointMatrices[index];
    }

    @Override
    public void setJointMatrix(int index, Matrix4d mat) {
        jointMatrices[index].set(mat);
    }
}
