package com.chaosbuffalo.bonetown.client.render.entity;

import com.chaosbuffalo.bonetown.core.mesh_data.BTAnimatedMesh;
import com.chaosbuffalo.bonetown.core.mesh_data.BTAnimatedModel;
import net.minecraft.client.renderer.entity.EntityRendererManager;

import java.util.HashMap;
import java.util.Map;

public class BTAnimatedModelRenderData extends BTModelRenderData {

    private BTAnimatedModel animatedModel;

    public BTAnimatedModelRenderData(BTAnimatedModel modelIn, EntityRendererManager managerIn) {
        super(modelIn, managerIn);
        this.animatedModel = modelIn;
    }

    @Override
    public Map<String, IBTRenderData> getRenderData() {
        HashMap<String, IBTRenderData> ret = new HashMap<>();
        for (BTAnimatedMesh mesh : animatedModel.getAnimatedMeshes()){
            ret.put(mesh.name, new BTAnimatedMeshRenderData(mesh));
        }
        return ret;
    }
}
