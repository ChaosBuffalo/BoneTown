package com.chaosbuffalo.bonetown.entity.animation_state.layers;

import com.chaosbuffalo.bonetown.core.animation.BakedAnimation;
import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.chaosbuffalo.bonetown.core.animation.WeightedAnimationBlend;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;


public class FullBodyPoseLayer<T extends Entity & IBTAnimatedEntity> extends LayerWithAnimation<T> {

    private boolean shouldLoop;
    private final WeightedAnimationBlend weightedBlend;

    public FullBodyPoseLayer(String name, ResourceLocation animName, T entity, boolean shouldLoop){
        super(name, animName, entity);
        this.shouldLoop = shouldLoop;
        this.weightedBlend = new WeightedAnimationBlend();
    }


    @Override
    void doLayerWork(IPose basePose, int currentTime, float partialTicks, IPose outPose) {
        BakedAnimation animation = getAnimation(BASE_SLOT);
        if (animation != null){
            BakedAnimation.InterpolationFramesReturn ret = animation.getInterpolationFrames(
                    currentTime - getStartTime(), shouldLoop, partialTicks);
            weightedBlend.simpleBlend(ret.current, ret.next, ret.partialTick);
            outPose.copyPose(weightedBlend.getPose());
        }
    }
}
