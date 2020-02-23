package com.chaosbuffalo.bonetown.core.animation_data;

import java.util.List;

public class BTAnimation {

    private List<AnimationFrame> frames;

    private String name;

    private double duration;

    private int frameCount;

    public BTAnimation(String name, List<AnimationFrame> frames, double duration) {
        this.name = name;
        this.frames = frames;
        this.duration = duration;
        this.frameCount = frames.size();
    }


    public double getDuration() {
        return this.duration;
    }

    public List<AnimationFrame> getFrames() {
        return frames;
    }


    public AnimationFrame getFrame(int index){
        return this.frames.get(index);
    }

    public int getFrameCount(){
        return frameCount;
    }

    public String getName(){
        return name;
    }


}