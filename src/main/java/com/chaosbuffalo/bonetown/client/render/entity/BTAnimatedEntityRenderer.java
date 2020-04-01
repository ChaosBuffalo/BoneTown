package com.chaosbuffalo.bonetown.client.render.entity;

import com.chaosbuffalo.bonetown.client.render.render_data.BTAnimatedModelRenderData;
import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.chaosbuffalo.bonetown.core.bonemf.BoneMFSkeleton;
import com.chaosbuffalo.bonetown.core.model.BTAnimatedModel;
import com.chaosbuffalo.bonetown.core.shaders.IBTShaderProgram;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;



public abstract class BTAnimatedEntityRenderer<T extends Entity & IBTAnimatedEntity> extends BTEntityRenderer<T> {
    private BTAnimatedModel animatedModel;

    protected BTAnimatedEntityRenderer(EntityRendererManager renderManager, BTAnimatedModel model) {
        super(renderManager, model);
        this.animatedModel = model;
        this.modelRenderData = new BTAnimatedModelRenderData(model, renderManager);
    }

    public BTAnimatedModel getAnimatedModel() {
        return animatedModel;
    }

    @Override
    public void drawModel(RenderType renderType, T entityIn, float entityYaw,
                          float partialTicks, MatrixStack matrixStackIn,
                          Matrix4f projectionMatrix, int packedLightIn,
                          int packedOverlay, IBTShaderProgram program) {
        program.initRender(renderType, matrixStackIn, projectionMatrix, packedLightIn, packedOverlay);
        IPose pose = entityIn.getAnimationComponent().getCurrentPose(partialTicks);
        BoneMFSkeleton skeleton = entityIn.getSkeleton();
        if (skeleton != null){
            // we need to upload this every render because if multiple models are sharing one shader
            // the uniforms will be invalid for bind pose, we could consider caching last model rendered
            // with a particular program and skip it
            program.uploadInverseBindPose(skeleton.getInverseBindPose());
        }
        program.uploadAnimationFrame(pose.getJointMatrices());
        modelRenderData.render();
        program.endRender(renderType);
    }
}
