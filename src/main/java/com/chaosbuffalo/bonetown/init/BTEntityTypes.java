package com.chaosbuffalo.bonetown.init;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.entity.DebugBoneEntity;
import com.chaosbuffalo.bonetown.entity.TestAnimatedEntity;
import com.chaosbuffalo.bonetown.entity.TestEntity;
import com.chaosbuffalo.bonetown.entity.TestZombieEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BTEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.ENTITIES, BoneTown.MODID);

    public static final String TEST_NAME = "test";
    public static final String TEST_ANIMATED_NAME = "test_animated";
    public static final String DEBUG_BONE = "debug_bone";
    public static final String TEST_ZOMBIE_NAME = "test_zombie";

    public static final RegistryObject<EntityType<TestZombieEntity>> TEST_ZOMBIE = ENTITY_TYPES.register(
            TEST_ZOMBIE_NAME, () ->
            EntityType.Builder.<TestZombieEntity>create(TestZombieEntity::new, EntityClassification.MONSTER)
                    .size(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight())
                    .build(new ResourceLocation(BoneTown.MODID, TEST_ZOMBIE_NAME).toString())
    );

    public static final RegistryObject<EntityType<TestEntity>> TEST_ENTITY = ENTITY_TYPES.register(
            TEST_NAME, () ->
            EntityType.Builder.<TestEntity>create(TestEntity::new, EntityClassification.MISC)
                    .size(EntityType.PIG.getWidth(), EntityType.PIG.getHeight())
                    .build(new ResourceLocation(BoneTown.MODID, TEST_NAME).toString())
    );
    public static final RegistryObject<EntityType<TestAnimatedEntity>> TEST_ANIMATED_ENTITY = ENTITY_TYPES.register(
            TEST_ANIMATED_NAME, () ->
            EntityType.Builder.<TestAnimatedEntity>create(TestAnimatedEntity::new, EntityClassification.MISC)
                    .size(EntityType.PIG.getWidth(), EntityType.PIG.getHeight())
                    .build(new ResourceLocation(BoneTown.MODID, TEST_ANIMATED_NAME).toString())
    );

    public static final RegistryObject<EntityType<DebugBoneEntity>> DEBUG_BONE_ENTITY = ENTITY_TYPES.register(
            DEBUG_BONE, () ->
                    EntityType.Builder.<DebugBoneEntity>create(DebugBoneEntity::new, EntityClassification.MISC)
                            .size(0.1f, 0.1f)
                            .build(new ResourceLocation(BoneTown.MODID, DEBUG_BONE).toString())
    );
}
