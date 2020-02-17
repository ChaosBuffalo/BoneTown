package com.chaosbuffalo.bonetown.client.render;


import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.shaders.BTShaderResourceManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE)
public class RenderEventListener {


    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void cameraSetup(EntityViewRenderEvent.CameraSetup event){
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(event.getRoll()));
        matrixStack.rotate(Vector3f.XP.rotationDegrees(event.getPitch()));
        matrixStack.rotate(Vector3f.YP.rotationDegrees(event.getYaw() + 180.0F));
        GlobalRenderInfo.INFO.setCurrentFrameGlobal(matrixStack);
    }
}
