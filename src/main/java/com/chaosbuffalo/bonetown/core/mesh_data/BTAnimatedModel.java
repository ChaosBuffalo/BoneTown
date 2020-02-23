package com.chaosbuffalo.bonetown.core.mesh_data;

import com.chaosbuffalo.bonetown.core.animation_data.BTSkeleton;
import com.chaosbuffalo.bonetown.core.assimp.AssimpMeshLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class BTAnimatedModel extends BTModel {

    private BTAnimatedMesh[] animatedMeshes;
    private BTSkeleton skeleton;

    public BTAnimatedModel(ResourceLocation name, ResourceLocation programName,
                           AssimpConstants.MeshTypes meshType) {
        super(name, programName, meshType);
    }

    @Override
    public BTMesh[] getMeshes(){
        return animatedMeshes;
    }

    public BTSkeleton getSkeleton(){
        return skeleton;
    }

    public BTAnimatedMesh[] getAnimatedMeshes() { return animatedMeshes; }

    @Override
    public void load() {
        String meshExt = AssimpConstants.stringFromMeshType(meshType);
        ResourceLocation name = getRegistryName();
        ResourceLocation meshLocation = new ResourceLocation(name.getNamespace(),
                AssimpConstants.ASSIMP_MODELS_DIR +
                        "/" + name.getPath() + "." + meshExt);
        try {
            InputStream stream = Minecraft.getInstance().getResourceManager()
                    .getResource(meshLocation)
                    .getInputStream();
            byte[] _data = IOUtils.toByteArray(stream);
            ByteBuffer data = MemoryUtil.memCalloc(_data.length + 1);
            data.put(_data);
            data.put((byte) 0);
            data.flip();
            stream.close();
            try {
                AssimpMeshLoader.LoadAnimatedReturn ret = AssimpMeshLoader.loadAnimated(data, name);
                this.animatedMeshes = ret.meshes;
                this.skeleton = ret.skeleton;
            } catch (Exception e) {
                e.printStackTrace();
            }
            MemoryUtil.memFree(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
