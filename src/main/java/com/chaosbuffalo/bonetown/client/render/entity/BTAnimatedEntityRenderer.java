package com.chaosbuffalo.bonetown.client.render.entity;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.client.render.render_data.BTAnimatedModelRenderData;
import com.chaosbuffalo.bonetown.core.animation.AnimationFrame;
import com.chaosbuffalo.bonetown.core.animation.BTAnimation;
import com.chaosbuffalo.bonetown.core.animation.BTSkeleton;
import com.chaosbuffalo.bonetown.core.animation.WeightedAnimationBlend;
import com.chaosbuffalo.bonetown.core.bonemf.BoneMFSkeleton;
import com.chaosbuffalo.bonetown.core.model.BTAnimatedModel;
import com.chaosbuffalo.bonetown.core.shaders.AnimatedShaderProgram;
import com.chaosbuffalo.bonetown.core.shaders.IBTShaderProgram;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;

import java.util.Arrays;
import java.util.Optional;


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
        Optional<BoneMFSkeleton> skeleton = animatedModel.getSkeleton();
        BTAnimation animation = null;
        if (animation != null && program instanceof AnimatedShaderProgram){

            BTAnimation.AnimationFrameReturn ret = animation.getInterpolatedFrame(entityIn.getAnimationTicks(),
                    entityIn.doLoopAnimation(), partialTicks);
            WeightedAnimationBlend blend = new WeightedAnimationBlend(ret.current, ret.next, ret.partialTick);
            ((AnimatedShaderProgram) program).uploadAnimationFrame(ret.current.getJointMatrices());

        } else if (program instanceof AnimatedShaderProgram){
//            BoneTown.LOGGER.info("Animation not found for {}", entityIn.getCurrentAnimation());

            ((AnimatedShaderProgram) program).uploadAnimationFrame(AnimationFrame.DEFAULT_FRAME);

        }
        modelRenderData.render();
        program.endRender(renderType);
    }
}
