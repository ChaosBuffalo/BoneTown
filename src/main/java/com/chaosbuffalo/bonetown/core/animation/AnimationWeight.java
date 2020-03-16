package com.chaosbuffalo.bonetown.core.animation;

public class AnimationWeight {

    public IAnimationProvider provider;
    public float weight;

    public AnimationWeight(IAnimationProvider provider, float weight){
        this.provider = provider;
        this.weight = weight;
    }
}
