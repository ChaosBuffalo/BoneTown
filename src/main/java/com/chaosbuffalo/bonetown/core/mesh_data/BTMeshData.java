package com.chaosbuffalo.bonetown.core.mesh_data;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.client.render.assimp.StaticMeshLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.io.IOUtils;
import org.lwjgl.system.MemoryUtil;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class BTMeshData implements IForgeRegistryEntry<BTMeshData> {
    private ResourceLocation location;
    private AssimpConstants.MeshTypes meshType;
    private AssimpMesh[] meshes;

    public BTMeshData(ResourceLocation name, AssimpConstants.MeshTypes meshType){
        setRegistryName(name);
        this.meshType = meshType;

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
            BoneTown.LOGGER.info("Buffer position is << {}", data.position());
            BoneTown.LOGGER.info("Buffer limit is << {}", data.limit());
            BoneTown.LOGGER.info("buffer remianing is << {}", data.remaining());
            BoneTown.LOGGER.info("Is nt? << {}", data.get(data.limit() - 1) == 0);
            try {
                this.meshes = StaticMeshLoader.load(data, name);
            } catch (Exception e) {
                e.printStackTrace();
            }
            MemoryUtil.memFree(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BTMeshData setRegistryName(ResourceLocation name) {
        location = name;
        return this;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return location;
    }

    @Override
    public Class<BTMeshData> getRegistryType() {
        return BTMeshData.class;
    }
}
