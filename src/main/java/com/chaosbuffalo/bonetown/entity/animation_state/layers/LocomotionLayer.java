package com.chaosbuffalo.bonetown.entity.animation_state.layers;

import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

public class LocomotionLayer<T extends LivingEntity & IBTAnimatedEntity<T>> extends BlendTwoPoseLayer<T> {

    public LocomotionLayer(String name, ResourceLocation idle, ResourceLocation run, T entity, boolean shouldLoop) {
        super(name, idle, run, entity, shouldLoop, 0.0f);
    }

    @Override
    protected float getBlendAmount() {
        return getEntity().limbSwingAmount;
    }
}
