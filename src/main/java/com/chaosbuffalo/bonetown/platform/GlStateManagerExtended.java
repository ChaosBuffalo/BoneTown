package com.chaosbuffalo.bonetown.platform;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/**
 * Created by Jacob on 1/25/2020.
 */
public class GlStateManagerExtended extends GlStateManager {

    public static int genVertexArrays() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        return GL30.glGenVertexArrays();
    }

    public static void bindVertexArray(int array) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL30.glBindVertexArray(array);
    }

    public static void enableVertexAttribArray(int array){
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL30.glEnableVertexAttribArray(array);
    }

    public static void deleteVertexArrays(int vaoId){
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL30.glDeleteVertexArrays(vaoId);
    }

    public static void disableVertexAttribArray(int array){
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL30.glDisableVertexAttribArray(array);
    }

    public static void drawElements(int mode, int count, int type, long indices){
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glDrawElements(mode, count, type, indices);
    }

}
