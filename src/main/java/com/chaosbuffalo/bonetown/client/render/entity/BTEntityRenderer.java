package com.chaosbuffalo.bonetown.client.render.entity;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.mesh_data.BTModel;
import com.chaosbuffalo.bonetown.core.shaders.BTShaderProgram;
import com.chaosbuffalo.bonetown.core.shaders.BTShaderResourceManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.HashMap;

@OnlyIn(Dist.CLIENT)
public abstract class BTEntityRenderer<T extends Entity> extends EntityRenderer<T> {

    private BTModel model;
    BTEntityRenderData entityRenderData;
    HashMap<T, Boolean> activeEntities;


    protected BTEntityRenderer(EntityRendererManager renderManager, BTModel model) {
        super(renderManager);
        this.model = model;
        this.activeEntities = new HashMap<>();
        this.entityRenderData = new BTEntityRenderData(model, renderManager);

    }

    @Override
    public boolean shouldRender(T livingEntityIn, ClippingHelperImpl camera, double camX, double camY, double camZ) {
        return super.shouldRender(livingEntityIn, camera, camX, camY, camZ);
    }

    @Nullable
    protected RenderType getRenderType(T entityType, boolean isVisible, boolean visibleToPlayer) {
        ResourceLocation resourcelocation = this.getEntityTexture(entityType);
        if (visibleToPlayer) {
            return RenderType.entityTranslucent(resourcelocation);
        } else if (isVisible) {
            return RenderType.entityCutoutNoCull(resourcelocation);
        } else {
            return entityType.isGlowing() ? RenderType.outline(resourcelocation) : null;
        }
    }

    protected boolean isVisible(T livingEntityIn) {
        return !livingEntityIn.isInvisible();
    }

    public void addEntity(T entityIn) {
        activeEntities.put(entityIn, true);
    }

    public void remoteEntity(T entityIn){
        activeEntities.remove(entityIn);
    }


    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn,
                       IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        boolean visible = this.isVisible(entityIn);
        boolean visibleToPlayer = !visible && !entityIn.isInvisibleToPlayer(Minecraft.getInstance().player);
        RenderType rendertype = this.getRenderType(entityIn, visible, visibleToPlayer);
        bufferIn.getBuffer(rendertype);
        if (!entityRenderData.isInitialized()){
//            BoneTown.LOGGER.info("In init call");
            entityRenderData.GLinit();
        }

        BoneTown.LOGGER.info("Block light {}", this.getBlockLight(entityIn, partialTicks));
        BoneTown.LOGGER.info("Sky light {}",
                entityIn.world.getLightFor(LightType.SKY, new BlockPos(entityIn.getEyePosition(partialTicks))));
        GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
        Matrix4f projMatrix = gameRenderer.getProjectionMatrix(gameRenderer.getActiveRenderInfo(), partialTicks,
                true);
        BTShaderProgram program = BTShaderResourceManager.INSTANCE.getShaderProgram(model.getProgramName());
        int packedOverlay;
        if (entityIn instanceof LivingEntity){
            packedOverlay = LivingRenderer.getPackedOverlay((LivingEntity) entityIn, partialTicks);
        } else {
            packedOverlay = OverlayTexture.DEFAULT_LIGHT;
        }
        program.initRender(rendertype, matrixStackIn, projMatrix, packedLightIn, packedOverlay);
//        Utils.readLightTextureData();
        entityRenderData.render();
        program.endRender(rendertype);

    }




}
