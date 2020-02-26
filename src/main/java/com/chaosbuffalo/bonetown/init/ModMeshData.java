package com.chaosbuffalo.bonetown.init;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.mesh_data.AssimpConstants;
import com.chaosbuffalo.bonetown.core.mesh_data.BTAnimatedModel;
import com.chaosbuffalo.bonetown.core.mesh_data.BTModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(BoneTown.MODID)
public class ModMeshData {

    @ObjectHolder("test_cube")
    public static BTModel TEST_CUBE;

    @ObjectHolder("spider")
    public static BTModel SPIDER;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void registerMeshData(RegistryEvent.Register<BTModel> event) {
        BoneTown.LOGGER.info("Registering Bone Town Mesh Data");
        event.getRegistry().register(new BTModel(
                new ResourceLocation(BoneTown.MODID, "test_cube"),
                AssimpConstants.MeshTypes.FBX));
        event.getRegistry().register(new BTAnimatedModel(
                new ResourceLocation(BoneTown.MODID, "spider"),
                AssimpConstants.MeshTypes.FBX));
    }
}
