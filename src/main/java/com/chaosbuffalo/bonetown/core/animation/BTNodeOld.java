package com.chaosbuffalo.bonetown.core.animation;


import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BTNodeOld {

    private final List<BTNodeOld> children;

    private final List<Matrix4f> transformations;

    private final String name;

    private final BTNodeOld parent;

    public BTNodeOld(String name, BTNodeOld parent) {
        this.name = name;
        this.parent = parent;
        this.transformations = new ArrayList<>();
        this.children = new ArrayList<>();

    }

    public static Matrix4f getParentTransforms(BTNodeOld node, int framePos) {
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

    public void addChild(BTNodeOld node) {
        this.children.add(node);
    }

    public void addTransformation(Matrix4f transformation) {
        transformations.add(transformation);
    }

    @Nullable
    public BTNodeOld findByName(String targetName) {
        BTNodeOld result = null;
        if (this.name.equals(targetName)) {
            result = this;
        } else {
            for (BTNodeOld child : children) {
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
        for (BTNodeOld child : children) {
            int childFrame = child.getAnimationFrames();
            numFrames = Math.max(numFrames, childFrame);
        }
        return numFrames;
    }

    public List<BTNodeOld> getChildren() {
        return children;
    }

    public List<Matrix4f> getTransformations() {
        return transformations;
    }

    public String getName() {
        return name;
    }

    public BTNodeOld getParent() {
        return parent;
    }
}
