package com.chaosbuffalo.bonetown.core.animation_data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BTSkeleton {

    public static int MAX_WEIGHTS = 4;
    private final Map<String, BTAnimation> animations;
    private final List<BTBone> bones;
    private final BTNode nodeHierarchy;

    public BTSkeleton(List<BTBone> bones, Map<String, BTAnimation> animations, BTNode nodeHierarchy){
        this.animations = animations;
        this.bones = bones;
        this.nodeHierarchy = nodeHierarchy;
    }


    public List<BTBone> getBones(){
        return bones;
    }

    public void addAnimation(String name, BTAnimation animation){
        this.animations.put(name, animation);
    }

}
