package com.chaosbuffalo.bonetown.core.animation;


import org.joml.Matrix4f;

import java.util.Arrays;

public class AnimationFrame implements IAnimationProvider {

    private static final Matrix4f IDENTITY_MATRIX = new Matrix4f();

    private final Matrix4f[] localJointMatrices;

    public static final int MAX_JOINTS = 150;

    public static final Matrix4f[] DEFAULT_FRAME = new Matrix4f[MAX_JOINTS];

    static {
        Arrays.fill(DEFAULT_FRAME, IDENTITY_MATRIX);
    }

    private final Matrix4f[] jointMatrices;

    public AnimationFrame() {
        jointMatrices = new Matrix4f[MAX_JOINTS];
        Arrays.fill(jointMatrices, IDENTITY_MATRIX);
        localJointMatrices = new Matrix4f[MAX_JOINTS];
        Arrays.fill(localJointMatrices, IDENTITY_MATRIX);
    }

    @Override
    public Matrix4f[] getJointMatrices() {
        return jointMatrices;
    }

    @Override
    public Matrix4f getJointMatrix(int index) {
        return jointMatrices[index];
    }

    @Override
    public Matrix4f getLocalJointMatrix(int index){
        return localJointMatrices[index];
    }

    @Override
    public Matrix4f[] getLocalJointMatrices() {
        return localJointMatrices;
    }

    public void setMatrix(int pos, Matrix4f localJointMatrix, Matrix4f invJointMatrix) {
        localJointMatrices[pos] = localJointMatrix;

        jointMatrices[pos] = invJointMatrix.mul(localJointMatrix);
    }
}