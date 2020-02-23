package com.chaosbuffalo.bonetown.core.animation_data;

public class BTVertexWeight {

    private int boneId;

    private int vertexId;

    private float weight;

    public BTVertexWeight(int boneId, int vertexId, float weight) {
        this.boneId = boneId;
        this.vertexId = vertexId;
        this.weight = weight;
    }

    public int getBoneId() {
        return boneId;
    }

    public int getVertexId() {
        return vertexId;
    }

    public float getWeight() {
        return weight;
    }

    public void setVertexId(int vertexId) {
        this.vertexId = vertexId;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}