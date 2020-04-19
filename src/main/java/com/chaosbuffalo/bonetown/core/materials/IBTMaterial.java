package com.chaosbuffalo.bonetown.core.materials;

import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.shader.IShaderManager;

public interface IBTMaterial extends IShaderManager {
    void initRender(RenderType renderType, MatrixStack matrixStackIn, Matrix4f projection,
                    int packedLight, int packedOverlay);

    void endRender(RenderType renderType);

    void setupUniforms();

    void uploadAnimationFrame(IPose pose);

    void uploadInverseBindPose(IPose pose);
}
