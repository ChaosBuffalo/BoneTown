package com.chaosbuffalo.bonetown.init;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.shaders.AnimatedMaterialEntry;
import com.chaosbuffalo.bonetown.core.shaders.BTMaterialEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class BTMaterials {

    public static final ResourceLocation DEFAULT_STATIC_LOC = new ResourceLocation(BoneTown.MODID,
            "default_static");


    public static final ResourceLocation DEFAULT_ANIMATED_LOC = new ResourceLocation(BoneTown.MODID,
            "default_animated");


    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void registerShaders(RegistryEvent.Register<BTMaterialEntry> event) {
        BoneTown.LOGGER.info("Registering Bone Town Shader Data");
        event.getRegistry().register(
                new BTMaterialEntry(
                        DEFAULT_STATIC_LOC,
                        new ResourceLocation(BoneTown.MODID, "bonetown/shaders/default_static.vs"),
                        new ResourceLocation(BoneTown.MODID, "bonetown/shaders/default_static.fs")));
        event.getRegistry().register(
                new AnimatedMaterialEntry(
                    DEFAULT_ANIMATED_LOC,
                        new ResourceLocation(BoneTown.MODID, "bonetown/shaders/default_animated.vs"),
                        new ResourceLocation(BoneTown.MODID, "bonetown/shaders/default_animated.fs")));

    }
}
