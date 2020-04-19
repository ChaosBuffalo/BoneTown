package com.chaosbuffalo.bonetown.client.render;

import com.chaosbuffalo.bonetown.client.render.render_data.*;
import com.chaosbuffalo.bonetown.core.model.BTAnimatedModel;
import com.chaosbuffalo.bonetown.core.model.BTModel;
import com.chaosbuffalo.bonetown.core.model.BakedArmorMeshes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class RenderDataManager {
    public static final RenderDataManager MANAGER = new RenderDataManager();

    private final HashMap<RenderDataKey, IBTRenderDataContainer> renderData;

    public static final int FRAMES_BEFORE_CLEANUP = 6000;

    private class RenderDataKey {
        private ResourceLocation modelName;
        private boolean combined;

        public RenderDataKey(ResourceLocation name, boolean combined){
            this.modelName = name;
            this.combined = combined;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RenderDataKey that = (RenderDataKey) o;
            return combined == that.combined &&
                    modelName.equals(that.modelName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(modelName, combined);
        }
    }

    public RenderDataManager(){
        this.renderData = new HashMap<>();
    }

    public void tick(){
        for (IBTRenderDataContainer data : renderData.values()){
            if (data.isInitialized()) {
                data.incrementFrameCount();
                if (data.getFrameSinceLastRender() >= FRAMES_BEFORE_CLEANUP){
                    data.cleanup();
                }
            }
        }
    }

    public IBTRenderDataContainer getRenderDataForModel(BTModel model, boolean doCombined){
        RenderDataKey renderKey = new RenderDataKey(model.getRegistryName(), doCombined);
        if (!renderData.containsKey(renderKey)) {
            renderData.put(renderKey, new BTModelRenderData(model, doCombined));
        }
        return renderData.get(renderKey);
    }

    public IBTRenderDataContainer getAnimatedRenderDataForModel(BTAnimatedModel model,
                                                                boolean doCombined){
        RenderDataKey renderKey = new RenderDataKey(model.getRegistryName(), doCombined);
        if (!renderData.containsKey(renderKey)) {
            renderData.put(renderKey, new BTAnimatedModelRenderData(model, doCombined));
        }
        return renderData.get(renderKey);
    }

    public BTArmorRenderData getArmorRenderDataForModel(BakedArmorMeshes armorMeshes){
        RenderDataKey renderKey = new RenderDataKey(armorMeshes.getName(), false);
        if (!renderData.containsKey(renderKey)) {
            renderData.put(renderKey, new BTArmorRenderData(armorMeshes));
        }
        return (BTArmorRenderData) renderData.get(renderKey);
    }

}
