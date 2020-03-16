package com.chaosbuffalo.bonetown.init;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.shaders.AnimatedShaderProgramEntry;
import com.chaosbuffalo.bonetown.core.shaders.BTShaderProgramEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModShaderData {

    public static final ResourceLocation DEFAULT_STATIC_LOC = new ResourceLocation(BoneTown.MODID,
            "default_static");


    public static final ResourceLocation DEFAULT_ANIMATED_LOC = new ResourceLocation(BoneTown.MODID,
            "default_animated");


    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void registerShaders(RegistryEvent.Register<BTShaderProgramEntry> event) {
        BoneTown.LOGGER.info("Registering Bone Town Shader Data");
        event.getRegistry().register(
                new BTShaderProgramEntry(
                        DEFAULT_STATIC_LOC,
                        new ResourceLocation(BoneTown.MODID, "bonetown/shaders/default_static.vs"),
                        new ResourceLocation(BoneTown.MODID, "bonetown/shaders/default_static.fs")));
        event.getRegistry().register(
                new AnimatedShaderProgramEntry(
                    DEFAULT_ANIMATED_LOC,
                        new ResourceLocation(BoneTown.MODID, "bonetown/shaders/default_animated.vs"),
                        new ResourceLocation(BoneTown.MODID, "bonetown/shaders/default_animated.fs")));

    }
}
