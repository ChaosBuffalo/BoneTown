package com.chaosbuffalo.bonetown.core.assimp;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.Utils;
import com.chaosbuffalo.bonetown.core.animation_data.*;
import com.chaosbuffalo.bonetown.core.mesh_data.BTAnimatedMesh;
import com.chaosbuffalo.bonetown.core.mesh_data.BTMesh;
import net.minecraft.util.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import static org.lwjgl.assimp.Assimp.*;


public class AssimpMeshLoader {

    public static BTMesh[] load(ByteBuffer resource, ResourceLocation name) throws Exception {
        return load(resource, name, "fbx",
                aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices |
                aiProcess_Triangulate | aiProcess_FixInfacingNormals);
    }

    public static LoadAnimatedReturn loadAnimated(ByteBuffer resource, ResourceLocation name)
            throws Exception {
        return loadAnimated(resource, name, "fbx",
                aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices |
                        aiProcess_Triangulate | aiProcess_FixInfacingNormals |
                        aiProcess_LimitBoneWeights);
    }

    public static class LoadAnimatedReturn {

        public BTAnimatedMesh[] meshes;
        public BTSkeleton skeleton;

        public LoadAnimatedReturn(BTAnimatedMesh[] meshes, BTSkeleton skeleton){
            this.meshes = meshes;
            this.skeleton = skeleton;
        }

    }

    public static LoadAnimatedReturn loadAnimated(ByteBuffer resource, ResourceLocation name,
                                                  String fileExt, int flags) throws Exception {
        byte[] stringBytes=fileExt.getBytes("ISO-8859-1");
        byte[] ntBytes=new byte[stringBytes.length+1];
        System.arraycopy(stringBytes, 0, ntBytes, 0, stringBytes.length);
        AIScene aiScene = aiImportFileFromMemory(resource, flags, ByteBuffer.wrap(ntBytes));
        if (aiScene == null) {
            throw new Exception("Error loading model: " + aiGetErrorString());
        }

        List<BTBone> boneList = new ArrayList<>();
        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        BTAnimatedMesh[] meshes = new BTAnimatedMesh[numMeshes];
        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            BTAnimatedMesh mesh = processAnimatedMesh(aiMesh, boneList);
            meshes[i] = mesh;
        }

