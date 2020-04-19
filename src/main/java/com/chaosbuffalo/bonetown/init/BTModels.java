package com.chaosbuffalo.bonetown.init;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.BoneTownConstants;
import com.chaosbuffalo.bonetown.core.model.BTAnimatedModel;
import com.chaosbuffalo.bonetown.core.model.BTArmorModelEntry;
import com.chaosbuffalo.bonetown.core.model.BTModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;
import java.util.Arrays;

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

    @ObjectHolder("biped_t")
    public static BTModel BIPED2;

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
                new ResourceLocation(BoneTown.MODID, "biped_t"),
                BoneTownConstants.MeshTypes.BONEMF));
        event.getRegistry().register(new BTModel(
                new ResourceLocation(BoneTown.MODID, "bone_display"),
                BoneTownConstants.MeshTypes.BONEMF));
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void registerArmorModels(RegistryEvent.Register<BTArmorModelEntry> event) {
        ArrayList<String> legs = new ArrayList<>();
        legs.add("biped_lower_armor_body");
        legs.add("biped_lower_armor_leg_upper_l");
        legs.add("biped_lower_armor_leg_upper_r");
        legs.add("biped_lower_armor_leg_lower_l");
        legs.add("biped_lower_armor_leg_lower_r");
        ArrayList<String> body = new ArrayList<>();
        body.add("biped_upper_armor_body");
        body.add("biped_upper_armor_arm_upper_l");
        body.add("biped_upper_armor_arm_upper_r");
        body.add("biped_upper_armor_arm_lower_l");
        body.add("biped_upper_armor_arm_lower_r");
        ArrayList<String> head = new ArrayList<>();
        head.add("biped_upper_armor_head");
        ArrayList<String> feet = new ArrayList<>();
        feet.add("biped_upper_armor_leg_lower_r");
        feet.add("biped_upper_armor_leg_lower_l");
        event.getRegistry().register(new BTArmorModelEntry(new ResourceLocation(BoneTown.MODID,
                "biped.default_armor"), new ResourceLocation(BoneTown.MODID, "biped"),
                new ResourceLocation(BoneTown.MODID, "biped_armor"), head, body, legs, feet));
    }
}
