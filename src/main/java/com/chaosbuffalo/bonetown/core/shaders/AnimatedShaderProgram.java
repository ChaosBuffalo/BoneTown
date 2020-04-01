package com.chaosbuffalo.bonetown.core.shaders;


import com.chaosbuffalo.bonetown.core.animation.AnimationFrame;
import net.minecraft.client.shader.ShaderLoader;
import org.joml.Matrix4d;

public class AnimatedShaderProgram extends BTShaderProgram {

    public BTShaderUniform jointsUniform;
    public BTShaderUniform inverseBindPoseUniform;

    public AnimatedShaderProgram(int program, ShaderLoader vert, ShaderLoader frag) {
        super(program, vert, frag);
        jointsUniform = new BTShaderUniform("joints_matrix",
                BTShaderUniform.UniformType.vecmat4x4, AnimationFrame.MAX_JOINTS, this);
        uniforms.add(jointsUniform);
        inverseBindPoseUniform = new BTShaderUniform("inverse_bind_pose",
                BTShaderUniform.UniformType.vecmat4x4, AnimationFrame.MAX_JOINTS, this);
        uniforms.add(inverseBindPoseUniform);
    }

    @Override
    public void uploadInverseBindPose(Matrix4d[] bindPose){
        inverseBindPoseUniform.set(bindPose);
        inverseBindPoseUniform.upload();
    }

    @Override
    public void uploadAnimationFrame(Matrix4d[] joints) {
        jointsUniform.set(joints);
        jointsUniform.upload();
    }
}
