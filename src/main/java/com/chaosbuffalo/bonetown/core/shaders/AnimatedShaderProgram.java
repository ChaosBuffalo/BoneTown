package com.chaosbuffalo.bonetown.core.shaders;

import com.chaosbuffalo.bonetown.core.animation.AnimationFrame;
import net.minecraft.client.shader.ShaderLoader;
import org.joml.Matrix4f;

public class AnimatedShaderProgram extends BTShaderProgram implements IProgramReceivesAnimations {

    public BTShaderUniform jointsUniform;

    public AnimatedShaderProgram(int program, ShaderLoader vert, ShaderLoader frag) {
        super(program, vert, frag);
        jointsUniform = new BTShaderUniform("joints_matrix",
                BTShaderUniform.UniformType.vecmat4x4, AnimationFrame.MAX_JOINTS, this);
        uniforms.add(jointsUniform);
    }

    @Override
    public void uploadAnimationFrame(Matrix4f[] joints) {
        jointsUniform.set(joints);
        jointsUniform.upload();
    }
}
