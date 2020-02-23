package com.chaosbuffalo.bonetown.core.animation_data;


import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class BTNode {

    private final List<BTNode> children;

    private final List<Matrix4f> transformations;

    private final String name;

    private final BTNode parent;

    public BTNode(String name, BTNode parent) {
        this.name = name;
        this.parent = parent;
        this.transformations = new ArrayList<>();
        this.children = new ArrayList<>();

    }

    public static Matrix4f getParentTransforms(BTNode node, int framePos) {
        if (node == null) {
            return new Matrix4f();
        } else {
            Matrix4f parentTransform = new Matrix4f(getParentTransforms(node.getParent(), framePos));
            List<Matrix4f> transformations = node.getTransformations();
            Matrix4f nodeTransform;
            int transfSize = transformations.size();
            if (framePos < transfSize) {
                nodeTransform = transformations.get(framePos);
            } else if ( transfSize > 0 ) {
                nodeTransform = transformations.get(transfSize - 1);
            } else {
                nodeTransform = new Matrix4f();
            }
            parentTransform.mul(nodeTransform);
            return parentTransform;
        }
    }

    public void addChild(BTNode node) {
        this.children.add(node);
    }

    public void addTransformation(Matrix4f transformation) {
        transformations.add(transformation);
    }

    public BTNode findByName(String targetName) {
        BTNode result = null;
        if (this.name.equals(targetName)) {
            result = this;
        } else {
            for (BTNode child : children) {
                result = child.findByName(targetName);
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

    public int getAnimationFrames() {
        int numFrames = this.transformations.size();
        for (BTNode child : children) {
            int childFrame = child.getAnimationFrames();
            numFrames = Math.max(numFrames, childFrame);
        }
        return numFrames;
    }

    public List<BTNode> getChildren() {
        return children;
    }

    public List<Matrix4f> getTransformations() {
        return transformations;
    }

    public String getName() {
        return name;
    }

    public BTNode getParent() {
        return parent;
    }
}
