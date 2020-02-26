package com.chaosbuffalo.bonetown.client.render.entity;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.mesh_data.BTAnimatedModel;
import com.chaosbuffalo.bonetown.entity.TestAnimatedEntity;
import com.chaosbuffalo.bonetown.init.ModMeshData;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class TestAnimatedRenderer extends BTAnimatedEntityRenderer<TestAnimatedEntity> {

    public static final ResourceLocation SPIDER_TEXTURE = new ResourceLocation(BoneTown.MODID,
            "bone_town/textures/spider.png");

    public TestAnimatedRenderer(final EntityRendererManager renderManager) {
        super(renderManager, (BTAnimatedModel) ModMeshData.SPIDER);
    }

    @Override
    public ResourceLocation getEntityTexture(TestAnimatedEntity entity) {
        return SPIDER_TEXTURE;
    }
}
