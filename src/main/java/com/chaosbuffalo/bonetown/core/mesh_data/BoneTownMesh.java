package com.chaosbuffalo.bonetown.core.mesh_data;

/**
 * Created by Jacob on 1/25/2020.
 */
public class BoneTownMesh {

    public final float[] positions;
    public final float[] textCoords;
    public final float[] normals;
    public final int[] indices;
    public String name;
    private BoneTownMaterial material;

    public BoneTownMesh(float[] positions, float[] textCoords, float[] normals, int[] indices, String name){
        this.positions = positions;
        this.textCoords = textCoords;
        this.normals = normals;
        this.indices = indices;
        this.name = name;
    }

    public BoneTownMaterial getMaterial() {
        return material;
    }

    public void setMaterial(BoneTownMaterial material) {
        this.material = material;
    }
}
