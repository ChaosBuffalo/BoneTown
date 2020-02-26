package com.chaosbuffalo.bonetown.core.shaders;

import net.minecraft.client.shader.ShaderLoader;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

public class BTShaderProgramEntry implements IForgeRegistryEntry<BTShaderProgramEntry> {
    private ResourceLocation location;
    private ResourceLocation vertexShader;
    private ResourceLocation fragShader;

    public BTShaderProgramEntry(ResourceLocation name, ResourceLocation vertexShader,
                                ResourceLocation fragShader){
        setRegistryName(name);
        this.vertexShader = vertexShader;
        this.fragShader = fragShader;
    }

    public ResourceLocation getVertexShader() {
        return vertexShader;
    }

    public ResourceLocation getFragShader() {
        return fragShader;
    }

    @Override
    public BTShaderProgramEntry setRegistryName(ResourceLocation name) {
        location = name;
        return this;
    }

    public IBTShaderProgram getProgram(int programId, ShaderLoader vertex, ShaderLoader frag){
        return new BTShaderProgram(programId, vertex, frag);
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return location;
    }

    @Override
    public Class<BTShaderProgramEntry> getRegistryType() {
        return BTShaderProgramEntry.class;
    }
}
