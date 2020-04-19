package com.chaosbuffalo.bonetown.client.render.render_data;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IBTRenderDataContainer extends IBTRenderData {

    boolean isInitialized();

    void incrementFrameCount();

    int getFrameSinceLastRender();
}