        BoneTown.LOGGER.info("Loaded  << {} meshes", numMeshes);
        AINode aiRootNode = aiScene.mRootNode();
        Matrix4f rootTransfromation = toMatrix(aiRootNode.mTransformation());
        BTNode rootNode = processNodesHierarchy(aiRootNode, null);
        HashMap<String, BTAnimation> animations = processAnimations(aiScene, boneList, rootNode, rootTransfromation);
        BTSkeleton skeleton = new BTSkeleton(boneList, animations, rootNode);
        return new LoadAnimatedReturn(meshes, skeleton);
    }

    private static HashMap<String, BTAnimation> processAnimations(AIScene aiScene,
                                                                  List<BTBone> boneList,
                                                                  BTNode rootNode,
                                                                  Matrix4f rootTransformation) {
        HashMap<String, BTAnimation> animations = new HashMap<>();

        // Process all animations
        int numAnimations = aiScene.mNumAnimations();
        PointerBuffer aiAnimations = aiScene.mAnimations();
        for (int i = 0; i < numAnimations; i++) {
            AIAnimation aiAnimation = AIAnimation.create(aiAnimations.get(i));

            // Calculate transformation matrices for each node
            int numChanels = aiAnimation.mNumChannels();
            PointerBuffer aiChannels = aiAnimation.mChannels();
            for (int j = 0; j < numChanels; j++) {
                AINodeAnim aiNodeAnim = AINodeAnim.create(aiChannels.get(j));
                String nodeName = aiNodeAnim.mNodeName().dataString();
                BTNode node = rootNode.findByName(nodeName);
                buildTransFormationMatrices(aiNodeAnim, node);
            }


            List<AnimationFrame> frames = buildAnimationFrames(boneList, rootNode, rootTransformation);
            BTAnimation animation = new BTAnimation(
                    aiAnimation.mName().dataString(), frames, aiAnimation.mDuration());
            BoneTown.LOGGER.info("Loading Animation: {} with {} frames", animation.getName(),
                    animation.getFrameCount());
            animations.put(animation.getName(), animation);
        }
        return animations;
    }

    private static List<AnimationFrame> buildAnimationFrames(List<BTBone> boneList, BTNode rootNode,
                                                            Matrix4f rootTransformation) {

        int numFrames = rootNode.getAnimationFrames();
        List<AnimationFrame> frameList = new ArrayList<>();
        for (int i = 0; i < numFrames; i++) {
            AnimationFrame frame = new AnimationFrame();
            frameList.add(frame);

            int numBones = boneList.size();
            for (int j = 0; j < numBones; j++) {
                BTBone bone = boneList.get(j);
                BTNode node = rootNode.findByName(bone.getBoneName());
                Matrix4f boneMatrix = BTNode.getParentTransforms(node, i);
                boneMatrix.mul(bone.getOffsetMatrix());
                boneMatrix = new Matrix4f(rootTransformation).mul(boneMatrix);
                frame.setMatrix(j, boneMatrix);
            }
        }

        return frameList;
    }

    private static void buildTransFormationMatrices(AINodeAnim aiNodeAnim, BTNode node) {
        int numFrames = aiNodeAnim.mNumPositionKeys();
        AIVectorKey.Buffer positionKeys = aiNodeAnim.mPositionKeys();
        AIVectorKey.Buffer scalingKeys = aiNodeAnim.mScalingKeys();
        AIQuatKey.Buffer rotationKeys = aiNodeAnim.mRotationKeys();

        for (int i = 0; i < numFrames; i++) {
            AIVectorKey aiVecKey = positionKeys.get(i);
            AIVector3D vec = aiVecKey.mValue();

            Matrix4f transfMat = new Matrix4f().translate(vec.x(), vec.y(), vec.z());

            AIQuatKey quatKey = rotationKeys.get(i);
            AIQuaternion aiQuat = quatKey.mValue();
            Quaternionf quat = new Quaternionf(aiQuat.x(), aiQuat.y(), aiQuat.z(), aiQuat.w());
            transfMat.rotate(quat);

            if (i < aiNodeAnim.mNumScalingKeys()) {
                aiVecKey = scalingKeys.get(i);
                vec = aiVecKey.mValue();
                transfMat.scale(vec.x(), vec.y(), vec.z());
            }

            node.addTransformation(transfMat);
        }
    }

    private static BTAnimatedMesh processAnimatedMesh(AIMesh aiMesh, List<BTBone> bones) {
        BoneTown.LOGGER.info("Loading Animated Mesh << {}", aiMesh.mName().toString());
        List<Float> vertices = new ArrayList<>();
        List<Float> textures = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        List<Float> weights = new ArrayList<>();
        List<Integer> boneIds = new ArrayList<>();

        processVertices(aiMesh, vertices);
        processNormals(aiMesh, normals);
        processTextCoords(aiMesh, textures);
        processIndices(aiMesh, indices);
        processBones(aiMesh, bones, boneIds, weights);


        BTAnimatedMesh mesh = new BTAnimatedMesh(
                aiMesh.mName().toString(),
                Utils.listToArray(vertices),
                Utils.listToArray(textures),
                Utils.listToArray(normals),
                Utils.listIntToArray(indices),
                Utils.listIntToArray(boneIds),
                Utils.listToArray(weights));
        BoneTown.LOGGER.info("Loaded  << {} vertices", vertices.size());
        BoneTown.LOGGER.info("Loaded  << {} indices", indices.size());
        BoneTown.LOGGER.info("Loaded  << {} normals", normals.size());
        BoneTown.LOGGER.info("Loaded  << {} textures", textures.size());
        BoneTown.LOGGER.info("Loaded  << {} bone ids", boneIds.size());
        BoneTown.LOGGER.info("Loaded  << {} weights", weights.size());
        return mesh;
    }

    protected static Matrix4f toMatrix(AIMatrix4x4 matIn){
        Matrix4f result = new Matrix4f();
        result.m00(matIn.a1());
        result.m10(matIn.a2());
        result.m20(matIn.a3());
        result.m30(matIn.a4());
        result.m01(matIn.b1());
        result.m11(matIn.b2());
        result.m21(matIn.b3());
        result.m31(matIn.b4());
        result.m02(matIn.c1());
        result.m12(matIn.c2());
        result.m22(matIn.c3());
        result.m32(matIn.c4());
        result.m03(matIn.d1());
        result.m13(matIn.d2());
        result.m23(matIn.d3());
        result.m33(matIn.d4());
        return result;
    }

    private static BTNode processNodesHierarchy(AINode aiNode, BTNode parentNode) {
        String nodeName = aiNode.mName().dataString();
        BTNode node = new BTNode(nodeName, parentNode);

        int numChildren = aiNode.mNumChildren();
        PointerBuffer aiChildren = aiNode.mChildren();
        for (int i = 0; i < numChildren; i++) {
            AINode aiChildNode = AINode.create(aiChildren.get(i));
            BTNode childNode = processNodesHierarchy(aiChildNode, node);
            node.addChild(childNode);
        }

        return node;
    }

    protected static void processBones(AIMesh aiMesh, List<BTBone> boneList,
                                       List<Integer> boneIds, List<Float> weights) {
        HashMap<Integer, List<BTVertexWeight>> weightSet = new HashMap<>();
        int numBones = aiMesh.mNumBones();
        PointerBuffer aiBones = aiMesh.mBones();
        for (int i = 0; i < numBones; i++) {
            AIBone aiBone = AIBone.create(aiBones.get(i));
            int id = boneList.size();
            BTBone bone = new BTBone(id, aiBone.mName().dataString(), toMatrix(aiBone.mOffsetMatrix()));
            boneList.add(bone);
            int numWeights = aiBone.mNumWeights();
            AIVertexWeight.Buffer aiWeights = aiBone.mWeights();
            for (int j = 0; j < numWeights; j++) {
                AIVertexWeight aiWeight = aiWeights.get(j);
                BTVertexWeight vw = new BTVertexWeight(bone.getBoneId(), aiWeight.mVertexId(),
                        aiWeight.mWeight());
                List<BTVertexWeight> vertexWeightList = weightSet.get(vw.getVertexId());
                if (vertexWeightList == null) {
                    vertexWeightList = new ArrayList<>();
                    weightSet.put(vw.getVertexId(), vertexWeightList);
                }
                vertexWeightList.add(vw);
            }
        }
        int numVertices = aiMesh.mNumVertices();
        for (int i = 0; i < numVertices; i++) {
            List<BTVertexWeight> vertexWeightList = weightSet.get(i);
            int size = vertexWeightList != null ? vertexWeightList.size() : 0;
            for (int j = 0; j < BTSkeleton.MAX_WEIGHTS; j++) {
                if (j < size) {
                    BTVertexWeight vw = vertexWeightList.get(j);
                    weights.add(vw.getWeight());
                    boneIds.add(vw.getBoneId());
                } else {
                    weights.add(0.0f);
                    boneIds.add(0);
                }
            }
        }
    }


    public static BTMesh[] load(ByteBuffer resource, ResourceLocation name, String fileExt, int flags) throws Exception {
        byte[] stringBytes=fileExt.getBytes("ISO-8859-1");
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


        BTMesh mesh = new BTMesh(aiMesh.mName().toString(), Utils.listToArray(vertices), Utils.listToArray(textures),
                Utils.listToArray(normals), Utils.listIntToArray(indices));
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