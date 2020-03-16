package com.chaosbuffalo.bonetown.core;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.animation.BTAdditionalAnimationEntry;
import com.chaosbuffalo.bonetown.core.model.BTModel;
import com.chaosbuffalo.bonetown.core.shaders.BTShaderProgramEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class BoneTownRegistry {

    public static IForgeRegistry<BTModel> MODEL_REGISTRY = null;
    public static IForgeRegistry<BTShaderProgramEntry> SHADER_REGISTRY = null;
    public static IForgeRegistry<BTAdditionalAnimationEntry> ADDITIONAL_ANIMATION_REGISTRY = null;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void createRegistries(RegistryEvent.NewRegistry event) {
        BoneTown.LOGGER.info("Registering Bone Town Registries");
        MODEL_REGISTRY = new RegistryBuilder<BTModel>()
                .setName(new ResourceLocation(BoneTown.MODID, "models"))
                .setType(BTModel.class)
                .setIDRange(0, Integer.MAX_VALUE - 1)
                .allowModification()
                .create();
        SHADER_REGISTRY = new RegistryBuilder<BTShaderProgramEntry>()
                .setName(new ResourceLocation(BoneTown.MODID, "shaders"))
                .setType(BTShaderProgramEntry.class)
                .setIDRange(0, Integer.MAX_VALUE - 1)
                .allowModification()
                .create();
        ADDITIONAL_ANIMATION_REGISTRY = new RegistryBuilder<BTAdditionalAnimationEntry>()
                .setName(new ResourceLocation(BoneTown.MODID, "z_add_animations"))
                .setType(BTAdditionalAnimationEntry.class)
                .setIDRange(0, Integer.MAX_VALUE - 1)
                .allowModification()
                .create();
    }
}
