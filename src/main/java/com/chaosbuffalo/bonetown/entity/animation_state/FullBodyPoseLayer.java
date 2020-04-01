package com.chaosbuffalo.bonetown.entity.animation_state;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.animation.BakedAnimation;
import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.chaosbuffalo.bonetown.core.animation.WeightedAnimationBlend;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;


public class FullBodyPoseLayer<T extends Entity & IBTAnimatedEntity> extends AnimationLayerBase<T> {
    private BakedAnimation animation;
    private boolean shouldLoop;
    private final WeightedAnimationBlend weightedBlend;

    public FullBodyPoseLayer(String name, ResourceLocation animName, T entity, boolean shouldLoop){
        super(name, entity);
        this.shouldLoop = shouldLoop;
        this.weightedBlend = new WeightedAnimationBlend();
        if (isValid()){
            // isValid performs the null check
            this.animation = entity.getSkeleton().getBakedAnimation(animName);
            if (animation == null){
                this.isValid = false;
                BoneTown.LOGGER.error("Animation {} not found for entity: {}",
                        animName.toString(), entity.toString());
            }
        }
    }


    @Override
    void doLayerWork(IPose basePose, int currentTime, float partialTicks, IPose outPose) {
        BoneTown.LOGGER.info("Doing full body pose layer");
        BakedAnimation.InterpolationFramesReturn ret = animation.getInterpolationFrames(
                currentTime - getStartTime(), shouldLoop, partialTicks);
        weightedBlend.simpleBlend(ret.current, ret.next, ret.partialTick);
        outPose.copyPose(weightedBlend.getPose());
    }
}
