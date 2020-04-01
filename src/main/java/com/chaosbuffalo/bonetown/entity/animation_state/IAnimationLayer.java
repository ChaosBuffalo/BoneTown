package com.chaosbuffalo.bonetown.entity.animation_state;

import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import net.minecraft.entity.Entity;

public interface IAnimationLayer<T extends Entity & IBTAnimatedEntity> {

    void processLayer(IPose basePose, int currentTime, float partialTicks, IPose outPose);

    void setStartTime(int tick);

    int getStartTime();

    T getEntity();

    void setActive(boolean active);

    boolean shouldAutoStart();

    boolean shouldRun();

    String getLayerName();

}
