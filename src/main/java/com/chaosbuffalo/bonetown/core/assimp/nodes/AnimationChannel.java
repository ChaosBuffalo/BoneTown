package com.chaosbuffalo.bonetown.core.assimp.nodes;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class AnimationChannel {

    private final List<Matrix4f> frames;

    public AnimationChannel(){
        this.frames = new ArrayList<>();
    }

    public void addFrame(Matrix4f transform){
        frames.add(transform);
    }

    public Matrix4f getFrame(int i){
        return frames.get(i);
    }

    public int getFrameCount(){
        return frames.size();
    }
}
