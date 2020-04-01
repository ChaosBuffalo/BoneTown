package com.chaosbuffalo.bonetown.core.model;



public class BakedAnimatedMesh extends BakedMesh {
    public final float[] weights;
    public final int[] boneIds;

    public BakedAnimatedMesh(String name, float[] positions, float[] texCoords,
                             float[] normals, int[] indices, float[] weights, int[] boneIds) {
        super(name, positions, texCoords, normals, indices);
        this.weights = weights;
        this.boneIds = boneIds;
    }
}
