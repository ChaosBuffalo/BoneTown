package com.chaosbuffalo.bonetown.core.model;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.BoneTownConstants;
import com.chaosbuffalo.bonetown.core.bonemf.BoneMFModelLoader;
import com.chaosbuffalo.bonetown.core.bonemf.BoneMFSkeleton;
import com.chaosbuffalo.bonetown.init.BTMaterials;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Optional;


public class BTAnimatedModel extends BTModel {

    private BakedAnimatedMesh[] animatedMeshes;

    public BTAnimatedModel(ResourceLocation name, ResourceLocation programName,
                           BoneTownConstants.MeshTypes meshType) {
        super(name, programName, meshType);
    }

    public BTAnimatedModel(ResourceLocation name, BoneTownConstants.MeshTypes meshType){
        this(name, BTMaterials.DEFAULT_ANIMATED_LOC, meshType);
    }

    @Override
    public BakedMesh[] getMeshes(){
        return animatedMeshes;
    }

    public Optional<BoneMFSkeleton> getSkeleton(){
        return this.model.getSkeleton();
    }

    public BakedAnimatedMesh[] getAnimatedMeshes() { return animatedMeshes; }


    @Override
    public void load() {
        String meshExt = BoneTownConstants.stringFromMeshType(meshType);
        ResourceLocation name = getRegistryName();
        ResourceLocation meshLocation = new ResourceLocation(name.getNamespace(),
                BoneTownConstants.BONETOWN_MODELS_DIR +
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
                model = BoneMFModelLoader.load(data, name);
                this.animatedMeshes = model.getBakeAsAnimatedMeshes()
                        .toArray(new BakedAnimatedMesh[0]);
                BoneTown.LOGGER.info("Loaded {} animated meshes for {}", animatedMeshes.length, name.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            MemoryUtil.memFree(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
