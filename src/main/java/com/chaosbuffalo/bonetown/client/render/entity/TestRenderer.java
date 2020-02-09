package com.chaosbuffalo.bonetown.client.render.entity;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.client.render.assimp.BTEntityRenderer;
import com.chaosbuffalo.bonetown.entities.TestEntity;
import com.chaosbuffalo.bonetown.init.ModMeshData;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class TestRenderer extends BTEntityRenderer<TestEntity> {

    public TestRenderer(final EntityRendererManager manager) {
        super(manager, ModMeshData.TEST_CUBE);
        BoneTown.LOGGER.info("Creating test renderer");
    }

    @Override
    public ResourceLocation getEntityTexture(TestEntity entity) {
        return null;
    }
}
