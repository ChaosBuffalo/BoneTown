package com.chaosbuffalo.bonetown.init;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.shaders.BTShaderProgramEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(BoneTown.MODID)
public class ModShaderData {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void registerMeshData(RegistryEvent.Register<BTShaderProgramEntry> event) {
        BoneTown.LOGGER.info("Registering Bone Town Shader Data");
        event.getRegistry().register(
                new BTShaderProgramEntry(
                        new ResourceLocation(BoneTown.MODID, "test_prog"),
                        new ResourceLocation(BoneTown.MODID, "bone_town/shaders/test_vert.vs"),
                        new ResourceLocation(BoneTown.MODID, "bone_town/shaders/test_frag.fs")));

    }
}
