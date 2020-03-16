package com.chaosbuffalo.bonetown.core.model;

import com.chaosbuffalo.bonetown.core.animation.BTSkeleton;
import com.chaosbuffalo.bonetown.core.animation.BTVertexWeight;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BTAnimatedMesh extends BTMesh {
    public final float[] weights;
    public final int[] boneIds;
    Map<Integer, List<BTVertexWeight>> weightMap;

    public BTAnimatedMesh(String name, float[] positions, float[] texCoords,
                          float[] normals, int[] indices, float[] weights, int[] boneIds) {
        super(name, positions, texCoords, normals, indices);
        this.weights = weights;
        this.boneIds = boneIds;
    }

    public void buildBoneWeights(BTSkeleton skeleton){
        int numVertices = this.vertices;
        List<Float> weightsTemp = new ArrayList<>();
        List<Integer> idsTemp = new ArrayList<>();
        for (int i = 0; i < numVertices; i++) {
            List<BTVertexWeight> vertexWeightList = weightMap.get(i);
            int size = vertexWeightList != null ? vertexWeightList.size() : 0;
            for (int j = 0; j < BTSkeleton.MAX_WEIGHTS; j++) {
                if (j < size) {
                    BTVertexWeight vw = vertexWeightList.get(j);
                    weightsTemp.add(vw.getWeight());
                    idsTemp.add(skeleton.getBoneId(vw.getBoneName()));
                } else {
                    weightsTemp.add(0.0f);
                    idsTemp.add(0);
                }
            }
        }

    }


}
