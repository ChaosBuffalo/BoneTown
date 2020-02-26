package com.chaosbuffalo.bonetown.client.render.entity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IBTRenderData {

    void render();

    void cleanup();

    void upload();

}
