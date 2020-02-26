package com.chaosbuffalo.bonetown.entity;



public interface IBTAnimatedEntity {


    String getCurrentAnimation();

    int getAnimationTicks();

    boolean doLoopAnimation();

}
