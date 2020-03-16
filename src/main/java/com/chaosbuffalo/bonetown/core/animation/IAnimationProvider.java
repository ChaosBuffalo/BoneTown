package com.chaosbuffalo.bonetown.core.animation;

import org.joml.Matrix4f;

public interface IAnimationProvider {

    Matrix4f[] getJointMatrices();

    Matrix4f getJointMatrix(int index);

    Matrix4f getLocalJointMatrix(int index);

    Matrix4f[] getLocalJointMatrices();


}
