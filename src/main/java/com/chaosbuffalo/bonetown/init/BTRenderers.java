package com.chaosbuffalo.bonetown.init;

import com.chaosbuffalo.bonetown.client.render.entity.TestRenderer;
import com.chaosbuffalo.bonetown.client.render.entity.TestZombieRenderer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class BTRenderers {

    public static void registerRenderers(){
        RenderingRegistry.registerEntityRenderingHandler(
                BTEntityTypes.TEST_ENTITY.get(),
                TestRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(
                BTEntityTypes.TEST_ZOMBIE.get(),
                TestZombieRenderer::new
        );
    }
}
