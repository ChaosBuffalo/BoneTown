package com.chaosbuffalo.bonetown.client.render.entity;

import com.chaosbuffalo.bonetown.client.render.BTClientMathUtils;
import com.chaosbuffalo.bonetown.client.render.layers.IBTAnimatedLayerRenderer;
import com.chaosbuffalo.bonetown.client.render.render_data.BTAnimatedModelRenderData;
import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.chaosbuffalo.bonetown.core.bonemf.BoneMFSkeleton;
import com.chaosbuffalo.bonetown.core.model.BTAnimatedModel;
import com.chaosbuffalo.bonetown.core.shaders.IBTMaterial;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import org.joml.Matrix4d;

import java.util.ArrayList;
import java.util.List;



public abstract class BTAnimatedEntityRenderer<T extends Entity & IBTAnimatedEntity<T>> extends BTEntityRenderer<T> {
    private BTAnimatedModel animatedModel;
    private final List<IBTAnimatedLayerRenderer<T>> renderLayers;

    protected BTAnimatedEntityRenderer(EntityRendererManager renderManager, BTAnimatedModel model, float shadowSize) {
        super(renderManager, model);
        this.renderLayers = new ArrayList<>();
        this.animatedModel = model;
        this.modelRenderData = new BTAnimatedModelRenderData(model, renderManager);
        this.shadowSize = shadowSize;
    }

    public BTAnimatedModel getAnimatedModel() {
        return animatedModel;
    }

    public List<IBTAnimatedLayerRenderer<T>> getRenderLayers() {
        return renderLayers;
    }

    public void addRenderLayer(IBTAnimatedLayerRenderer<T> layer){
        renderLayers.add(layer);
    }

    public void handleEntityOrientation(MatrixStack matrixStackIn, T entity, float partialTicks){

    }

    public void moveMatrixStackToBone(T entityIn, String boneName, MatrixStack matrixStack, IPose pose){
        BoneMFSkeleton skeleton = entityIn.getSkeleton();
        if (skeleton != null){
            int boneId = skeleton.getBoneId(boneName);
            if (boneId != -1){
                Matrix4d boneMat = pose.getJointMatrix(boneId);
                BTClientMathUtils.applyTransformToStack(boneMat, matrixStack);
            }
        }
    }

    protected float getTimeAlive(T entity, float partialTicks){
        return entity.ticksExisted + partialTicks;
    }

    @Override
    public void drawModel(RenderType renderType, T entityIn, float entityYaw,
                          float partialTicks, MatrixStack matrixStackIn,
                          Matrix4f projectionMatrix, int packedLightIn,
                          int packedOverlay, IBTMaterial program, IRenderTypeBuffer buffer) {
        matrixStackIn.push();
        handleEntityOrientation(matrixStackIn, entityIn, partialTicks);
        program.initRender(renderType, matrixStackIn, projectionMatrix, packedLightIn, packedOverlay);
        IPose pose = entityIn.getAnimationComponent().getCurrentPose(partialTicks);
        BoneMFSkeleton skeleton = entityIn.getSkeleton();
        if (skeleton != null){
            // we need to upload this every render because if multiple models are sharing one shader
            // the uniforms will be invalid for bind pose, we could consider caching last model rendered
            // with a particular program and skip it
            program.uploadInverseBindPose(skeleton.getInverseBindPose());
        }
        program.uploadAnimationFrame(pose);
        modelRenderData.render();
        program.endRender(renderType);
        for (IBTAnimatedLayerRenderer<T> layer : getRenderLayers()){
            layer.render(matrixStackIn, buffer, packedLightIn, entityIn, pose, partialTicks,
                    getTimeAlive(entityIn, partialTicks));
        }
        matrixStackIn.pop();
    }
}
