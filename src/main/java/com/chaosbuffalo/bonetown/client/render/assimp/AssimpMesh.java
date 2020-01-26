package com.chaosbuffalo.bonetown.client.render.assimp;

/**
 * Created by Jacob on 1/25/2020.
 */
public class AssimpMesh {

    public final float[] positions;
    public final float[] textCoords;
    public final float[] normals;
    public final int[] indices;
    private AssimpMaterial material;

    public AssimpMesh(float[] positions, float[] textCoords, float[] normals, int[] indices){
        this.positions = positions;
        this.textCoords = textCoords;
        this.normals = normals;
        this.indices = indices;
    }

    public AssimpMaterial getMaterial() {
        return material;
    }

    public void setMaterial(AssimpMaterial material) {
        this.material = material;
    }
}
