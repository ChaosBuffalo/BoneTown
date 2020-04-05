package com.chaosbuffalo.bonetown.entity.animation_state.layers;

import com.chaosbuffalo.bonetown.core.animation.BakedAnimation;
import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.chaosbuffalo.bonetown.core.animation.WeightedAnimationBlend;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import com.chaosbuffalo.bonetown.entity.animation_state.messages.layer.AnimationLayerMessage;
import com.chaosbuffalo.bonetown.entity.animation_state.messages.layer.ChangeBlendWeightMessage;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class BlendTwoPoseLayer<T extends Entity & IBTAnimatedEntity<T>> extends LayerWithAnimation<T> {
    public static final String SECOND_SLOT = "SECOND_SLOT";

    private final WeightedAnimationBlend anim1Blend;
    private final WeightedAnimationBlend anim2Blend;
    private final WeightedAnimationBlend finalBlend;
    private boolean shouldLoop;
    private float blendWeight;

    public BlendTwoPoseLayer(String name, ResourceLocation anim1, ResourceLocation anim2, T entity,
                             boolean shouldLoop, float blendWeight) {
        super(name, anim1, entity);
        anim1Blend = new WeightedAnimationBlend();
        anim2Blend = new WeightedAnimationBlend();
        finalBlend = new WeightedAnimationBlend();
        this.shouldLoop = shouldLoop;
        setAnimation(anim2, SECOND_SLOT);
        this.blendWeight = blendWeight;
        addMessageCallback(ChangeBlendWeightMessage.CHANGE_BLEND_WEIGHT_TYPE, this::consumeChangeBlendWeight);
    }

    protected float getBlendAmount(){
        return blendWeight;
    }

    private void consumeChangeBlendWeight(AnimationLayerMessage message){
        if (message instanceof ChangeBlendWeightMessage){
            ChangeBlendWeightMessage changeMessage = (ChangeBlendWeightMessage) message;
            this.blendWeight = changeMessage.getBlendWeight();
        }
    }

    @Override
    void doLayerWork(IPose basePose, int currentTime, float partialTicks, IPose outPose) {
        BakedAnimation baseAnimation = getAnimation(BASE_SLOT);
        BakedAnimation blendAnimation = getAnimation(SECOND_SLOT);
        if (baseAnimation != null && blendAnimation != null){
            BakedAnimation.InterpolationFramesReturn ret = baseAnimation.getInterpolationFrames(
                    currentTime - getStartTime(), shouldLoop, partialTicks);
            anim1Blend.simpleBlend(ret.current, ret.next, ret.partialTick);
            BakedAnimation.InterpolationFramesReturn ret2 = blendAnimation.getInterpolationFrames(
                    currentTime - getStartTime(), shouldLoop, partialTicks);
            anim2Blend.simpleBlend(ret2.current, ret2.next, ret2.partialTick);
            finalBlend.simpleBlend(anim1Blend.getPose(), anim2Blend.getPose(), getBlendAmount());
            outPose.copyPose(finalBlend.getPose());
        }
    }
}
