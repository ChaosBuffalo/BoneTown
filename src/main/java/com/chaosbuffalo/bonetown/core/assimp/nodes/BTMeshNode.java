package com.chaosbuffalo.bonetown.core.assimp.nodes;

import com.chaosbuffalo.bonetown.core.model.BTMesh;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class BTMeshNode extends BTNode {

    private final List<BTMesh> meshes;

    public BTMeshNode(String name, Matrix4f trans) {
        super(name, trans);
        this.meshes = new ArrayList<>();
    }

    public void addMesh(BTMesh mesh){
        this.meshes.add(mesh);
    }

    public List<BTMesh> getMeshes(){
        return meshes;
    }
}
