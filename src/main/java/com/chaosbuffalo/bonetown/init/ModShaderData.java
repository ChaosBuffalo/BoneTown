package com.chaosbuffalo.bonetown.init;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.shaders.BTShaderProgramEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModShaderData {

    public static final ResourceLocation DEFAULT_SHADER_LOC = new ResourceLocation(BoneTown.MODID,
            "default_shader");


    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void registerShaders(RegistryEvent.Register<BTShaderProgramEntry> event) {
        BoneTown.LOGGER.info("Registering Bone Town Shader Data");
        event.getRegistry().register(
                new BTShaderProgramEntry(
                        DEFAULT_SHADER_LOC,
                        new ResourceLocation(BoneTown.MODID, "bone_town/shaders/default_vert.vs"),
                        new ResourceLocation(BoneTown.MODID, "bone_town/shaders/default_frag.fs")));

    }
}
