package com.chaosbuffalo.bonetown.core.animation;


import com.chaosbuffalo.bonetown.BoneTown;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class BakedAnimation {

    private List<AnimationFrame> frames;

    private ResourceLocation name;

    private double ticksPerSecond;

    private int frameCount;

    public static class InterpolationFramesReturn {

        public AnimationFrame current;
        public AnimationFrame next;
        public float partialTick;

        public InterpolationFramesReturn(AnimationFrame current,
                                         AnimationFrame next,
                                         float partialTick){
            this.current = current;
            this.next = next;
            this.partialTick = partialTick;
        }
    }

    public BakedAnimation(ResourceLocation name, List<AnimationFrame> frames, double ticksPerSecond) {
        this.name = name;
        this.frames = frames;
        this.frameCount = frames.size();
        this.ticksPerSecond = ticksPerSecond;
    }


    public double getDuration() {
        return (double) getFrameCount() / getTicksPerSecond();
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

    public double getTicksPerSecond(){
        return ticksPerSecond;
    }

    public ResourceLocation getName(){
        return name;
    }

    public int getFrameForTicks(int ticks){
        double time = ticks * (1.0/20.0);
        double animTicks = time * getTicksPerSecond();
        return (int)Math.floor(animTicks);
    }

    public int getTotalTicks(){
        return (int) Math.ceil(getDuration() * 20.0);
    }


    public InterpolationFramesReturn getInterpolationFrames(int ticks,
                                                            boolean doLoop,
                                                            float partialTicks){
        double time = (ticks + partialTicks) * (1.0/20.0);
        double animTicks = time * getTicksPerSecond();
        double leftover = animTicks - Math.floor(animTicks);
        int currentFrame = (int) Math.floor(animTicks);
        int totalFrames = getFrameCount();
        int nextFrame = currentFrame + 1;
        if (doLoop){
            currentFrame = currentFrame % totalFrames;
            nextFrame = nextFrame % totalFrames;
        } else {
            currentFrame = Math.min(currentFrame, totalFrames - 1);
            nextFrame = Math.min(nextFrame, totalFrames - 1);
        }
        return new InterpolationFramesReturn(getFrame(currentFrame), getFrame(nextFrame), (float) leftover);

    }


}