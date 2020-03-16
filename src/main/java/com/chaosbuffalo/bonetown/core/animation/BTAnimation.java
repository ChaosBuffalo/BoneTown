package com.chaosbuffalo.bonetown.core.animation;


import java.util.List;

public class BTAnimation {

    private List<IAnimationProvider> frames;

    private String name;

    private double duration;

    private double ticksPerSecond;

    private int frameCount;

    public class AnimationFrameReturn {

        public IAnimationProvider current;
        public IAnimationProvider next;
        public float partialTick;

        public AnimationFrameReturn(IAnimationProvider current,
                                    IAnimationProvider next,
                                    float partialTick){
            this.current = current;
            this.next = next;
            this.partialTick = partialTick;
        }
    }

    public BTAnimation(String name, List<IAnimationProvider> frames, double duration, double ticksPerSecond) {
        this.name = name;
        this.frames = frames;
        this.duration = duration;
        this.frameCount = frames.size();
        this.ticksPerSecond = ticksPerSecond;
    }


    public double getDuration() {
        return this.duration;
    }

    public List<IAnimationProvider> getFrames() {
        return frames;
    }


    public IAnimationProvider getFrame(int index){
        return this.frames.get(index);
    }

    public int getFrameCount(){
        return frameCount;
    }

    public double getTicksPerSecond(){
        return ticksPerSecond;
    }

    public String getName(){
        return name;
    }


    public AnimationFrameReturn getInterpolatedFrame(int ticks,
                                                     boolean doLoop,
                                                     float partialTicks){
        double time = (ticks + partialTicks) * (1.0/20.0);
        double animTicks = time * getTicksPerSecond();
        double leftover = animTicks - Math.floor(animTicks);
        int currentFrame = (int) Math.floor(animTicks);
        // we need to offset by 1 because most animators start their animations at 1
        int offset = 0;
        int totalTicks = (int) getDuration() + offset;
        int nextFrame = currentFrame + 1;
        if (doLoop){
            currentFrame = currentFrame % totalTicks;
            nextFrame = nextFrame % totalTicks;
        } else {
            currentFrame = Math.min(currentFrame, totalTicks);
            nextFrame = Math.min(nextFrame, totalTicks);
        }
        return new AnimationFrameReturn(getFrame(currentFrame + offset),
                getFrame(nextFrame + offset), (float) leftover);

    }


}