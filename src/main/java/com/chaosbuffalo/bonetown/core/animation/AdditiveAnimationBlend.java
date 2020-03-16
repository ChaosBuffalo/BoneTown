package com.chaosbuffalo.bonetown.core.animation;

import org.joml.Matrix4f;

public class AdditiveAnimationBlend implements IPoseProvider {
    private final IAnimationProvider pose;

    public AdditiveAnimationBlend(IAnimationProvider basePose, IAnimationProvider add){
        AnimationFrame frame = new AnimationFrame();
        int count = 0;
        for (Matrix4f joint : basePose.getJointMatrices()){
            Matrix4f newJoint = new Matrix4f(joint);
            newJoint.add(add.getJointMatrix(count));
            frame.setMatrix(count, newJoint, basePose.getLocalJointMatrix(count));
            count++;
        }
        pose = frame;
    }

    @Override
    public IAnimationProvider getPose() {
        return pose;
    }
}
