package com.chaosbuffalo.bonetown.core.animation;

import org.joml.Matrix4d;

public class AdditiveAnimationBlend implements IPoseProvider {
    private final IPose pose;

    public AdditiveAnimationBlend(IPose basePose, IPose add){
        AnimationFrame frame = new AnimationFrame();
        int count = 0;
        for (Matrix4d joint : basePose.getJointMatrices()){
            Matrix4d newJoint = new Matrix4d(joint);
            newJoint.add(add.getJointMatrix(count));
            frame.setJointMatrix(count, newJoint);
            count++;
        }
        pose = frame;
    }

    @Override
    public IPose getPose() {
        return pose;
    }
}
