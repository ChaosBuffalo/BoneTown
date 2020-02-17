package com.chaosbuffalo.bonetown.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.Util;

public class GlobalRenderInfo {

    public static final GlobalRenderInfo INFO =  new GlobalRenderInfo();
    public static final Vector3f DIFFUSE_LIGHT_0 = Util.make(new Vector3f(
            0.2F, 1.0F, -0.7F), Vector3f::normalize);
    public static final Vector3f DIFFUSE_LIGHT_1 = Util.make(new Vector3f(
            -0.2F, 1.0F, 0.7F), Vector3f::normalize);

    private MatrixStack currentFrameGlobal;

    public GlobalRenderInfo(){

    }

    public void setCurrentFrameGlobal(MatrixStack in){
        currentFrameGlobal = in;
    }

    public MatrixStack getCurrentFrameGlobal(){
        return currentFrameGlobal;
    }
}
