package com.chaosbuffalo.bonetown.init;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.BoneTownConstants;
import com.chaosbuffalo.bonetown.core.model.BTAnimatedModel;
import com.chaosbuffalo.bonetown.core.model.BTModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(BoneTown.MODID)
public class BTModels {
//
    @ObjectHolder("test_cube")
    public static BTModel TEST_CUBE;

//    @ObjectHolder("spider")
//    public static BTModel SPIDER;

    @ObjectHolder("biped")
    public static BTModel BIPED;

    @ObjectHolder("bone_display")
    public static BTModel BONE_DISPLAY;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void registerModels(RegistryEvent.Register<BTModel> event) {
        BoneTown.LOGGER.info("Registering Bone Town Mesh Data");
        event.getRegistry().register(new BTModel(
                new ResourceLocation(BoneTown.MODID, "test_cube"),
                BoneTownConstants.MeshTypes.BONEMF));
//        event.getRegistry().register(new BTAnimatedModel(
//                new ResourceLocation(BoneTown.MODID, "spider"),
//                BoneTownConstants.MeshTypes.FBX));
        event.getRegistry().register(new BTAnimatedModel(
                new ResourceLocation(BoneTown.MODID, "biped"),
                BoneTownConstants.MeshTypes.BONEMF));
        event.getRegistry().register(new BTModel(
                new ResourceLocation(BoneTown.MODID, "bone_display"),
                BoneTownConstants.MeshTypes.BONEMF));
    }
}
