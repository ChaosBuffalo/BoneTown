package com.chaosbuffalo.bonetown.entity.animation_state;

import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import net.minecraft.entity.Entity;

public interface IHasAnimationComponent<T extends Entity & IBTAnimatedEntity> {

    AnimationComponent<T> getAnimationComponent();
}
