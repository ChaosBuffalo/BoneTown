package com.chaosbuffalo.bonetown.core.proxy;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.shaders.BTShaderResourceManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerHandlers() {
        super.registerHandlers();
        BoneTown.LOGGER.info("Registering client handlers");
        ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager())
                .addReloadListener(BTShaderResourceManager.INSTANCE);
    }
}
