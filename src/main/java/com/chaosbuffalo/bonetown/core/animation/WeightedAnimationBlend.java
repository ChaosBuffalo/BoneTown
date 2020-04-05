package com.chaosbuffalo.bonetown.core.animation;

import org.joml.Matrix4d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeightedAnimationBlend implements IPoseProvider {

    private final List<AnimationWeight> animBlends;
    private IPose basePose;
    private final Pose workFrame;


    public WeightedAnimationBlend(){
        workFrame = new Pose();
        animBlends = new ArrayList<>();
    }

    public IPose getPose(){
        if (basePose != null){
            bake();
        }
        return workFrame;
    }

    public void setBlends(IPose basePose, AnimationWeight... blends){
        this.basePose = basePose;
        animBlends.clear();
        animBlends.addAll(Arrays.asList(blends));
    }

    public void simpleBlend(IPose basePose, IPose otherPose, float time){
        setBlends(basePose, new AnimationWeight(otherPose, time));
    }

    private void bake(){
        int count = 0;
        for (Matrix4d joint : basePose.getJointMatrices()){
            workFrame.setJointMatrix(count, joint);
            Matrix4d newJoint = workFrame.getJointMatrix(count);
            for (AnimationWeight blend : animBlends){
                newJoint.lerp(blend.provider.getJointMatrix(count), blend.weight);
            }
            count++;
        }
    }
}
