package com.chaosbuffalo.bonetown.entity.animation_state.layers;

import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import com.chaosbuffalo.bonetown.entity.animation_state.messages.AnimationLayerMessage;
import net.minecraft.entity.Entity;

import java.util.HashMap;
import java.util.Map;

import java.util.function.Consumer;


public abstract class AnimationLayerBase<T extends Entity & IBTAnimatedEntity> implements IAnimationLayer<T> {

    private int startTime;
    protected boolean isValid;
    private boolean isActive;
    private final T entity;
    private boolean autoStart;
    private final String name;
    private final Map<String, Consumer<AnimationLayerMessage>> messageCallbacks;

    public AnimationLayerBase(String name, T entity){
        startTime = 0;
        this.entity = entity;
        this.isValid = entity != null && entity.getSkeleton() != null;
        this.isActive = true;
        this.autoStart = true;
        this.messageCallbacks = new HashMap<>();
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

    public void addMessageCallback(String messageType, Consumer<AnimationLayerMessage> consumer){
        messageCallbacks.put(messageType, consumer);
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
