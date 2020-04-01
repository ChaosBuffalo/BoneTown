package com.chaosbuffalo.bonetown.entity;


import com.chaosbuffalo.bonetown.core.bonemf.BoneMFSkeleton;
import com.chaosbuffalo.bonetown.entity.animation_state.AnimationComponent;
import net.minecraft.entity.Entity;

import javax.annotation.Nullable;


public interface IBTAnimatedEntity<T extends Entity & IBTAnimatedEntity<T>> {

    AnimationComponent<T> getAnimationComponent();

    @Nullable
    BoneMFSkeleton getSkeleton();

}
