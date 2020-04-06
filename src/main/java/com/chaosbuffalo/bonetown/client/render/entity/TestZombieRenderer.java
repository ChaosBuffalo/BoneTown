package com.chaosbuffalo.bonetown.client.render.entity;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.model.BTAnimatedModel;
import com.chaosbuffalo.bonetown.entity.TestZombieEntity;
import com.chaosbuffalo.bonetown.init.BTModels;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class TestZombieRenderer extends BTAnimatedEntityRenderer<TestZombieEntity> {

    public static final ResourceLocation ZOMBIE_TEXTURE = new ResourceLocation(BoneTown.MODID,
            "bonetown/textures/zombie.png");

    public TestZombieRenderer(final EntityRendererManager renderManager) {
        super(renderManager, (BTAnimatedModel) BTModels.BIPED);
    }


    @Override
    public void handleEntityOrientation(MatrixStack matrixStackIn, TestZombieEntity entity, float partialTicks) {
        float renderYaw = MathHelper.interpolateAngle(partialTicks, entity.prevRenderYawOffset, entity.renderYawOffset);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees( 360.0f - renderYaw));
    }

    @Override
    public ResourceLocation getEntityTexture(TestZombieEntity entity) {
        return ZOMBIE_TEXTURE;
    }
}

