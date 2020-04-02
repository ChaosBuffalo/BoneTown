package com.chaosbuffalo.bonetown.entity.animation_state.layers;

import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import com.chaosbuffalo.bonetown.entity.animation_state.messages.AnimationLayerMessage;
import net.minecraft.entity.Entity;

public interface IAnimationLayer<T extends Entity & IBTAnimatedEntity> {

    void processLayer(IPose basePose, int currentTime, float partialTicks, IPose outPose);

    void setStartTime(int tick);

    int getStartTime();

    T getEntity();

    void setActive(boolean active);

    boolean shouldAutoStart();

    boolean shouldRun();

    void receiveLayerMessage(AnimationLayerMessage message);

    String getLayerName();

}
