package com.chaosbuffalo.bonetown.client.render.layers;

import com.chaosbuffalo.bonetown.client.render.entity.BTAnimatedEntityRenderer;
import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class BTAnimatedLayerRenderer<T extends Entity & IBTAnimatedEntity<T>>
        implements IBTAnimatedLayerRenderer<T> {

    private final BTAnimatedEntityRenderer<T> entityRenderer;

    public BTAnimatedLayerRenderer(BTAnimatedEntityRenderer<T> entityRenderer){
        this.entityRenderer = entityRenderer;
    }

    public BTAnimatedEntityRenderer<T> getEntityRenderer() {
        return entityRenderer;
    }

    public abstract void render(MatrixStack matrixStack, IRenderTypeBuffer renderBuffer, int packedLight, T entityIn,
                                IPose pose, float partialTicks, float ageInTicks);
}
