package com.chaosbuffalo.bonetown.client.render.entity;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.entity.TestEntity;
import com.chaosbuffalo.bonetown.init.BTModels;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.ArmorLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TestRenderer extends BTEntityRenderer<TestEntity> {

    public static final ResourceLocation TEST_TEXTURE = new ResourceLocation(BoneTown.MODID,
            "bonetown/textures/test_cube.png");

    public TestRenderer(final EntityRendererManager manager) {
        super(manager, BTModels.TEST_CUBE);
        BoneTown.LOGGER.info("Creating test renderer");
    }

    @Override
    public ResourceLocation getEntityTexture(TestEntity entity) {
        return TEST_TEXTURE;
    }
}
