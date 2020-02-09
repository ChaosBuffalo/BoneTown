package com.chaosbuffalo.bonetown.core.shaders;

import com.chaosbuffalo.bonetown.BoneTown;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.shader.IShaderManager;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.client.shader.ShaderLoader;
import net.minecraft.client.shader.ShaderUniform;


public class BTShaderProgram implements IShaderManager {
    private final int program;
    private final ShaderLoader vert;
    private final ShaderLoader frag;
    public ShaderUniform projUniform;
    public ShaderUniform modelViewUniform;

    public BTShaderProgram(int program, ShaderLoader vert, ShaderLoader frag) {
        this.program = program;
        this.vert = vert;
        this.frag = frag;
        projUniform = new ShaderUniform("projectionMatrix", ShaderUniform.parseType("matrix4x4"),
                16, this);
        modelViewUniform = new ShaderUniform("model_view", ShaderUniform.parseType("matrix4x4"),
                16, this);

    }

    @Override
    public int getProgram() {
        return program;
    }

    @Override
    public void markDirty() {

    }

    public void setupUniforms(){
        int loc = ShaderUniform.func_227806_a_(program, "model_view");
        modelViewUniform.setUniformLocation(loc);
        int projLoc = ShaderUniform.func_227806_a_(program, "proj_mat");
        projUniform.setUniformLocation(projLoc);
    }

    public void useProgram(){
        ShaderLinkHelper.func_227804_a_(program);
    }

    public void releaseProgram(){
        ShaderLinkHelper.func_227804_a_(0);
    }

    public void uploadProjectionMatrix(Matrix4f mat){
        projUniform.set(mat);
        projUniform.upload();
    }

    public void uploadModelViewMatrix(Matrix4f mat){
        modelViewUniform.set(mat);
        modelViewUniform.upload();
    }

    @Override
    public ShaderLoader getVertexShaderLoader() {
        return vert;
    }

    @Override
    public ShaderLoader getFragmentShaderLoader() {
        return frag;
    }
}