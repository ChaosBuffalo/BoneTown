package com.chaosbuffalo.bonetown.core.shaders;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.client.render.GlobalRenderInfo;
import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.client.shader.ShaderLoader;

import java.util.ArrayList;
import java.util.List;


public class BTMaterial implements IBTMaterial {
    protected final int program;
    protected final ShaderLoader vert;
    protected final ShaderLoader frag;
    protected boolean firstUpload;
    protected final static int DIFFUSE_COUNT = 2;
    public MaterialUniform projUniform;
    public MaterialUniform modelViewUniform;
    public MaterialUniform lightMapUV;
    public MaterialUniform overlayUV;
    public MaterialUniform ambientLight;
    public MaterialUniform diffuseColors;
    public MaterialUniform diffuseLocs;
    protected final List<MaterialUniform> uniforms = new ArrayList<>();

    public BTMaterial(int program, ShaderLoader vert, ShaderLoader frag) {
        this.program = program;
        this.vert = vert;
        this.frag = frag;
        this.firstUpload = true;
        modelViewUniform = new MaterialUniform("model_view",
                MaterialUniform.UniformType.mat4x4, this);
        uniforms.add(modelViewUniform);
        projUniform = new MaterialUniform("proj_mat",
                MaterialUniform.UniformType.mat4x4, this);
        uniforms.add(projUniform);
        lightMapUV = new MaterialUniform("lightmap_uv",
                MaterialUniform.UniformType.vec2f, this);
        uniforms.add(lightMapUV);
        overlayUV = new MaterialUniform("overlay_uv",
                MaterialUniform.UniformType.vec2f, this);
        uniforms.add(overlayUV);
        ambientLight = new MaterialUniform("ambient_light",
                MaterialUniform.UniformType.vec3f,this);
        uniforms.add(ambientLight);
        diffuseColors = new MaterialUniform("diffuse_colors",
                MaterialUniform.UniformType.vec3f, DIFFUSE_COUNT, this);
        uniforms.add(diffuseColors);
        diffuseLocs = new MaterialUniform("diffuse_locs",
                MaterialUniform.UniformType.vec3f, DIFFUSE_COUNT, this);
        uniforms.add(diffuseLocs);

    }

    @Override
    public void initRender(RenderType renderType, MatrixStack matrixStackIn, Matrix4f projection,
                           int packedLight, int packedOverlay){

        this.useProgram();
        renderType.enable();
        if (this.firstUpload){
            initStaticValues();
            this.firstUpload = false;
        }
        this.uploadModelViewMatrix(matrixStackIn.getLast().getPositionMatrix());
        this.uploadProjectionMatrix(projection);
        this.uploadPackedLightMap(packedLight);
        this.uploadPackedOverlay(packedOverlay);
        this.receiveLightingInfo(GlobalRenderInfo.INFO.getCurrentFrameGlobal());

    }

    public void initStaticValues(){
        ambientLight.set(0.4f, 0.4f, 0.4f);
        ambientLight.upload();
        diffuseColors.set(0.6f, 0.6f, 0.6f, 0.6f, 0.6f, 0.6f);
        diffuseColors.upload();
    }

    @Override
    public void endRender(RenderType renderType){
        renderType.disable();
        this.releaseProgram();
    }

    @Override
    public int getProgram() {
        return program;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public void setupUniforms(){
        for (MaterialUniform uniform : uniforms){
            uniform.bindUniform(program);
        }
    }

    @Override
    public void uploadAnimationFrame(IPose pose) {
        BoneTown.LOGGER.warn("BTShaderProgram does not support animation, skipping uploadAnimationFrame. " +
                "Use AnimatedShaderProgram instead.");
    }

    @Override
    public void uploadInverseBindPose(IPose pose) {
        BoneTown.LOGGER.warn("BTShaderProgram does not support animation, skipping uploadInverseBindPose. " +
                "Use AnimatedShaderProgram instead.");
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

    public void uploadPackedLightMap(int packedLightmap){
        lightMapUV.set(((packedLightmap & 0xFFFF) / 256.0f) + 0.03125f, ((packedLightmap >>> 16) / 256.0f) + 0.03125f);
        lightMapUV.upload();
    }

    public void uploadPackedOverlay(int packedOverlay){
        overlayUV.set((short)(packedOverlay & '\uffff') / 16.0f, (short)(packedOverlay >>> 16 & '\uffff') / 16.0f);
        overlayUV.upload();
    }


    public void receiveLightingInfo(MatrixStack lightingStack){
        Vector4f diffuse0Loc = new Vector4f(GlobalRenderInfo.DIFFUSE_LIGHT_0);
        diffuse0Loc.transform(lightingStack.getLast().getPositionMatrix());
        Vector4f diffuse1Loc = new Vector4f(GlobalRenderInfo.DIFFUSE_LIGHT_1);
        diffuse1Loc.transform(lightingStack.getLast().getPositionMatrix());

        diffuseLocs.set(diffuse0Loc.getX(), diffuse0Loc.getY(), diffuse0Loc.getZ(),
                diffuse1Loc.getX(), diffuse1Loc.getY(), diffuse1Loc.getZ());
        diffuseLocs.upload();

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