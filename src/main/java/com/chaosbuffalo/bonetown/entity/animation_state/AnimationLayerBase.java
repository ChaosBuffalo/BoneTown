package com.chaosbuffalo.bonetown.entity.animation_state;

import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import net.minecraft.entity.Entity;



public abstract class AnimationLayerBase<T extends Entity & IBTAnimatedEntity> implements IAnimationLayer<T> {

    private int startTime;
    protected boolean isValid;
    private boolean isActive;
    private final T entity;
    private boolean autoStart;
    private final String name;

    public AnimationLayerBase(String name, T entity){
        startTime = 0;
        this.entity = entity;
        this.isValid = entity != null && entity.getSkeleton() != null;
        this.isActive = true;
        this.autoStart = true;
        this.name = name;
    }

    @Override
    public String getLayerName() {
        return name;
    }

    @Override
    public void setActive(boolean isActive){
        this.isActive = isActive;
    }


    @Override
    public T getEntity() {
        return entity;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    @Override
    public boolean shouldAutoStart(){
        return autoStart;
    }

    @Override
    public boolean shouldRun(){
        return isValid() && isActive();
    }


    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setStartTime(int tick) {
        startTime = tick;
    }

    public boolean isValid() {
        return isValid;
    }

    @Override
    public int getStartTime() {
        return startTime;
    }

    abstract void doLayerWork(IPose basePose, int currentTime, float partialTicks, IPose outPose);

    @Override
    public void processLayer(IPose basePose, int currentTime, float partialTicks, IPose outPose) {
        if (!shouldRun()){
            return;
        }
        doLayerWork(basePose, currentTime, partialTicks, outPose);
    }
}
