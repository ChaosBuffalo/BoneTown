package com.chaosbuffalo.bonetown.entity.animation_state.layers;

import com.chaosbuffalo.bonetown.core.animation.BakedAnimation;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

public class LocomotionLayer<T extends LivingEntity & IBTAnimatedEntity<T>> extends BlendTwoPoseLayer<T> {

    public LocomotionLayer(String name, ResourceLocation idle, ResourceLocation run, T entity, boolean shouldLoop) {
        super(name, idle, run, entity, shouldLoop, 0.0f);
        computeDuration();
    }

    @Override
    protected float getBlendAmount() {
        return getEntity().limbSwingAmount;
    }

    private void computeDuration(){
        BakedAnimation anim = getAnimation(SECOND_SLOT);
        if (anim != null){
            duration = anim.getTotalTicks();
        }
    }
}
