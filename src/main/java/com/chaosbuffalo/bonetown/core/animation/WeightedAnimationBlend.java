package com.chaosbuffalo.bonetown.core.animation;

import org.joml.Matrix4d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeightedAnimationBlend implements IPoseProvider {

    private final List<AnimationWeight> animBlends;
    private IPose basePose;
    private final AnimationFrame workFrame;


    public WeightedAnimationBlend(){
        workFrame = new AnimationFrame();
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
            Matrix4d newJoint = new Matrix4d(joint);
            for (AnimationWeight blend : animBlends){
                newJoint.lerp(blend.provider.getJointMatrix(count), blend.weight);
            }
            workFrame.setJointMatrix(count, newJoint);
            count++;
        }
    }
}
