package com.chaosbuffalo.bonetown.client.render.entity;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.entity.DebugBoneEntity;
import com.chaosbuffalo.bonetown.init.ModMeshData;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class DebugBoneRenderer extends BTEntityRenderer<DebugBoneEntity> {

    public static final ResourceLocation TEST_TEXTURE = new ResourceLocation(BoneTown.MODID,
        "bonetown/textures/test_cube.png");

    public DebugBoneRenderer(final EntityRendererManager manager) {
        super(manager, ModMeshData.BONE_DISPLAY);
        BoneTown.LOGGER.info("Creating test renderer");
    }

    @Override
    public ResourceLocation getEntityTexture(DebugBoneEntity entity) {
        return TEST_TEXTURE;
    }
}
