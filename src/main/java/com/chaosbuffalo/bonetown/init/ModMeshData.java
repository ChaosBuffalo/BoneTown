package com.chaosbuffalo.bonetown.init;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.mesh_data.AssimpConstants;
import com.chaosbuffalo.bonetown.core.mesh_data.BTMeshData;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(BoneTown.ModId)
public class ModMeshData {

    @ObjectHolder("test_cube")
    public static BTMeshData TEST_CUBE;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void registerMeshData(RegistryEvent.Register<BTMeshData> event) {
        BoneTown.LOGGER.info("Registering Bone Town Mesh Data");
        event.getRegistry().register(new BTMeshData(
                new ResourceLocation(BoneTown.ModId, "test_cube"),
                AssimpConstants.MeshTypes.FBX));
    }
}
