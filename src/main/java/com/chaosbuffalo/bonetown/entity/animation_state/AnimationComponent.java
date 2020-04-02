package com.chaosbuffalo.bonetown.entity.animation_state;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.animation.AnimationFrame;
import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import com.chaosbuffalo.bonetown.entity.animation_state.messages.AnimationLayerMessage;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.*;

public class AnimationComponent<T extends Entity & IBTAnimatedEntity> implements INBTSerializable<CompoundNBT> {

    private final T entity;
    private int ticks;
    private final Map<String, AnimationState<T>> animationStates;
    private final AnimationFrame workFrame;
    private static final AnimationFrame DEFAULT_FRAME = new AnimationFrame();
    private int lastPoseFetch;
    private float lastPartialTicks;
    private String currentState;
    public static final String INVALID_STATE = "invalid";


    public AnimationComponent(T entity){
        this.entity = entity;
        ticks = 0;
        animationStates = new HashMap<>();
        workFrame = new AnimationFrame();
        lastPoseFetch = -1;
        lastPartialTicks = 0;
        currentState = INVALID_STATE;
    }

    public void addAnimationState(AnimationState<T> animState){
        animationStates.put(animState.getName(), animState);
    }

    public void removeAnimationState(String name){
        animationStates.remove(name);
    }

    public void startLayer(String name){
        AnimationState<T> state = getState(getCurrentState());
        if (state != null){
            state.startLayer(name, ticks);
        }
    }

    public void sendLayerMessage(String stateName, String layerName, AnimationLayerMessage message){
        AnimationState<T> state = getState(stateName);
        if (state != null){
            state.sendLayerMessage(layerName, message);
        }

    }

    public void stopLayer(String name){
        AnimationState<T> state = getState(getCurrentState());
        if (state != null){
            state.stopLayer(name);
        }
    }

    public void setState(String stateName){
        if (!getCurrentState().equals(INVALID_STATE)){
            AnimationState<T> state = getState(getCurrentState());
            if (state != null){
                state.leaveState();
            }
        }
        AnimationState<T> newState = getState(stateName);
        if (newState != null){
            currentState = stateName;
            newState.enterState(ticks);
        } else {
            currentState = INVALID_STATE;
        }
    }

    protected boolean isSamePose(float partialTicks){
        return ticks == lastPoseFetch && partialTicks == lastPartialTicks;
    }


    public IPose getCurrentPose(){
        return getCurrentPose(0);
    }

    public IPose getCurrentPose(float partialTicks){
        if (isSamePose(partialTicks)){
            return workFrame;
        }
        if (getCurrentState().equals(INVALID_STATE)){
            BoneTown.LOGGER.warn("Animation for entity: {} currently in invalid state", getEntity().toString());
            return DEFAULT_FRAME;
        }
        AnimationState<T> state = getState(getCurrentState());
        if (state != null){
            state.applyToPose(ticks, partialTicks, workFrame);
            lastPoseFetch = ticks;
            lastPartialTicks = partialTicks;
            return workFrame;
        } else {
            BoneTown.LOGGER.warn("Animation for entity: {} state not found: {}",
                    getEntity().toString(), getCurrentState());
            return DEFAULT_FRAME;
        }
    }

    public String getCurrentState() {
        return currentState;
    }


    @Nullable
    public AnimationState<T> getState(String state){
        return animationStates.get(state);
    }

    public T getEntity(){
        return entity;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("current_state", getCurrentState());
        return tag;
    }

    public void update() {
        ticks++;
    }

    public void setCurrentTicks(int ticks){
        this.ticks = ticks;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("current_state")){
            currentState = nbt.getString("current_state");
        } else {
            currentState = INVALID_STATE;
        }

    }


}
