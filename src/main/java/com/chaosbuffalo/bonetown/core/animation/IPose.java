package com.chaosbuffalo.bonetown.core.animation;

import org.joml.Matrix4d;

public interface IPose {

    Matrix4d[] getJointMatrices();

    Matrix4d getJointMatrix(int index);

    int getJointCount();

    void setJointCount(int count);

    void setJointMatrix(int index, Matrix4d mat);

    default void copyPose(IPose otherPose) {
        for (int i = 0; i < otherPose.getJointCount(); i++){
            setJointMatrix(i, otherPose.getJointMatrix(i));
        }
    }

}
