package com.chaosbuffalo.bonetown.core.animation_data;


import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;


public class BTSkeleton {

    public static int MAX_WEIGHTS = 4;
    private final Map<String, BTAnimation> animations;
    private final List<BTBone> bones;
    private final BTNode rootNode;

    public BTSkeleton(List<BTBone> bones, Map<String, BTAnimation> animations, BTNode rootNode){
        this.animations = animations;
        this.bones = bones;
        this.rootNode = rootNode;
    }

    public List<BTBone> getBones(){
        return bones;
    }

    public void addAnimation(String name, BTAnimation animation){
        this.animations.put(name, animation);
    }

    public BTNode getRootNode(){
        return rootNode;
    }

    @Nullable
    public BTAnimation getAnimation(String name){
        return animations.get(name);
    }



}
