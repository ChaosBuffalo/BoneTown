package com.chaosbuffalo.bonetown.core.animation;

public class BTVertexWeight {

    private String boneName;

    private int vertexId;

    private float weight;

    public BTVertexWeight(String boneName, int vertexId, float weight) {
        this.boneName = boneName;
        this.vertexId = vertexId;
        this.weight = weight;
    }

    public String getBoneName() {
        return boneName;
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