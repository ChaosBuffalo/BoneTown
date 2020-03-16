package com.chaosbuffalo.bonetown.core.assimp.nodes;

import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

public class BTAnimationNode extends BTNode {

    private final Map<String, AnimationChannel> channels;
    private float duration;
    private float ticksPerSecond;
    private int frameCount;


    public BTAnimationNode(String name) {
        super(name, new Matrix4f());
        this.channels = new HashMap<>();
    }

    public void addChannel(String name, AnimationChannel channel){
        this.channels.put(name, channel);
    }

    public void setTicksPerSecond(float ticksPerSecond){
        this.ticksPerSecond = ticksPerSecond;
    }

    public float getTicksPerSecond() {
        return ticksPerSecond;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration){
        this.duration = duration;
    }

    public void setFrameCount(int frames){
        this.frameCount = frames;
    }

    public int getFrameCount(){
        return this.frameCount;
    }

    public Map<String, AnimationChannel> getChannels(){
        return channels;
    }

    public AnimationChannel getChannel(String name){
        return channels.get(name);
    }
}
