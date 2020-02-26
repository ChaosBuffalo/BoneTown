package com.chaosbuffalo.bonetown.core.shaders;

import org.joml.Matrix4f;

public interface IProgramReceivesAnimations {

    void uploadAnimationFrame(Matrix4f[] joints);
}
