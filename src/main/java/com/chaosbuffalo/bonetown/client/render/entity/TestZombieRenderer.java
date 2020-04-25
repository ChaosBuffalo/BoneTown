package com.chaosbuffalo.bonetown.client.render.entity;


import com.chaosbuffalo.bonetown.core.model.BTAnimatedModel;
import com.chaosbuffalo.bonetown.entity.TestZombieEntity;
import com.chaosbuffalo.bonetown.init.BTModels;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class TestZombieRenderer extends AnimatedBipedRenderer<TestZombieEntity> {

    public static final ResourceLocation ZOMBIE_TEXTURE = new ResourceLocation(
            "textures/entity/zombie/zombie.png");

    public TestZombieRenderer(final EntityRendererManager renderManager) {
        super(renderManager, (BTAnimatedModel) BTModels.BIPED, 1.0f);
    }


    @Override
    public ResourceLocation getEntityTexture(TestZombieEntity entity) {
        return ZOMBIE_TEXTURE;
    }
}

