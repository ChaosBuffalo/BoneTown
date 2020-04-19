package com.chaosbuffalo.bonetown.entity.animation_state.layers;

import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import com.chaosbuffalo.bonetown.entity.animation_state.messages.layer.AnimationLayerMessage;
import net.minecraft.entity.Entity;

import java.util.function.Consumer;

public interface IAnimationLayer<T extends Entity & IBTAnimatedEntity> {

    void processLayer(IPose basePose, int currentTime, float partialTicks, IPose outPose);

    void setStartTime(int tick);

    int getStartTime();

    T getEntity();

    void setActive(boolean active);

    boolean shouldAutoStart();

    boolean shouldRun();

    void tick(int tick);

    boolean shouldLoop();

    int getDuration();

    void addEndCallback(Runnable callback);

    void consumeLayerMessage(AnimationLayerMessage message);

    String getLayerName();

}
