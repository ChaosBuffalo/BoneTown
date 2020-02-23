package com.chaosbuffalo.bonetown.core.mesh_data;

/**
 * Created by Jacob on 1/25/2020.
 */
public class BTMesh {

    public final float[] positions;
    public final float[] texCoords;
    public final float[] normals;
    public final int[] indices;
    public String name;

    public BTMesh(String name, float[] positions, float[] texCoords, float[] normals, int[] indices){
        this.positions = positions;
        this.texCoords = texCoords;
        this.normals = normals;
        this.indices = indices;
        this.name = name;
    }
}
