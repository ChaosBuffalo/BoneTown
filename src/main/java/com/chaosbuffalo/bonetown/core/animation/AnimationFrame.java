package com.chaosbuffalo.bonetown.core.animation;


import org.joml.Matrix4d;

import java.util.Arrays;

public class AnimationFrame implements IPose {

    public static final int MAX_JOINTS = 150;

    public static final Matrix4d[] DEFAULT_FRAME = new Matrix4d[MAX_JOINTS];

    static {
        Arrays.fill(DEFAULT_FRAME, new Matrix4d());
    }

    private final Matrix4d[] jointMatrices;

    public AnimationFrame() {
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