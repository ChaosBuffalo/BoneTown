package com.chaosbuffalo.bonetown.core.animation;


import com.chaosbuffalo.bonetown.core.assimp.fbx.FbxPivot;
import com.chaosbuffalo.bonetown.core.assimp.nodes.BTBoneNode;
import com.chaosbuffalo.bonetown.core.assimp.nodes.BTNode;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.util.*;


public class BTSkeleton {

    public static int MAX_WEIGHTS = 4;
    private final Map<String, BTAnimation> animations;
    private final List<BTBoneNode> bones;
    private final BTNode rootNode;
    private final Map<String, BTBoneNode> boneIndex;
    private final Matrix4f rootTransformation;
    private final Map<String, FbxPivot> pivots;

    private final Map<String, Integer> boneNameToBoneIdIndex;


    public BTSkeleton(List<BTBoneNode> bones, Map<String, BTAnimation> animations, BTNode rootNode,
                      Matrix4f rootTransformation, Map<String, FbxPivot> pivots){
        this.animations = animations;
        this.bones = bones;
        this.rootNode = rootNode;
        boneIndex = new HashMap<>();
        this.rootTransformation = rootTransformation;
        for (BTBoneNode bone : bones){
            boneIndex.put(bone.getName(), bone);
        }
        boneNameToBoneIdIndex = new HashMap<>();
        int i = 0;
        for (BTBoneNode bone : bones){
            boneNameToBoneIdIndex.put(bone.getName(), i);
            i++;
        }
        this.pivots = pivots;
    }

    public void assignBoneParents(){
        for (BTBoneNode bone : bones){
            BTNode parent = bone.getParent();
            if (parent != null){
                bone.setParentIndex(getBoneId(parent.getName()));
            } else {
                bone.setParentIndex(-1);
            }
        }
    }

    public Map<String, FbxPivot> getPivots(){ return pivots; }

    public List<BTBoneNode> getBones(){
        return bones;
    }

    public void addAnimation(String name, BTAnimation animation){
        this.animations.put(name, animation);
    }

    public BTNode getRootNode(){
        return rootNode;
    }

    public Matrix4f getRootTransformation(){
        return rootTransformation;
    }

    @Nullable
    public BTBoneNode getBone(int index){
        return bones.get(index);
    }

//    private void addChildrenToSet(BTNodeOld node, Set<BTBoneOld> out){
//        out.add(getBoneByName(node.getName()));
//        for (BTNodeOld child : node.getChildren()){
//            addChildrenToSet(child, out);
//        }
//    }

//    @Nullable
//    public BTAnimation createSubsetAnimationExcludeBone(String startingBone, String animName){
//        BTAnimation animation = getAnimation(animName);
//        if (animation == null){
//            BoneTown.LOGGER.error("Couldn't find animation {} for createSubsetAnimation", animName);
//            return null;
//        }
//        List<IAnimationProvider> newFrames = new ArrayList<>();
//        Set<BTBoneOld> subsetBones = getBonesFromBone(startingBone);
//        for (IAnimationProvider frame : animation.getFrames()){
//            SubsetFrame newFrame = new SubsetFrame();
//            for (BTBoneNode bone : getBones()){
//                if (!subsetBones.contains(bone)){
//                    newFrame.setMatrix(bone.get), frame.getJointMatrix(bone.getBoneId()));
//                }
//            }
//        }
//        return new BTAnimation(UUID.randomUUID().toString(), newFrames, animation.getDuration(),
//                animation.getTicksPerSecond());
//    }

//    @Nullable
//    public BTAnimation createSubsetAnimationIncludeBone(String startingBone, String animName){
//        BTAnimation animation = getAnimation(animName);
//        if (animation == null){
//            BoneTown.LOGGER.error("Couldn't find animation {} for createSubsetAnimation", animName);
//            return null;
//        }
//        List<IAnimationProvider> newFrames = new ArrayList<>();
//        Set<BTBoneOld> subsetBones = getBonesFromBone(startingBone);
//        for (IAnimationProvider frame : animation.getFrames()){
//            SubsetFrame newFrame = new SubsetFrame();
//            for (BTBoneOld bone : subsetBones){
//                newFrame.setMatrix(bone.getBoneId(), frame.getJointMatrix(bone.getBoneId()));
//            }
//        }
//        return new BTAnimation(UUID.randomUUID().toString(), newFrames, animation.getDuration(),
//                animation.getTicksPerSecond());
//
//    }
//
//    public Set<BTBoneOld> getBonesFromBone(String name){
//        HashSet<BTBoneOld> ret = new HashSet<>();
//        BTNodeOld node = getRootNode().findByName(name);
//        if (node != null){
//            addChildrenToSet(node, ret);
//        }
//        return ret;
//    }

    @Nullable
    public BTBoneNode getBoneByName(String name){
        return boneIndex.get(name);
    }


    public int getBoneId(String name){
        return boneNameToBoneIdIndex.getOrDefault(name, -1);

    }

//    public Matrix4f getBoneTransform(String name, IAnimationProvider frame){
//        return frame.getJointMatrix(getBoneId(name));
//    }

    @Nullable
    public BTAnimation getAnimation(String name){
        return animations.get(name);
    }


}
