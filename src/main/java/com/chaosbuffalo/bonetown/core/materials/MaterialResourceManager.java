package com.chaosbuffalo.bonetown.core.materials;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.BoneTownRegistry;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.client.shader.ShaderLoader;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.resource.VanillaResourceType;

import javax.annotation.Nullable;
import java.io.*;
import java.util.HashMap;
import java.util.OptionalInt;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class MaterialResourceManager implements ISelectiveResourceReloadListener {

    public static final MaterialResourceManager INSTANCE = new MaterialResourceManager();

    private IResourceManager manager;
    private HashMap<ResourceLocation, IBTMaterial> programCache = new HashMap<>();


    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        BoneTown.LOGGER.info("In resource manager reload for shader manager");
        if (resourcePredicate.test(getResourceType()))
        {
            clearProgramCache();
            onResourceManagerReload(resourceManager);
        }
    }

    private void clearProgramCache(){
        programCache.values().forEach(ShaderLinkHelper::deleteShader);
        programCache.clear();
    }

    public OptionalInt getProgramId(ResourceLocation location) {
        IBTMaterial prog = programCache.get(location);
        return prog == null ? OptionalInt.empty() : OptionalInt.of(prog.getProgram());
    }


    public IBTMaterial getShaderProgram(ResourceLocation location){
        return programCache.get(location);
    }


    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        BoneTown.LOGGER.info("Reloading Shaders");
        this.manager = resourceManager;
        for (BTMaterialEntry entry : BoneTownRegistry.MATERIAL_REGISTRY.getValues()){
            loadProgram(entry);
        }
    }

    private void loadProgram(BTMaterialEntry program){
        try {
            ShaderLoader vert = createShader(manager, program.getVertexShader(), ShaderLoader.ShaderType.VERTEX);
            ShaderLoader frag = createShader(manager, program.getFragShader(), ShaderLoader.ShaderType.FRAGMENT);
            int progId = ShaderLinkHelper.createProgram();
            IBTMaterial prog = program.getProgram(progId, vert, frag);
            ShaderLinkHelper.linkProgram(prog);
            prog.setupUniforms();
            programCache.put(program.getRegistryName(), prog);
        } catch (IOException ex) {
            BoneTown.LOGGER.error("Failed to load program {}",
                    program.getRegistryName().toString(), ex);
        }
    }

    @Nullable
    @Override
    public IResourceType getResourceType() {
        return VanillaResourceType.SHADERS;
    }

    private static ShaderLoader createShader(IResourceManager manager, ResourceLocation location,
                                             ShaderLoader.ShaderType shaderType) throws IOException {
        BoneTown.LOGGER.info("Trying to create shader: {}", location.toString());
        try (InputStream is = new BufferedInputStream(manager.getResource(location).getInputStream())) {
            return ShaderLoader.func_216534_a(shaderType, location.toString(), is);
        }
    }
}
