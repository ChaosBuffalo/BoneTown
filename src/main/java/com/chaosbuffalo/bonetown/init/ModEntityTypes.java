package com.chaosbuffalo.bonetown.init;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.entities.TestEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.ENTITIES, BoneTown.MODID);

    public static final String TEST_NAME = "test";
    public static final RegistryObject<EntityType<TestEntity>> TEST_ENTITY = ENTITY_TYPES.register(TEST_NAME, () ->
            EntityType.Builder.<TestEntity>create(TestEntity::new, EntityClassification.MISC)
                    .size(EntityType.PIG.getWidth(), EntityType.PIG.getHeight())
                    .build(new ResourceLocation(BoneTown.MODID, TEST_NAME).toString())
    );
}
