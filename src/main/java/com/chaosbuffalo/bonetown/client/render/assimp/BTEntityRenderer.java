package com.chaosbuffalo.bonetown.client.render.assimp;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.mesh_data.BTMeshData;
import com.chaosbuffalo.bonetown.core.shaders.BTShaderProgram;
import com.chaosbuffalo.bonetown.core.shaders.BTShaderResourceManager;
import com.chaosbuffalo.bonetown.platform.GlStateManagerExtended;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.HashMap;

@OnlyIn(Dist.CLIENT)
public abstract class BTEntityRenderer<T extends Entity> extends EntityRenderer<T> {

    private BTMeshData model;
    BTEntityRenderData entityRenderData;
    HashMap<T, Boolean> activeEntities;
    private static final ResourceLocation shader = new ResourceLocation(BoneTown.MODID, "test_prog");


    protected BTEntityRenderer(EntityRendererManager renderManager, BTMeshData model) {
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
    protected RenderType getRenderType(T entityType, boolean isInvisible, boolean visibleToPlayer) {
        ResourceLocation resourcelocation = this.getEntityTexture(entityType);
        if (visibleToPlayer) {
            return RenderType.entityTranslucent(resourcelocation);
//        } else if (isInvisible) {
//            return this.model.getRenderType(resourcelocation);
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
//        boolean visibleToPlayer = !visible && !entityIn.isInvisibleToPlayer(Minecraft.getInstance().player);
//        RenderType rendertype = this.getRenderType(entityIn, visible, visibleToPlayer);
        if (!entityRenderData.isInitialized()){
            BoneTown.LOGGER.info("In init call");
            entityRenderData.GLinit();
        }
        GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
        Matrix4f projMatrix = gameRenderer.getProjectionMatrix(gameRenderer.getActiveRenderInfo(), partialTicks,
                true);

        Matrix4f modelViewMatrix = matrixStackIn.getLast().getPositionMatrix();
        BTShaderProgram program = BTShaderResourceManager.INSTANCE.getShaderProgram(shader);
        RenderType normal = RenderType.entityTranslucent(model.getMeshes()[0].getMaterial().getTexture());
        normal.enable();
        program.useProgram();
        program.uploadModelViewMatrix(modelViewMatrix);
        program.uploadProjectionMatrix(projMatrix);
        entityRenderData.render();
        program.releaseProgram();
        normal.disable();
    }
}
