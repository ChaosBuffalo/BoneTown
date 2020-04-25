package com.chaosbuffalo.bonetown.entity.animation_state.layers;

import com.chaosbuffalo.bonetown.core.animation.*;
import com.chaosbuffalo.bonetown.core.bonemf.BoneMFSkeleton;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import org.joml.Matrix4d;

public class SubTreePoseLayer<T extends Entity & IBTAnimatedEntity<T>> extends LayerWithAnimation<T> {
    private boolean shouldLoop;
    private final LocalSubTreeBlend localBlend;
    private final List<Integer> boneIds;
    private final BoneMFSkeleton skeleton;

    public SubTreePoseLayer(String name, ResourceLocation animName, T entity, boolean shouldLoop, String boneName){
        super(name, animName, entity);
        this.shouldLoop = shouldLoop;
        skeleton = entity.getSkeleton();
        if (skeleton != null){
            boneIds = skeleton.getBoneIdsOfSubTree(boneName);
        } else {
            boneIds = new ArrayList<>();
        }
        this.localBlend = new LocalSubTreeBlend(boneIds);
    }

    @Override
    public boolean shouldLoop() {
        return shouldLoop;
    }

    @Override
    void doLayerWork(IPose basePose, int currentTime, float partialTicks, IPose outPose) {
        BakedAnimation animation = getAnimation(BASE_SLOT);
        if (animation != null){
            InterpolationFramesReturn ret = animation.getInterpolationFrames(
                    currentTime - getStartTime(), shouldLoop(), partialTicks);
            localBlend.setFrames(ret);
            IPose localPose = localBlend.getPose();
            for (int id : boneIds){
                int parentId = skeleton.getBoneIdParentId(id);
                Matrix4d parentGlobalLoc;
                if (parentId != -1){
                    parentGlobalLoc = outPose.getJointMatrix(parentId);
                } else {
                    parentGlobalLoc = new Matrix4d();
                }
                outPose.setJointMatrix(id, parentGlobalLoc);
                outPose.getJointMatrix(id).mulAffine(localPose.getJointMatrix(id));
            }
        }
    }
}
