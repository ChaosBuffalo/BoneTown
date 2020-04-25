package com.chaosbuffalo.bonetown.core.animation;


import org.joml.Matrix4d;

import java.util.Arrays;

public class AnimationFrame extends Pose {

    public static final int MAX_JOINTS = 100;

    public static final Matrix4d[] DEFAULT_FRAME = new Matrix4d[MAX_JOINTS];

    static {
        Arrays.fill(DEFAULT_FRAME, new Matrix4d());
    }

    private final Matrix4d[] localJointMatrices;

    public AnimationFrame() {
        super();
        localJointMatrices = new Matrix4d[MAX_JOINTS];
        for (int i = 0; i < MAX_JOINTS; i++){
            localJointMatrices[i] = new Matrix4d();
        }
    }

    public void setLocalJointMatrix(int index, Matrix4d mat){
        localJointMatrices[index].set(mat);
    }

    public Matrix4d getLocalJointMatrix(int index){
        return localJointMatrices[index];
    }


}