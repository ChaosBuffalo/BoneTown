package com.chaosbuffalo.bonetown.core.assimp.nodes;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class BTNode {

    private final List<BTNode> children;

    private Matrix4f transformation;

    private final String name;

    private BTNode parent;

    public BTNode(String name){
        this(name, new Matrix4f());
    }

    public BTNode(String name, Matrix4f trans){
        this.children = new ArrayList<>();
        this.name = name;
        this.transformation = trans;
    }

    public BTNode getParent() {
        return parent;
    }

    public void addChild(BTNode child){
        children.add(child);
    }

    public String getName() {
        return name;
    }

    public List<BTNode> getChildren(){
        return children;
    }

    public Matrix4f getTransformation() {
        return transformation;
    }

    public void setTransformation(Matrix4f transformation) {
        this.transformation = transformation;
    }

    public void setParent(BTNode parent){
        this.parent = parent;
    }

    public Matrix4f getAbsoluteTransformation(){
        BTNode parentNode = parent;
        Matrix4f transform  = new Matrix4f(getTransformation());
        while (parentNode != null){
            transform = new Matrix4f(parentNode.getTransformation()).mul(transform);
            parentNode = parentNode.parent;
        }
        return transform;
    }
}
