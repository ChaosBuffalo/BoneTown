package com.chaosbuffalo.bonetown.client.render.render_data;

import com.chaosbuffalo.bonetown.core.model.BakedMesh;
import com.chaosbuffalo.bonetown.core.model.BTModel;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


import java.util.HashMap;
import java.util.Map;


@OnlyIn(Dist.CLIENT)
public class BTModelRenderData {
    private BTModel model;
    protected EntityRendererManager manager;
    private boolean initialized;

    private Map<String, IBTRenderData> meshData;

    public BTModelRenderData(BTModel modelIn, EntityRendererManager managerIn){
        model = modelIn;
        manager = managerIn;
        initialized = false;
    }

    public Map<String, IBTRenderData> getRenderData() {
        HashMap<String, IBTRenderData> ret = new HashMap<>();
        for (BakedMesh mesh : model.getMeshes()){
            ret.put(mesh.name, new BTMeshRenderData(mesh));
        }
        return ret;
    }


    public void GLinit(){
        meshData = getRenderData();
        for (IBTRenderData data : meshData.values()){
            data.upload();
        }
        initialized = true;
    }

    public boolean isInitialized(){
        return initialized;
    }

    public void GLclear(){
        for (IBTRenderData meshRenderData : meshData.values()){
            meshRenderData.cleanup();
        }
        meshData.clear();
        initialized = false;
    }

    public void render(){
        if (!initialized){
            return;
        }
        for (IBTRenderData meshRenderData : meshData.values()){
            meshRenderData.render();
        }
    }
}
