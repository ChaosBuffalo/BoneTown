package com.chaosbuffalo.bonetown.client.render.entity;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.animation_data.AnimationFrame;
import com.chaosbuffalo.bonetown.core.animation_data.BTAnimation;
import com.chaosbuffalo.bonetown.core.animation_data.BTSkeleton;
import com.chaosbuffalo.bonetown.core.mesh_data.BTAnimatedModel;
import com.chaosbuffalo.bonetown.core.shaders.AnimatedShaderProgram;
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

    @Override
    public void drawModel(RenderType renderType, T entityIn, float entityYaw,
                          float partialTicks, MatrixStack matrixStackIn,
                          Matrix4f projectionMatrix, int packedLightIn,
                          int packedOverlay, IBTShaderProgram program) {
        program.initRender(renderType, matrixStackIn, projectionMatrix, packedLightIn, packedOverlay);
        BTSkeleton skeleton = animatedModel.getSkeleton();
        BTAnimation animation = skeleton.getAnimation(entityIn.getCurrentAnimation());
        if (animation != null && program instanceof AnimatedShaderProgram){
            AnimationFrame frame = animation.getFrame(entityIn.getAnimationTicks(),
                    entityIn.doLoopAnimation());
            ((AnimatedShaderProgram) program).uploadAnimationFrame(frame.getJointMatrices());
        }
        modelRenderData.render();
        program.endRender(renderType);
    }
}
