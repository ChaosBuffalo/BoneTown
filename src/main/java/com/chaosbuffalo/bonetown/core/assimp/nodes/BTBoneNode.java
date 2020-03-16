package com.chaosbuffalo.bonetown.core.assimp.nodes;

import org.joml.Matrix4f;


public class BTBoneNode extends BTNode {

    private Matrix4f offsetMatrix; // assimps offset matrix

    private Matrix4f localTransform; // Transforms bone local to parent bone

    private Matrix4f worldTransform; // Transform of bone in model space

    private Matrix4f preRotation; // pre-rotation for bone

    private Matrix4f boneToMeshMatrix; // Bind pose matrix, from bone to model space

    private Matrix4f meshToBoneMatrix; // Inverse bind pose matrix, from model space to bone

    private int parentIndex;

    public BTBoneNode(String name) {
        super(name);
        this.offsetMatrix = new Matrix4f();
        this.preRotation = new Matrix4f();
    }

    public int getParentIndex() {
        return parentIndex;
    }

    public Matrix4f getMeshToBoneMatrix() {
        return meshToBoneMatrix;
    }

    public Matrix4f getBoneToMeshMatrix() {
        return boneToMeshMatrix;
    }

    public Matrix4f getWorldTransform() {
        return worldTransform;
    }

    public Matrix4f getLocalTransform() {
        return localTransform;
    }

    public Matrix4f getPreRotation() {
        return preRotation;
    }

    public void setPreRotation(Matrix4f preRotation) {
        this.preRotation = preRotation;
    }

    public void setBoneToMeshMatrix(Matrix4f boneToMeshMatrix) {
        this.boneToMeshMatrix = boneToMeshMatrix;
    }

    public void setLocalTransform(Matrix4f localTransform) {
        this.localTransform = localTransform;
    }

    public void setWorldTransform(Matrix4f worldTransform) {
        this.worldTransform = worldTransform;
    }

    public void setParentIndex(int parentIndex) {
        this.parentIndex = parentIndex;
    }

    public void setMeshToBoneMatrix(Matrix4f meshToBoneMatrix) {
        this.meshToBoneMatrix = meshToBoneMatrix;
    }

    @Override
    public Matrix4f getTransformation() {
        return new Matrix4f(getLocalTransform());
    }

    public void setOffsetMatrix(Matrix4f offsetMatrix){
        this.offsetMatrix = offsetMatrix;
    }

    public Matrix4f getOffsetMatrix() {
        return offsetMatrix;
    }
}
