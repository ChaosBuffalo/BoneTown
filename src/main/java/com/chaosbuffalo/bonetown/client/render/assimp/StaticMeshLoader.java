package com.chaosbuffalo.bonetown.client.render.assimp;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.Utils;
import com.chaosbuffalo.bonetown.core.mesh_data.BTMesh;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;

import static org.lwjgl.assimp.Assimp.*;


public class StaticMeshLoader {

    public static BTMesh[] load(ByteBuffer resource, ResourceLocation name) throws Exception {
        return load(resource, name,aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices |
                aiProcess_Triangulate | aiProcess_FixInfacingNormals);
    }

    public static BTMesh[] load(ByteBuffer resource, ResourceLocation name, int flags) throws Exception {
        byte[] stringBytes="fbx".getBytes("ISO-8859-1");
        byte[] ntBytes=new byte[stringBytes.length+1];
        System.arraycopy(stringBytes, 0, ntBytes, 0, stringBytes.length);
        AIScene aiScene = aiImportFileFromMemory(resource, flags, ByteBuffer.wrap(ntBytes));
        if (aiScene == null) {
            throw new Exception("Error loading model: " + aiGetErrorString());
        }

        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        BTMesh[] meshes = new BTMesh[numMeshes];
        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            BTMesh mesh = processMesh(aiMesh);
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


    private static BTMesh processMesh(AIMesh aiMesh) {
        BoneTown.LOGGER.info("Loading Mesh << {}", aiMesh.mName().toString());
        List<Float> vertices = new ArrayList<>();
        List<Float> textures = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        processVertices(aiMesh, vertices);
        processNormals(aiMesh, normals);
        processTextCoords(aiMesh, textures);
        processIndices(aiMesh, indices);


        BTMesh mesh = new BTMesh(Utils.listToArray(vertices), Utils.listToArray(textures),
                Utils.listToArray(normals), Utils.listIntToArray(indices), aiMesh.mName().toString());
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
            BoneTown.LOGGER.info("vert: {}, tex coord: {}, {}", i, textCoord.x(), 1 - textCoord.y());
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