package com.chaosbuffalo.bonetown.core.animation_data;

import com.chaosbuffalo.bonetown.BoneTown;

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

    public AnimationFrame getFrame(int ticks, boolean doLoop){
        double time = ticks * (1.0/20.0);
        double timePerFrame = getDuration() / getFrameCount();
        int currentFrame = (int) Math.floor(time / timePerFrame);
        if (doLoop){
            currentFrame = currentFrame % frameCount;
        } else {
            currentFrame = Math.min(currentFrame, frameCount);
        }
        BoneTown.LOGGER.info("Returning frame: {}, {}, {}, {}, ticks: {}, currentFrame: {}, dur {}", currentFrame, getFrameCount(), timePerFrame, time, ticks, (int) Math.floor(time / timePerFrame), duration);
        return getFrame(currentFrame);

    }


}