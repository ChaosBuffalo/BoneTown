package com.chaosbuffalo.bonetown.core.assimp;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.Utils;
import com.chaosbuffalo.bonetown.core.animation.*;
import com.chaosbuffalo.bonetown.core.assimp.fbx.FbxPivot;
import com.chaosbuffalo.bonetown.core.assimp.nodes.*;
import com.chaosbuffalo.bonetown.core.model.BTAnimatedMesh;
import com.chaosbuffalo.bonetown.core.model.BTMesh;
import net.minecraft.util.ResourceLocation;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import static org.lwjgl.assimp.Assimp.*;


public class AssimpMeshLoader {

    public static final String FBX_MANGLING = "_$AssimpFbxNull$"; // Null leaf nodes are helpers

    public static final String FBX_PIVOT =  "_$AssimpFbx$";

    public static final String FBX_PRE_ROTATION = "_$AssimpFbx$_PreRotation";

    private static class SkeletonData {
        Map<String, Matrix4f> deformationBones;
        AINode root;
        List<AINode> boneNodes;

        public SkeletonData(Map<String, Matrix4f> deformationBones, AINode root,
                            List<AINode> boneNodes){
            this.deformationBones = deformationBones;
            this.root = root;
            this.boneNodes = boneNodes;
        }
    }

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
                        aiProcess_LimitBoneWeights | aiProcess_MakeLeftHanded);
    }

    public static class LoadAnimatedReturn {

        public BTAnimatedMesh[] meshes;
        public BTSkeleton skeleton;

        public LoadAnimatedReturn(BTAnimatedMesh[] meshes, BTSkeleton skeleton){
            this.meshes = meshes;
            this.skeleton = skeleton;
        }

    }

    private static AIScene loadScene(ByteBuffer resource, String fileExt, int flags) throws Exception{
        byte[] stringBytes=fileExt.getBytes(StandardCharsets.ISO_8859_1);
        byte[] ntBytes=new byte[stringBytes.length+1];
        System.arraycopy(stringBytes, 0, ntBytes, 0, stringBytes.length);
        AIPropertyStore propertyStore = Assimp.aiCreatePropertyStore();
        aiSetImportPropertyInteger(propertyStore, AI_CONFIG_IMPORT_FBX_PRESERVE_PIVOTS, 1);
        AIScene aiScene = aiImportFileFromMemoryWithProperties(resource, flags, ByteBuffer.wrap(ntBytes), propertyStore);
        Assimp.aiReleasePropertyStore(propertyStore);
        if (aiScene == null) {
            throw new Exception("Error loading Scene: " + aiGetErrorString());
        }
        return aiScene;
    }

    public static void loadAnimationsForSkeleton(ByteBuffer resource, ResourceLocation name,
                                                 BTSkeleton skeleton) throws Exception {
        loadAnimationsForSkeleton(resource, name, "fbx", aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices |
                aiProcess_Triangulate | aiProcess_FixInfacingNormals |
                aiProcess_LimitBoneWeights, skeleton);
    }

    public static void loadAnimationsForSkeleton(ByteBuffer resource, ResourceLocation name,
                                            String fileExt, int flags, BTSkeleton skeleton) throws Exception {

        BoneTown.LOGGER.info("Trying to load additional animations: {}", name.toString());
        AIScene aiScene = loadScene(resource, fileExt, flags);
        Map<String, BTAnimation> animations = processAnimations(aiScene, skeleton);
        for (String key : animations.keySet()){
            BoneTown.LOGGER.info("Adding {} to skeleton {}", key, name.toString());
            skeleton.addAnimation(key, animations.get(key));
        }

    }


    private static void printNodeTree(BTNode root){

        printNodeTree(root, 0);
    }

    private static void printNodeTree(BTNode node, int iteration){
        StringBuilder toPrint = new StringBuilder();
        for (int i = 0; i < iteration; i++){
            toPrint.append("--");
        }
        toPrint.append(node.getName());
        toPrint.append(" class: ");
        toPrint.append(node.getClass().getName());
        BoneTown.LOGGER.info(toPrint);
        for (BTNode child : node.getChildren()){
            printNodeTree(child, iteration + 1);
        }
    }

    private static AIMesh getMeshFromScene(AIScene scene, int index){
        PointerBuffer meshes = scene.mMeshes();
        return AIMesh.create(meshes.get(index));
    }

    private static void extractBoneTransfrom(AINode node, Matrix4f accumulatedTransform, int parentBoneIndex,
                                             BTSkeleton skeleton){
        String nodeName = node.mName().dataString();
        Matrix4f localNodeTransform = toMatrix(node.mTransformation());
        Matrix4f worldNodeTransform = new Matrix4f(accumulatedTransform).mul(localNodeTransform);
        int boneIndex = skeleton.getBoneId(nodeName);
        BTBoneNode bone = skeleton.getBoneByName(nodeName);
        if (boneIndex >= 0){
            Matrix4f offsetMatrix = new Matrix4f(bone.getOffsetMatrix());
            BoneTown.LOGGER.info("Setting skeleton data for: {}", nodeName);
            bone.setWorldTransform(worldNodeTransform);
            bone.setParentIndex(parentBoneIndex);
            bone.setMeshToBoneMatrix(offsetMatrix);
            bone.setBoneToMeshMatrix(new Matrix4f(offsetMatrix).invert());

        }

        if (nodeName.contains(FBX_PRE_ROTATION)){
            String baseName = getNodeName(nodeName);
            BTBoneNode boneNode = skeleton.getBoneByName(baseName);
            if (boneNode != null){
                BoneTown.LOGGER.info("Setting prerotation for: {}", boneNode.getName());
                printMatrixComponents(localNodeTransform, "Prerotation: ");
                BoneTown.LOGGER.info(localNodeTransform.toString());
                boneNode.setPreRotation(localNodeTransform);
            }
        }

        int childParentIndex = (boneIndex >= 0 ? boneIndex : parentBoneIndex);

        for (int nodeIndex = 0; nodeIndex < node.mNumChildren(); nodeIndex++){
            AINode child = AINode.create(node.mChildren().get(nodeIndex));
            extractBoneTransfrom(child, worldNodeTransform, childParentIndex, skeleton);
        }
    }

    private static void buildLocalBoneMatrices(BTSkeleton skeleton){

        for (BTBoneNode bone : skeleton.getBones()){
            int parentIndex = bone.getParentIndex();
            BoneTown.LOGGER.info("Setting local transform for bone: {}, parent index: {}", bone.getName(), parentIndex);
            if (parentIndex >= 0){
                BTBoneNode parentBone = skeleton.getBone(parentIndex);
                Matrix4f parentWorldInverse = new Matrix4f(parentBone.getWorldTransform());
                parentWorldInverse.invert();

                Matrix4f localTransform = parentWorldInverse.mul(bone.getWorldTransform());
                bone.setLocalTransform(localTransform);
            } else {
                bone.setLocalTransform(new Matrix4f(bone.getWorldTransform()));
            }
        }
    }

    private static void setBoneOffsetData(AINode node, AIScene scene, BTSkeleton skeleton){
        int meshCount = node.mNumMeshes();
        for (int meshIndex = 0; meshIndex < meshCount; meshIndex++){
            AIMesh mesh = getMeshFromScene(scene, node.mMeshes().get(meshIndex));
            int boneCount = mesh.mNumBones();
            for (int boneIndex = 0; boneIndex < boneCount; boneIndex++){
                AIBone bone = AIBone.create(mesh.mBones().get(boneIndex));
                BTBoneNode boneNode = skeleton.getBoneByName(bone.mName().dataString());
                if (boneNode == null){
                    BoneTown.LOGGER.error("Failed to find bone for {}", bone.mName().dataString());
                    continue;
                }
                Matrix4f offset = toMatrix(bone.mOffsetMatrix());
                AINode tempNode = node;
                while (tempNode != null){
                    BoneTown.LOGGER.info("Parent is: {}", tempNode.mName().dataString());
                    offset = toMatrix(tempNode.mTransformation()).mul(offset);
                    tempNode = tempNode.mParent();
                }
                BoneTown.LOGGER.info("Setting offset for: {}", boneNode.getName());
                printMatrixComponents(offset, "Offset");
                boneNode.setOffsetMatrix(offset);
            }
        }
        int childCount = node.mNumChildren();
        for (int childIndex = 0; childIndex < childCount; childIndex++)
        {
            AINode child = AINode.create(node.mChildren().get(childIndex));
            setBoneOffsetData(child, scene, skeleton);
        }

    }

    public static LoadAnimatedReturn loadAnimated(ByteBuffer resource, ResourceLocation name,
                                                  String fileExt, int flags) throws Exception {


        AIScene aiScene = loadScene(resource, fileExt, flags);
        SkeletonData data = findSkeleton(aiScene);
        Map<String, FbxPivot> pivots = new HashMap<>();
        BTNode rootNode = ImportNodes(aiScene, data, pivots);
        BoneTown.LOGGER.info("Root node is: {}", data.root.mName().dataString());
        BTNode rootBone = ImportBones(data.root, data.root.mParent(), null, pivots, data);
        BoneTown.LOGGER.info("Bones: ");
        printNodeTree(rootBone);
        BoneTown.LOGGER.info("Nodes: ");
        printNodeTree(rootNode);
        List<BTMeshNode> allMeshes = getAllNodesOfType(BTMeshNode.class, rootNode);
        List<BTAnimatedMesh> meshes = new ArrayList<>();
        List<BTBoneNode> allBones = getAllNodesOfType(BTBoneNode.class, rootBone);
        BTSkeleton skeleton = new BTSkeleton(allBones, new HashMap<>(), rootBone, new Matrix4f(), pivots);
        setBoneOffsetData(aiScene.mRootNode(), aiScene, skeleton);
        extractBoneTransfrom(aiScene.mRootNode(), new Matrix4f(), -1, skeleton);
        buildLocalBoneMatrices(skeleton);
        for (BTMeshNode meshNode : allMeshes){
            BoneTown.LOGGER.info("Found mesh: {}", meshNode.getName());
            for (BTMesh mesh : meshNode.getMeshes()){
                if (mesh instanceof BTAnimatedMesh){
                    ((BTAnimatedMesh) mesh).buildBoneWeights(skeleton);
                    meshes.add((BTAnimatedMesh) mesh);
                }
            }
        }

        return new LoadAnimatedReturn(meshes.toArray(new BTAnimatedMesh[0]), skeleton);
    }

    private static String aiVectord3DToString(AIVector3D vec){
        return String.format("%f, %f, %f", vec.x(), vec.y(), vec.z());
    }

    private static String aiQuaternionToString(AIQuaternion quat){
        return String.format("%f, %f, %f, %f", quat.x(), quat.y(), quat.z(), quat.w());
    }

    private static Map<String, BTAnimation> processAnimations(AIScene aiScene,
                                                              BTSkeleton skeleton) {
        HashMap<String, BTAnimation> animations = new HashMap<>();

        // Process all animations
        int numAnimations = aiScene.mNumAnimations();
        PointerBuffer aiAnimations = aiScene.mAnimations();
        for (int i = 0; i < numAnimations; i++) {
            BoneTown.LOGGER.info("Processing animation: {} ", i);
            AIAnimation aiAnimation = AIAnimation.create(aiAnimations.get(i));

            // Calculate transformation matrices for each node
            int numChannels = aiAnimation.mNumChannels();
            PointerBuffer aiChannels = aiAnimation.mChannels();

            Map<String, ArrayList<AINodeAnim>> channelGroups = new HashMap<>();
            ArrayList<String> groupOrder = new ArrayList<>();

            // Group our channels because sometimes they get split out
            for (int chanIndex = 0; chanIndex < numChannels; chanIndex++){
                AINodeAnim channelAnim = AINodeAnim.create(aiChannels.get(chanIndex));
                String nodeName = getNodeName(channelAnim.mNodeName().dataString());
                BoneTown.LOGGER.info("Found node name: {} for {}", nodeName, channelAnim.mNodeName().dataString());
                if (!channelGroups.containsKey(nodeName)){
                    channelGroups.put(nodeName, new ArrayList<>());
                    groupOrder.add(nodeName);
                }
                channelGroups.get(nodeName).add(channelAnim);
            }

            BoneTown.LOGGER.info("Done sorting groups");
            ArrayList<AINodeAnim> finalChannels = new ArrayList<>();
            for (String channelGroup : groupOrder){
                ArrayList<AINodeAnim> channels = channelGroups.get(channelGroup);
                AIVectorKey.Buffer positionKeys = null;
                AIVectorKey.Buffer scalingKeys = null;
                AIQuatKey.Buffer rotationKeys = null;
                AINodeAnim newChannel = AINodeAnim.create();
                for (AINodeAnim channel : channels) {
                    String channelName = channel.mNodeName().dataString();
                    if (channelName.endsWith("_$AssimpFbx$_Scaling")){
                        scalingKeys = channel.mScalingKeys();
                    } else if (channelName.endsWith("_$AssimpFbx$_Rotation")){
                        rotationKeys = channel.mRotationKeys();
                    } else if (channelName.endsWith("_$AssimpFbx$_Translation")){
                        positionKeys = channel.mPositionKeys();
                    } else {
                        scalingKeys = channel.mScalingKeys();
                        rotationKeys = channel.mRotationKeys();
                        positionKeys = channel.mPositionKeys();
                        newChannel.mNodeName(channel.mNodeName());
                    }
                }
                if (scalingKeys != null){
                    newChannel.mScalingKeys(scalingKeys);
                }
                if (rotationKeys != null){
                    newChannel.mRotationKeys(rotationKeys);
                }
                if (positionKeys != null){
                    newChannel.mPositionKeys(positionKeys);
                }

                newChannel.mNodeName(channels.get(0).mNodeName());

                finalChannels.add(newChannel);
            }
            BTAnimationNode animationNode = new BTAnimationNode(aiAnimation.mName().dataString());
            for (AINodeAnim aiNodeAnim : finalChannels) {
                String nodeName = getNodeName(aiNodeAnim.mNodeName().dataString());
                BTBoneNode node = skeleton.getBoneByName(nodeName);
                if (node == null){
                    BoneTown.LOGGER.info("Skipping processing for {}, pos keys: {}, " +
                                    "scale keys: {}, rot keys: {}", nodeName,
                            aiNodeAnim.mNumPositionKeys(),
                            aiNodeAnim.mNumScalingKeys(), aiNodeAnim.mNumRotationKeys());
                    continue;
                }
                BoneTown.LOGGER.info("Processing channel: {}", nodeName);
                AnimationChannel channel = buildTransFormationMatrices(aiNodeAnim, node, skeleton);
                animationNode.addChannel(nodeName, channel);
            }
            AnimationChannel rootChannel = animationNode.getChannel(skeleton.getRootNode().getName());
            animationNode.setFrameCount(rootChannel.getFrameCount());



            String name = aiAnimation.mName().dataString();
            List<IAnimationProvider> frames = buildAnimationFrames(animationNode, skeleton);
            BTAnimation animation = new BTAnimation(name, frames, aiAnimation.mDuration(),
                    aiAnimation.mTicksPerSecond());
            BoneTown.LOGGER.info("Loading Animation: {} with {} frames", animation.getName(),
                    animation.getFrameCount());
            animations.put(animation.getName(), animation);
        }
        return animations;
    }

    private static List<IAnimationProvider> buildAnimationFrames(BTAnimationNode animationNode, BTSkeleton skeleton) {

        int numFrames = animationNode.getFrameCount();
        List<IAnimationProvider> frameList = new ArrayList<>();
        for (int i = 0; i < numFrames; i++) {
            AnimationFrame frame = new AnimationFrame();
            frameList.add(frame);
            List<BTBoneNode> bones = skeleton.getBones();
            int numBones = bones.size();
            for (int j = 0; j < numBones; j++) {
                BTBoneNode bone = bones.get(j);
                AnimationChannel channel = animationNode.getChannel(bone.getName());
                Matrix4f frameTransform = new Matrix4f(channel.getFrame(i));
                BTNode parent = bone.getParent();
                Matrix4f boneTransform = new Matrix4f(bone.getAbsoluteTransformation());
                Matrix4f parentTransform;
                if (parent != null){
//                    BoneTown.LOGGER.info("index is: {} Parent index is: {}, parent: {}, bone: {}", j,
//                            skeleton.getBoneId(parent.getName()), parent.getName(), bone.getName());
                    int parentIndex =skeleton.getBoneId(parent.getName());
                    parentTransform = new Matrix4f(frame.getLocalJointMatrix(parentIndex));
                } else {
                    parentTransform = new Matrix4f();
                }

//                frame.setMatrix(j, frameTransform.mul(parentTransform).mul(boneTransform.invert()));
                frame.setMatrix(j, frameTransform.mul(parentTransform), boneTransform.invert());

            }
        }

        return frameList;
    }

    private static String getNodeName(String name){
        int index = name.indexOf(FBX_PIVOT);
        return (index >= 0) ? name.substring(0, index) : name;
    }


    public static void printMatrixComponents(Matrix4f matrix, String name){
        Vector3f translation = new Vector3f();
        matrix.getTranslation(translation);
        AxisAngle4f rotation = new AxisAngle4f();
        matrix.getRotation(rotation);
        Vector3f scale = new Vector3f();
        matrix.getScale(scale);
        BoneTown.LOGGER.info("Matrix {}", name);
        BoneTown.LOGGER.info("Translation: {}", translation.toString());
        BoneTown.LOGGER.info("Rotation: {}", rotation.toString());
        BoneTown.LOGGER.info("Scale: {}", scale.toString());
    }


    private static AnimationChannel buildTransFormationMatrices(AINodeAnim aiNodeAnim, BTBoneNode bone, BTSkeleton skeleton) {
        int numFrames = aiNodeAnim.mNumPositionKeys();
        AIVectorKey.Buffer positionKeys = aiNodeAnim.mPositionKeys();
        AIVectorKey.Buffer scalingKeys = aiNodeAnim.mScalingKeys();
        AIQuatKey.Buffer rotationKeys = aiNodeAnim.mRotationKeys();
        FbxPivot pivot = skeleton.getPivots().getOrDefault(bone.getName(), FbxPivot.Default);
        AnimationChannel channel = new AnimationChannel();
        BoneTown.LOGGER.info("Processing: {} Num keys: {}, {}, {}", bone.getName(),
                positionKeys.remaining(), scalingKeys.remaining(), rotationKeys.remaining());
        for (int i = 0; i < numFrames; i++) {
//            BoneTown.LOGGER.info("Processing frame: {}", i);
            AIVectorKey aiVecKey = positionKeys.get(i);
            AIVector3D vec = aiVecKey.mValue();
            Vector3f transVec = new Vector3f(vec.x(), vec.y(), vec.z());
            AIQuatKey quatKey = rotationKeys.get(i);
            AIQuaternion aiQuat = quatKey.mValue();
            Quaternionf quat = new Quaternionf(aiQuat.x(), aiQuat.y(), aiQuat.z(), aiQuat.w());

//            BoneTown.LOGGER.info("Translation: {}", transVec.toString());
//            BoneTown.LOGGER.info("Rotation: {}", quat.toString());
            Vector3f scaleVec = null;
            if (i < aiNodeAnim.mNumScalingKeys()) {
                aiVecKey = scalingKeys.get(i);
                vec = aiVecKey.mValue();
                scaleVec = new Vector3f(vec.x(), vec.y(), vec.z());
            }
            channel.addFrame(pivot.getTransform(transVec, quat, scaleVec));
        }
        return channel;
    }

    private static BTAnimatedMesh processAnimatedMesh(AIMesh aiMesh,
                                                      SkeletonData skeletonData) {
        BoneTown.LOGGER.info("Loading Animated Mesh << {}", aiMesh.mName().dataString());
        List<Float> vertices = new ArrayList<>();
        List<Float> textures = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        processVertices(aiMesh, vertices);
        processNormals(aiMesh, normals);
        processTextCoords(aiMesh, textures);
        processIndices(aiMesh, indices);
        Map<Integer, List<BTVertexWeight>> weightMap = processBones(aiMesh);


        BTAnimatedMesh mesh = null;
        BoneTown.LOGGER.info("Loaded  << {} vertices", vertices.size());
        BoneTown.LOGGER.info("Loaded  << {} indices", indices.size());
        BoneTown.LOGGER.info("Loaded  << {} normals", normals.size());
        BoneTown.LOGGER.info("Loaded  << {} textures", textures.size());
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

    private static Set<String> getBoneNames(AIScene scene){
        Set<String> boneNames = new HashSet<>();
        int numMeshes = scene.mNumMeshes();
        PointerBuffer aiMeshes = scene.mMeshes();
        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            int numBones = aiMesh.mNumBones();
            PointerBuffer aiBones = aiMesh.mBones();
            for (int b = 0; b < numBones; b++) {
                AIBone aiBone = AIBone.create(aiBones.get(b));
                boneNames.add(aiBone.mName().dataString());
            }
        }
        return boneNames;
    }

    private static Map<String, Matrix4f> findDeformationBones(AIScene scene){
        Map<String, Matrix4f> bones = new HashMap<>();
        int numMeshes = scene.mNumMeshes();
        PointerBuffer aiMeshes = scene.mMeshes();
        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            int numBones = aiMesh.mNumBones();
            PointerBuffer aiBones = aiMesh.mBones();
            for (int b = 0; b < numBones; b++){
                AIBone aiBone = AIBone.create(aiBones.get(b));
                if (!bones.containsKey(aiBone.mName().dataString())){
                    BoneTown.LOGGER.info("Adding {} to deformation bones", aiBone.mName().dataString());
                    bones.put(aiBone.mName().dataString(), toMatrix(aiBone.mOffsetMatrix()));
                }
            }
        }
        return bones;
    }

    private static AINode findRootBone(AIScene scene, String boneName){
        AINode node = findNodeByName(scene.mRootNode(), boneName);
        AINode rootBone = node;
        while (!node.equals(scene.mRootNode()) && node.mNumMeshes() == 0){
            if (!node.mName().dataString().contains("$AssimpFbx$")) {
                rootBone = node;
            }
            BoneTown.LOGGER.info("Going up one level: {}", node.mParent().mName().dataString());
            node = node.mParent();
        }
        BoneTown.LOGGER.info("FOUND ROOT ----- {}", rootBone.mName().dataString());
        return rootBone;
    }

    private static <T extends BTNode> List<T> getAllNodesOfType(Class<T> clazz, BTNode node){
        List<T> ret = new ArrayList<>();
        if (clazz.isInstance(node)){
            ret.add((T) node);
        }
        for (BTNode child : node.getChildren()){
            ret.addAll(getAllNodesOfType(clazz, child));
        }
        return ret;
    }

    private static BTNode ImportNodes(AIScene scene, SkeletonData skeletonData, Map<String, FbxPivot> pivots){
        BTNode node = ImportNodes(scene, scene.mRootNode(), null, null,
                pivots, skeletonData);
        return node;
    }

    @SuppressWarnings("Duplicates")
    private static BTNode ImportNodes(AIScene scene, AINode aiNode, AINode aiParent,
                                      BTNode parent, Map<String, FbxPivot> pivots,
                                      SkeletonData data){

        BTNode node = null;
        String nodeName = aiNode.mName().dataString();
        // has meshes
        if (aiNode.mNumMeshes() > 0){
            BoneTown.LOGGER.info("Node has meshes: {}", nodeName);
            PointerBuffer meshes = scene.mMeshes();
            BTMeshNode meshNode = new BTMeshNode(nodeName,
                    getRelativeTransformation(aiNode, aiParent));

            for (int i = 0; i < aiNode.mMeshes().remaining(); i++) {
                AIMesh aiMesh = AIMesh.create(meshes.get(aiNode.mMeshes().get(i)));
                BTAnimatedMesh mesh = processAnimatedMesh(aiMesh, data);
                meshNode.addMesh(mesh);
            }
            node = meshNode;
        }
        else if (nodeName.contains(FBX_PIVOT)){
            String originalName = getNodeName(nodeName);
            FbxPivot pivot;
            if (!pivots.containsKey(originalName)){
                pivot = new FbxPivot();
                pivots.put(originalName, pivot);
            } else {
                pivot = pivots.get(originalName);
            }
            Matrix4f transform = toMatrix(aiNode.mTransformation());
            BoneTown.LOGGER.info("Found pivot: {}", nodeName);
            if (nodeName.endsWith("_Translation"))
                pivot.translation = transform;
            else if (nodeName.endsWith("_RotationOffset"))
                pivot.rotationOffset = transform;
            else if (nodeName.endsWith("_RotationPivot"))
                pivot.rotationPivot = transform;
            else if (nodeName.endsWith("_PreRotation"))
                pivot.preRotation = transform;
            else if (nodeName.endsWith("_Rotation"))
                pivot.rotation = transform;
            else if (nodeName.endsWith("_PostRotation"))
                pivot.postRotation = transform;
            else if (nodeName.endsWith("_RotationPivotInverse"))
                pivot.rotationPivotInverse = transform;
            else if (nodeName.endsWith("_ScalingOffset"))
                pivot.scalingOffset = transform;
            else if (nodeName.endsWith("_ScalingPivot"))
                pivot.scalingPivot = transform;
            else if (nodeName.endsWith("_Scaling"))
                pivot.scaling = transform;
            else if (nodeName.endsWith("_ScalingPivotInverse"))
                pivot.scalingPivotInverse = transform;
            else if (nodeName.endsWith("_GeometricTranslation"))
                pivot.geometricTranslation = transform;
            else if (nodeName.endsWith("_GeometricRotation"))
                pivot.geometricRotation = transform;
            else if (nodeName.endsWith("_GeometricScaling"))
                pivot.geometricScaling = transform;
            else
                BoneTown.LOGGER.error("Failed to decode pivot node {}, unknown type", nodeName);
        }
        else if (!data.boneNodes.contains(aiNode)){
            node = new BTNode(nodeName, getRelativeTransformation(aiNode, aiParent));
        }

        if (node != null){
            if (parent != null){
                parent.addChild(node);
                node.setParent(parent);
            }
            aiParent = aiNode;
            parent = node;
        }

        for (int i = 0; i < aiNode.mNumChildren(); i++){
            AINode child = AINode.create(aiNode.mChildren().get(i));
            ImportNodes(scene, child, aiParent, parent, pivots, data);
        }
        return node;
    }

    private static SkeletonData findSkeleton(AIScene scene){
        Map<String, Matrix4f> deformationBones = findDeformationBones(scene);
        Set<AINode> rootBones = new HashSet<>();
        for (String boneName : deformationBones.keySet()){
            rootBones.add(findRootBone(scene, boneName));
        }

        if (rootBones.size() > 1){
            BoneTown.LOGGER.error("Found more than 1 root bone");
            for (AINode node : rootBones){
                BoneTown.LOGGER.info("Found: {}", node.mName().dataString());
            }
        } else {
            for (AINode node : rootBones){
                BoneTown.LOGGER.info("Found Root Node: {}", node.mName().dataString());
            }
        }
        AINode root = rootBones.toArray(new AINode[1])[0];
        List<AINode> nodes = new ArrayList<>();
        getSubtree(root, nodes, FBX_PIVOT);
        for (AINode node : nodes){
            BoneTown.LOGGER.info("Found {} in subtree", node.mName().dataString());
        }
        return new SkeletonData(deformationBones, root, nodes);
    }

    private static AINode findNodeByName(AINode node, String nodeName){
        if (node.mName().dataString().equals(nodeName)){
            return node;
        } else {
            int numChildren = node.mNumChildren();
            PointerBuffer children = node.mChildren();
            for (int i = 0; i < numChildren; i++){
                AINode child = AINode.create(children.get(i));
                AINode found = findNodeByName(child, nodeName);
                if (found != null){
                    return found;
                }
            }
            return null;
        }
    }


    protected static Map<Integer, List<BTVertexWeight>> processBones(AIMesh aiMesh) {
        HashMap<Integer, List<BTVertexWeight>> weightMap = new HashMap<>();
        int numBones = aiMesh.mNumBones();
        PointerBuffer aiBones = aiMesh.mBones();
        for (int i = 0; i < numBones; i++) {
            AIBone aiBone = AIBone.create(aiBones.get(i));
            int numWeights = aiBone.mNumWeights();
            BoneTown.LOGGER.info("Processing bone: {}", aiBone.mName().dataString());
            AIVertexWeight.Buffer aiWeights = aiBone.mWeights();
            for (int j = 0; j < numWeights; j++) {
                AIVertexWeight aiWeight = aiWeights.get(j);
                BTVertexWeight vw = new BTVertexWeight(aiBone.mName().dataString(), aiWeight.mVertexId(),
                        aiWeight.mWeight());
                List<BTVertexWeight> vertexWeightList = weightMap.get(vw.getVertexId());
                if (vertexWeightList == null) {
                    vertexWeightList = new ArrayList<>();
                    weightMap.put(vw.getVertexId(), vertexWeightList);
                }
                vertexWeightList.add(vw);
            }
        }
        return weightMap;
    }

    public static BTNode ImportBones(AINode aiNode, AINode aiParent, BTNode parent,
                                     Map<String, FbxPivot> pivots, SkeletonData skeletonData){
        BTNode node = null;
        String nodeName = aiNode.mName().dataString();
        BoneTown.LOGGER.info("Parsing: {}", nodeName);
        if (!nodeName.contains(FBX_PIVOT)) // Ignore pivot nodes
        {
            if (nodeName.contains(FBX_MANGLING)){
                BoneTown.LOGGER.info("Adding mangled node {} to bones", nodeName.replace(FBX_MANGLING, ""));
                node = new BTNode(nodeName.replace(FBX_MANGLING, ""),
                        getRelativeTransformation(aiNode, aiParent));
            } else if (skeletonData.boneNodes.contains(aiNode)){
                node = new BTBoneNode(nodeName);
                BoneTown.LOGGER.info("Adding bone: {}", node.getName());
            }
        }

        if (node != null){
            if (parent != null){
                parent.addChild(node);
                node.setParent(parent);
            }

            aiParent = aiNode;
            parent = node;
        }

        for (int i = 0; i < aiNode.mNumChildren(); i++){
            AINode child = AINode.create(aiNode.mChildren().get(i));
            ImportBones(child, aiParent, parent, pivots, skeletonData);
        }
        return node;



    }

    public static BTMesh[] load(ByteBuffer resource, ResourceLocation name, String fileExt, int flags) throws Exception {
        AIScene aiScene = loadScene(resource, fileExt, flags);

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
        BoneTown.LOGGER.info("Loading Mesh << {}", aiMesh.mName().dataString());
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
        }
    }

    private static void getSubtree(AINode node, List<AINode> list, String excludeNames){
        if (!node.mName().dataString().contains(excludeNames)){
            list.add(node);
        }
        int numChildren = node.mNumChildren();
        PointerBuffer children = node.mChildren();
        for (int i = 0; i < numChildren; i++) {
            AINode child = AINode.create(children.get(i));
            getSubtree(child, list, excludeNames);
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

    private static Matrix4f getAINodeWorldTransform(AINode node){
        if (node == null){
            BoneTown.LOGGER.info("Returning identity, end of tree");
            return new Matrix4f();
        } else {
            BoneTown.LOGGER.info("Getting node world transform for: {}", node.mName().dataString());
        }
        Matrix4f localTransform = toMatrix(node.mTransformation());
        BoneTown.LOGGER.info("{} \n {}", node.mName().dataString(), localTransform.toString());
        printMatrixComponents(localTransform, node.mName().dataString());
        Matrix4f parentTransform = getAINodeWorldTransform(node.mParent());
        return parentTransform.mul(localTransform);
    }


    private static Matrix4f getRelativeTransformation(AINode node, AINode ancestor){

        Matrix4f transform = toMatrix(node.mTransformation());
        Matrix4f finalTransform = new Matrix4f();
        AINode parent = node.mParent();
        List<AINode> parents = new ArrayList<>();
        while (parent != null && !parent.equals(ancestor)){
            BoneTown.LOGGER.info("Node: {}, Parent: {}", node.mName().dataString(), parent.mName().dataString());
//            transform.mul(toMatrix(parent.mTransformation()));
            if (!parent.mName().dataString().contains(FBX_PIVOT)){
                parents.add(parent);
            }

            parent = parent.mParent();
        }
        Collections.reverse(parents);
        for (AINode parentNode : parents){
            BoneTown.LOGGER.info("Multiplying by: {}", parentNode.mName().dataString());
            finalTransform.mul(toMatrix(parentNode.mTransformation()));
        }
        if (parent == null && ancestor != null){
            BoneTown.LOGGER.error("Node: {} does not descend from {}",
                    node.mName().dataString(), ancestor.mName().dataString());
        }
        finalTransform.mul(transform);
        return finalTransform;
    }
}