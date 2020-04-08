package com.chaosbuffalo.bonetown.core.shaders;

import net.minecraft.client.shader.ShaderLoader;
import net.minecraft.util.ResourceLocation;

public class AnimatedMaterialEntry extends BTMaterialEntry {

    public AnimatedMaterialEntry(ResourceLocation name, ResourceLocation vertexShader,
                                 ResourceLocation fragShader) {
        super(name, vertexShader, fragShader);
    }

    @Override
    public IBTMaterial getProgram(int programId, ShaderLoader vertex, ShaderLoader frag) {
        return new AnimatedMaterial(programId, vertex, frag);
    }
}
