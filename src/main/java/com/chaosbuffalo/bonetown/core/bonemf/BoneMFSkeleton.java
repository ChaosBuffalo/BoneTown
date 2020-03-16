package com.chaosbuffalo.bonetown.core.bonemf;

import com.chaosbuffalo.bonetown.BoneTown;
import org.joml.Matrix4d;
import org.joml.Vector4d;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoneMFSkeleton {
    private final List<BoneMFNode> boneArray;
    private final Map<String, BoneMFNode> boneIndex;
    private final Map<String, Integer> boneArrayIndex;
    private final Map<String, Integer> boneParentArrayIndex;
    private final BoneMFNode root;

    public BoneMFSkeleton(BoneMFNode root){
        this.root = root;
        boneIndex = new HashMap<>();
        boneArrayIndex = new HashMap<>();
        boneParentArrayIndex = new HashMap<>();
        boneArray = root.getNodesOfType(BoneMFAttribute.AttributeTypes.SKELETON);
        int count = 0;
        for (BoneMFNode node : boneArray){
            boneIndex.put(node.getName(), node);
            boneArrayIndex.put(node.getName(), count);
            if (node.getParent() != null){
                int parentId = getBoneId(node.getParent().getName());
                BoneTown.LOGGER.info("Found {} id for parent {}", parentId, node.getParent().getName());
                boneParentArrayIndex.put(node.getName(), parentId);
            } else {
                boneParentArrayIndex.put(node.getName(), -1);
            }
            count++;
        }
        for (BoneMFNode node : boneArray){
            BoneTown.LOGGER.info("Node transform: {}", node.getName());
            BoneTown.LOGGER.info("\n \n {}", node.calculateGlobalTransform());
        }
    }

    public int getBoneId(String name){
        return boneArrayIndex.getOrDefault(name, -1);
    }

    public int getBoneParentId(String name){
        return boneParentArrayIndex.get(name);
    }

    public BoneMFNode getBone(String name){
        return boneIndex.get(name);
    }

    public BoneMFNode getRoot() {
        return root;
    }

    public List<BoneMFNode> getBones(){
        return boneArray;
    }
}
