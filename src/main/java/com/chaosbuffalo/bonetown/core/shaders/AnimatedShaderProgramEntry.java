package com.chaosbuffalo.bonetown.core.shaders;

import net.minecraft.client.shader.ShaderLoader;
import net.minecraft.util.ResourceLocation;

public class AnimatedShaderProgramEntry extends BTShaderProgramEntry {

    public AnimatedShaderProgramEntry(ResourceLocation name, ResourceLocation vertexShader,
                                      ResourceLocation fragShader) {
        super(name, vertexShader, fragShader);
    }

    @Override
    public IBTShaderProgram getProgram(int programId, ShaderLoader vertex, ShaderLoader frag) {
        return new AnimatedShaderProgram(programId, vertex, frag);
    }
}
