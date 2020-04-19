package com.chaosbuffalo.bonetown.client.render.render_data;

import com.chaosbuffalo.bonetown.core.model.BakedArmorMeshes;
import net.minecraft.inventory.EquipmentSlotType;

import java.util.HashMap;

public class BTArmorRenderData implements IBTRenderDataContainer {

    private int frameSinceLastRender;
    private boolean initialized;
    private final HashMap<EquipmentSlotType, BTAnimatedMeshRenderData> renderData;

    public BTArmorRenderData(BakedArmorMeshes bakedArmor){
        frameSinceLastRender = 0;
        this.initialized = false;
        this.renderData = new HashMap<>();
        renderData.put(EquipmentSlotType.HEAD, new BTAnimatedMeshRenderData(bakedArmor.getHead()));
        renderData.put(EquipmentSlotType.CHEST, new BTAnimatedMeshRenderData(bakedArmor.getBody()));
        renderData.put(EquipmentSlotType.LEGS, new BTAnimatedMeshRenderData(bakedArmor.getLegs()));
        renderData.put(EquipmentSlotType.FEET, new BTAnimatedMeshRenderData(bakedArmor.getFeet()));
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void incrementFrameCount() {
        frameSinceLastRender++;
    }

    @Override
    public int getFrameSinceLastRender() {
        return frameSinceLastRender;
    }

    public void renderSlot(EquipmentSlotType slotType){
        if (!initialized){
            return;
        }
        if (renderData.containsKey(slotType)){
            renderData.get(slotType).render();
            frameSinceLastRender = 0;
        }
    }

    @Override
    public void render() {

    }

    @Override
    public void cleanup() {
        if (!initialized){
            return;
        }
        for (BTAnimatedMeshRenderData data : renderData.values()){
            data.cleanup();
        }
        initialized = false;
    }

    @Override
    public void upload() {
        for (BTAnimatedMeshRenderData data : renderData.values()){
            data.upload();
        }
        frameSinceLastRender = 0;
        initialized = true;
    }
}
