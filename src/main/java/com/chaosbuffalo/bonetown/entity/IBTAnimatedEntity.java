package com.chaosbuffalo.bonetown.entity;


import com.chaosbuffalo.bonetown.core.bonemf.BoneMFSkeleton;

import java.util.Optional;

public interface IBTAnimatedEntity {


    String getCurrentAnimation();

    int getAnimationTicks();

    boolean doLoopAnimation();

    Optional<BoneMFSkeleton> getSkeleton();


}
