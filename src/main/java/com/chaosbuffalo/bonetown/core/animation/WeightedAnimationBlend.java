package com.chaosbuffalo.bonetown.core.animation;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeightedAnimationBlend implements IPoseProvider {

    private final List<AnimationWeight> animBlends = new ArrayList<>();
    private final IAnimationProvider basePose;
    private final IAnimationProvider bakedPose;

    public WeightedAnimationBlend(IAnimationProvider basePose, AnimationWeight... blends){
        this.basePose = basePose;
        animBlends.addAll(Arrays.asList(blends));
        bakedPose = bake();
    }

    public WeightedAnimationBlend(IAnimationProvider basePose, IAnimationProvider otherPose, float time){
        this(basePose, new AnimationWeight(otherPose, time));
    }

    public IAnimationProvider getPose(){
        return bakedPose;
    }

    private IAnimationProvider bake(){
        AnimationFrame frame = new AnimationFrame();
        int count = 0;
        for (Matrix4f joint : basePose.getJointMatrices()){
            Matrix4f newJoint = new Matrix4f(joint);
            for (AnimationWeight blend : animBlends){
                newJoint.lerp(blend.provider.getJointMatrix(count), blend.weight);
            }
            frame.setMatrix(count, newJoint, basePose.getLocalJointMatrix(count));
            count++;
        }
        return frame;
    }
}
