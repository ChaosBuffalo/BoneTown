package com.chaosbuffalo.bonetown.init;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.animation.BTAdditionalAnimationEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class BTAnimations {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void registerAnimationData(RegistryEvent.Register<BTAdditionalAnimationEntry> event){

        event.getRegistry().register(new BTAdditionalAnimationEntry(
                new ResourceLocation(BoneTown.MODID, "biped.idle"),
                new ResourceLocation(BoneTown.MODID, "biped"),
                new ResourceLocation(BoneTown.MODID, "biped_idle")));
        event.getRegistry().register(new BTAdditionalAnimationEntry(
                new ResourceLocation(BoneTown.MODID, "biped.running"),
                new ResourceLocation(BoneTown.MODID, "biped"),
                new ResourceLocation(BoneTown.MODID, "biped_running")));
        event.getRegistry().register(new BTAdditionalAnimationEntry(
                new ResourceLocation(BoneTown.MODID, "biped.zombie_arms"),
                new ResourceLocation(BoneTown.MODID, "biped"),
                new ResourceLocation(BoneTown.MODID, "biped_zombie_arms")));
        event.getRegistry().register(new BTAdditionalAnimationEntry(
                new ResourceLocation(BoneTown.MODID, "biped.backflip"),
                new ResourceLocation(BoneTown.MODID, "biped"),
                new ResourceLocation(BoneTown.MODID, "biped_backflip")));

    }
}
