package com.chaosbuffalo.bonetown.client.render.assimp;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.Utils;
import com.chaosbuffalo.bonetown.core.mesh_data.AssimpConstants;
import com.chaosbuffalo.bonetown.core.mesh_data.AssimpMaterial;
import com.chaosbuffalo.bonetown.core.mesh_data.AssimpMesh;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;

import static org.lwjgl.assimp.Assimp.*;


public class StaticMeshLoader {

    public static AssimpMesh[] load(ByteBuffer resource, ResourceLocation name) throws Exception {
        return load(resource, name,aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices |
                aiProcess_Triangulate | aiProcess_FixInfacingNormals);
    }

    public static AssimpMesh[] load(ByteBuffer resource, ResourceLocation name, int flags) throws Exception {
        byte[] stringBytes="fbx".getBytes("ISO-8859-1");
        byte[] ntBytes=new byte[stringBytes.length+1];
        System.arraycopy(stringBytes, 0, ntBytes, 0, stringBytes.length);
        AIScene aiScene = aiImportFileFromMemory(resource, flags, ByteBuffer.wrap(ntBytes));
        if (aiScene == null) {
            throw new Exception("Error loading model: " + aiGetErrorString());
        }
        ResourceLocation textureLocation = new ResourceLocation(name.getNamespace(),
                AssimpConstants.ASSIMP_TEXTURES_DIR +
                        "/" + name.getPath() + ".png");
        int numMaterials = aiScene.mNumMaterials();
        PointerBuffer aiMaterials = aiScene.mMaterials();
        List<AssimpMaterial> materials = new ArrayList<>();
        for (int i = 0; i < numMaterials; i++) {
            AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
            processMaterial(aiMaterial, materials, textureLocation);
        }

        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        AssimpMesh[] meshes = new AssimpMesh[numMeshes];
        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            AssimpMesh mesh = processMesh(aiMesh, materials);
            meshes[i] = mesh;
        }
        BoneTown.LOGGER.info("Loaded  << {} meshes", numMeshes);

        return meshes;
    }

    protected static void processIndices(AIMesh aiMesh, List<Integer> indices) {
        int numFaces = aiMesh.mNumFaces();
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        for (int i = 0; i < numFaces; i++) {
            AIFace aiFace = aiFaces.get(i);
            IntBuffer buffer = aiFace.mIndices();
            while (buffer.remaining() > 0) {
                indices.add(buffer.get());
            }
        }
    }

    protected static void processMaterial(AIMaterial aiMaterial, List<AssimpMaterial> materials,
                                          ResourceLocation baseTexture) throws Exception {
        AIColor4D colour = AIColor4D.create();

        AIString path = AIString.calloc();
        Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, path, (IntBuffer) null,
                null, null, null, null, null);
        String textPath = path.dataString();
        Texture texture = null;

        if (textPath != null && textPath.length() > 0) {
            BoneTown.LOGGER.info(String.format("File for texture is: %s", textPath));
        }

        Vector4f diffuse = AssimpMaterial.DEFAULT_COLOUR;
        int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0,
                colour);
        if (result == 0) {
            diffuse = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
        }

        Vector4f specular = AssimpMaterial.DEFAULT_COLOUR;
        result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0,
                colour);
        if (result == 0) {
            specular = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
        }

        AssimpMaterial material = new AssimpMaterial(diffuse, specular, 1.0f);
        material.setTexture(baseTexture);
        materials.add(material);
    }

    private static AssimpMesh processMesh(AIMesh aiMesh, List<AssimpMaterial> materials) {
        List<Float> vertices = new ArrayList<>();
        List<Float> textures = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        processVertices(aiMesh, vertices);
        processNormals(aiMesh, normals);
        processTextCoords(aiMesh, textures);
        processIndices(aiMesh, indices);

        AssimpMesh mesh = new AssimpMesh(Utils.listToArray(vertices), Utils.listToArray(textures),
                Utils.listToArray(normals), Utils.listIntToArray(indices));
        AssimpMaterial material;
        int materialIdx = aiMesh.mMaterialIndex();
        if (materialIdx >= 0 && materialIdx < materials.size()) {
            material = materials.get(materialIdx);
        } else {
            material = new AssimpMaterial();
        }
        mesh.setMaterial(material);
        BoneTown.LOGGER.info("Loaded  << {} vertices", vertices.size());
        BoneTown.LOGGER.info("Loaded  << {} indices", indices.size());
        BoneTown.LOGGER.info("Loaded  << {} normals", normals.size());
        BoneTown.LOGGER.info("Loaded  << {} textures", textures.size());
        return mesh;
    }

    protected static void processNormals(AIMesh aiMesh, List<Float> normals) {
        AIVector3D.Buffer aiNormals = aiMesh.mNormals();
        while (aiNormals != null && aiNormals.remaining() > 0) {
            AIVector3D aiNormal = aiNormals.get();
            normals.add(aiNormal.x());
            normals.add(aiNormal.y());
            normals.add(aiNormal.z());
        }
    }

    protected static void processTextCoords(AIMesh aiMesh, List<Float> textures) {
        AIVector3D.Buffer textCoords = aiMesh.mTextureCoords(0);
        int numTextCoords = textCoords != null ? textCoords.remaining() : 0;
        for (int i = 0; i < numTextCoords; i++) {
            AIVector3D textCoord = textCoords.get();
            textures.add(textCoord.x());
            textures.add(1 - textCoord.y());
        }
    }

    protected static void processVertices(AIMesh aiMesh, List<Float> vertices) {
        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        while (aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();
            vertices.add(aiVertex.x());
            vertices.add(aiVertex.y());
            vertices.add(aiVertex.z());
        }
    }
}