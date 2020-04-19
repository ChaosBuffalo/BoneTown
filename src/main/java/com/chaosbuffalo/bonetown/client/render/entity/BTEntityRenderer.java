package com.chaosbuffalo.bonetown.client.render.entity;


import com.chaosbuffalo.bonetown.client.render.RenderDataManager;
import com.chaosbuffalo.bonetown.client.render.render_data.BTModelRenderData;
import com.chaosbuffalo.bonetown.client.render.render_data.IBTRenderDataContainer;
import com.chaosbuffalo.bonetown.core.model.BTModel;
import com.chaosbuffalo.bonetown.core.materials.MaterialResourceManager;
import com.chaosbuffalo.bonetown.core.materials.IBTMaterial;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;

@OnlyIn(Dist.CLIENT)
public abstract class BTEntityRenderer<T extends Entity> extends EntityRenderer<T> {

    private BTModel model;
    protected IBTRenderDataContainer modelRenderData;
    HashMap<T, Boolean> activeEntities;


    protected BTEntityRenderer(EntityRendererManager renderManager, BTModel model) {
        super(renderManager);
        this.model = model;
        this.activeEntities = new HashMap<>();
        setupModelRenderData(model);

    }

    protected void setupModelRenderData(BTModel model){
        this.modelRenderData = RenderDataManager.MANAGER.getRenderDataForModel(model, true);
    }


    @Override
    public boolean shouldRender(T livingEntityIn, ClippingHelperImpl camera, double camX, double camY, double camZ) {
        return super.shouldRender(livingEntityIn, camera, camX, camY, camZ);
    }

    public BTModel getModel() {
        return model;
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


    public void drawModel(RenderType renderType, T entityIn, float entityYaw, float partialTicks,
                          MatrixStack matrixStackIn, Matrix4f projectionMatrix, int packedLightIn,
                          int packedOverlay, IBTMaterial program, IRenderTypeBuffer buffer){

        program.initRender(renderType, matrixStackIn, projectionMatrix, packedLightIn, packedOverlay);
        modelRenderData.render();
        program.endRender(renderType);
    }

    private void initializeRender(IBTMaterial program){
        modelRenderData.upload();
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn,
                       IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        boolean visible = this.isVisible(entityIn);
        boolean visibleToPlayer = !visible && !entityIn.isInvisibleToPlayer(Minecraft.getInstance().player);
        RenderType rendertype = this.getRenderType(entityIn, visible, visibleToPlayer);
        bufferIn.getBuffer(rendertype);
        IBTMaterial program = MaterialResourceManager.INSTANCE.getShaderProgram(model.getProgramName());
        if (!modelRenderData.isInitialized()){
           initializeRender(program);
        }
        GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
        Matrix4f projMatrix = gameRenderer.getProjectionMatrix(gameRenderer.getActiveRenderInfo(), partialTicks,
                true);
        int packedOverlay;
        if (entityIn instanceof LivingEntity){
            packedOverlay = LivingRenderer.getPackedOverlay((LivingEntity) entityIn, partialTicks);
        } else {
            packedOverlay = OverlayTexture.DEFAULT_LIGHT;
        }
        drawModel(rendertype, entityIn,
                entityYaw, partialTicks, matrixStackIn,
                projMatrix, packedLightIn,
                packedOverlay, program, bufferIn);


    }




}
