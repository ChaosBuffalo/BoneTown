package com.chaosbuffalo.bonetown.core;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.mesh_data.BTMeshData;
import com.chaosbuffalo.bonetown.core.shaders.BTShaderProgramEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class BoneTownRegistry {

    public static IForgeRegistry<BTMeshData> MESH_REGISTRY  = null;
    public static IForgeRegistry<BTShaderProgramEntry> SHADER_REGISTRY = null;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void createRegistries(RegistryEvent.NewRegistry event) {
        BoneTown.LOGGER.info("Registering Bone Town Registries");
        MESH_REGISTRY = new RegistryBuilder<BTMeshData>()
                .setName(new ResourceLocation(BoneTown.MODID, "models"))
                .setType(BTMeshData.class)
                .setIDRange(0, Integer.MAX_VALUE - 1)
                .allowModification()
                .create();
        SHADER_REGISTRY = new RegistryBuilder<BTShaderProgramEntry>()
                .setName(new ResourceLocation(BoneTown.MODID, "shaders"))
                .setType(BTShaderProgramEntry.class)
                .setIDRange(0, Integer.MAX_VALUE - 1)
                .allowModification()
                .create();
    }
}
