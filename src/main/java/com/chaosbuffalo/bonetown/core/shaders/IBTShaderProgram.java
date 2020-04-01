package com.chaosbuffalo.bonetown.core.shaders;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.shader.IShaderManager;
import org.joml.Matrix4d;

public interface IBTShaderProgram extends IShaderManager {
    void initRender(RenderType renderType, MatrixStack matrixStackIn, Matrix4f projection,
                    int packedLight, int packedOverlay);

    void endRender(RenderType renderType);

    void setupUniforms();

    void uploadAnimationFrame(Matrix4d[] joints);

    void uploadInverseBindPose(Matrix4d[] bindPose);
}