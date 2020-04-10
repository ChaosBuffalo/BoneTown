package com.chaosbuffalo.bonetown.client.render.layers;

import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;

public interface IBTAnimatedLayerRenderer<T extends Entity> {

    void render(MatrixStack matrixStack, IRenderTypeBuffer renderBuffer, int packedLight, T entityIn,
                IPose pose, float partialTicks, float ageInTicks);
}
