package com.chaosbuffalo.bonetown.entity.animation_state.layers;

import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import com.chaosbuffalo.bonetown.entity.animation_state.messages.layer.AnimationLayerMessage;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.function.Consumer;


public abstract class AnimationLayerBase<T extends Entity & IBTAnimatedEntity<T>> implements IAnimationLayer<T> {

    private int startTime;
    protected boolean isValid;
    private boolean isActive;
    private final T entity;
    private boolean autoStart;
    protected int duration;
    private final String name;
    private final Map<String, Consumer<AnimationLayerMessage>> messageCallbacks;
    private Runnable animEndCallback;

    public AnimationLayerBase(String name, T entity){
        startTime = 0;
        this.entity = entity;
        this.isValid = entity != null && entity.getSkeleton() != null;
        this.isActive = true;
        this.autoStart = true;
        this.duration = -1;
        this.messageCallbacks = new HashMap<>();
        animEndCallback = null;
        this.name = name;
    }

    @Override
    public boolean shouldLoop() {
        return false;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public String getLayerName() {
        return name;
    }

    @Override
    public void setActive(boolean isActive){
        this.isActive = isActive;
    }

    public void addMessageCallback(String messageType, Consumer<AnimationLayerMessage> consumer){
        messageCallbacks.put(messageType, consumer);
    }


    @Override
    public void addEndCallback(Runnable callback) {
        animEndCallback = callback;
    }

    @Override
    public void tick(int ticks) {
        if (getDuration() != -1 && animEndCallback != null){
            int currentTicks = ticks - getStartTime();
            if (shouldLoop()){
                currentTicks = currentTicks % getDuration();
            }
            if (currentTicks == getDuration() - 1){
                animEndCallback.run();
            }
        }
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

    @Override
    public void consumeLayerMessage(AnimationLayerMessage message) {
        if (messageCallbacks.containsKey(message.getMessageType())){
            messageCallbacks.get(message.getMessageType()).accept(message);
        }
    }
}
