package com.chaosbuffalo.bonetown.client.render.entity;

import com.chaosbuffalo.bonetown.client.render.layers.AnimatedHeldItemLayer;
import com.chaosbuffalo.bonetown.core.model.BTAnimatedModel;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import com.chaosbuffalo.bonetown.entity.IHasHandBones;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;

public abstract class AnimatedBipedRenderer<T extends LivingEntity & IBTAnimatedEntity<T> & IHasHandBones> extends
        AnimatedLivingEntityRenderer<T> {

    protected AnimatedBipedRenderer(EntityRendererManager renderManager, BTAnimatedModel model, float shadowSize) {
        super(renderManager, model, shadowSize);
        addRenderLayer(new AnimatedHeldItemLayer<>(this));
    }
}
