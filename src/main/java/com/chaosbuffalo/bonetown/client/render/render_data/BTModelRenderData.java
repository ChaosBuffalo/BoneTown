package com.chaosbuffalo.bonetown.client.render.render_data;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.model.BakedMesh;
import com.chaosbuffalo.bonetown.core.model.BTModel;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@OnlyIn(Dist.CLIENT)
public class BTModelRenderData implements IBTRenderDataContainer {
    private final BTModel model;
    private boolean initialized;
    protected final boolean doCombined;
    private int frameSinceLastRender;

    private Map<String, IBTRenderData> meshData;

    public BTModelRenderData(BTModel modelIn, boolean doCombined){
        model = modelIn;
        initialized = false;
        meshData = new HashMap<>();
        this.doCombined = doCombined;
        frameSinceLastRender = 0;
    }

    public Map<String, IBTRenderData> getRenderData() {
        HashMap<String, IBTRenderData> ret = new HashMap<>();
        if (doCombined){
            BakedMesh mesh = model.getCombinedMesh();
            ret.put(mesh.name, new BTMeshRenderData(mesh));
        } else {
            for (BakedMesh mesh : model.getMeshes()){
                ret.put(mesh.name, new BTMeshRenderData(mesh));
            }
        }
        return ret;
    }

    public void renderSubset(Set<String> toRender){
        if (!initialized){
            return;
        }
        for (String key : toRender){
            IBTRenderData data = meshData.get(key);
            if(data != null){
                data.render();
            }
        }
    }


    @Override
    public void upload(){
        meshData = getRenderData();
        for (IBTRenderData data : meshData.values()){
            data.upload();
        }
        frameSinceLastRender = 0;
        initialized = true;
    }

    @Override
    public boolean isInitialized(){
        return initialized;
    }

    @Override
    public void cleanup(){
        if (!initialized){
            return;
        }
        for (IBTRenderData meshRenderData : meshData.values()){
            meshRenderData.cleanup();
        }
        meshData.clear();
        initialized = false;
    }

    @Override
    public void incrementFrameCount(){
        frameSinceLastRender++;
    }

    @Override
    public int getFrameSinceLastRender() {
        return frameSinceLastRender;
    }

    @Override
    public void render(){
        if (!initialized){
            return;
        }
        frameSinceLastRender = 0;
        for (IBTRenderData meshRenderData : meshData.values()){
            meshRenderData.render();
        }
    }
}
