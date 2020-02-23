package com.chaosbuffalo.bonetown.core.mesh_data;

public class BTAnimatedMesh extends BTMesh {
    public final float[] weights;
    public final int[] boneIds;

    public BTAnimatedMesh(String name, float[] positions, float[] texCoords,
                          float[] normals, int[] indices,  int[] boneIds, float[] weights) {
        super(name, positions, texCoords, normals, indices);
        this.boneIds = boneIds;
        this.weights = weights;
    }


}
