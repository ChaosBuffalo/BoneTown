package com.chaosbuffalo.bonetown.client.render.entity;

import com.chaosbuffalo.bonetown.core.model.BTAnimatedModel;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public abstract class AnimatedLivingEntityRenderer<T extends LivingEntity & IBTAnimatedEntity<T>> extends
        BTAnimatedEntityRenderer<T> {

    protected AnimatedLivingEntityRenderer(EntityRendererManager renderManager,
                                           BTAnimatedModel model, float shadowSize) {
        super(renderManager, model, shadowSize);
    }

    @Override
    public void handleEntityOrientation(MatrixStack matrixStackIn, T entity, float partialTicks) {
        float renderYaw = MathHelper.interpolateAngle(partialTicks, entity.prevRenderYawOffset, entity.renderYawOffset);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees( -renderYaw));
    }
}
