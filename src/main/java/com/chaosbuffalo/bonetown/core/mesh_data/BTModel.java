package com.chaosbuffalo.bonetown.core.mesh_data;

import com.chaosbuffalo.bonetown.core.assimp.AssimpMeshLoader;
import com.chaosbuffalo.bonetown.init.ModShaderData;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.io.IOUtils;
import org.lwjgl.system.MemoryUtil;



import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class BTModel implements IForgeRegistryEntry<BTModel> {
    private ResourceLocation location;
    private ResourceLocation programName;
    public final AssimpConstants.MeshTypes meshType;
    private BTMesh[] meshes;

    public BTModel(ResourceLocation name, AssimpConstants.MeshTypes meshType){
        this(name, ModShaderData.DEFAULT_STATIC_LOC, meshType);
    }


    public BTModel(ResourceLocation name, ResourceLocation programName,
                   AssimpConstants.MeshTypes meshType){
        setRegistryName(name);
        this.meshType = meshType;
        this.programName = programName;
    }

    public BTMesh[] getMeshes(){
        return meshes;
    }

    public void load(){
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
                this.meshes = AssimpMeshLoader.load(data, name);
            } catch (Exception e) {
                e.printStackTrace();
            }
            MemoryUtil.memFree(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BTModel setRegistryName(ResourceLocation name) {
        location = name;
        return this;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return location;
    }

    @Override
    public Class<BTModel> getRegistryType() {
        return BTModel.class;
    }

    public ResourceLocation getProgramName(){ return programName; }
}
