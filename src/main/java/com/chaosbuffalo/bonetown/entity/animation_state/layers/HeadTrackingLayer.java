package com.chaosbuffalo.bonetown.entity.animation_state.layers;

import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.chaosbuffalo.bonetown.core.bonemf.BoneMFNode;
import com.chaosbuffalo.bonetown.core.bonemf.BoneMFSkeleton;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import com.chaosbuffalo.bonetown.entity.animation_state.layers.AnimationLayerBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4d;
import org.joml.Vector4d;

public class HeadTrackingLayer<T extends LivingEntity & IBTAnimatedEntity> extends AnimationLayerBase<T> {

    private final String boneName;

    public HeadTrackingLayer(String name, T entity, String headBoneName){
        super(name, entity);
        boneName = headBoneName;
    }

    public String getBoneName(){
        return boneName;
    }


    @Override
    public void doLayerWork(IPose basePose, int currentTime, float partialTicks, IPose outPose) {
        BoneMFSkeleton skeleton = getEntity().getSkeleton();
        // Would only be called if we are isValid(), null check is performed
        BoneMFNode bone = skeleton.getBone(getBoneName());
        T entity = getEntity();
        if (bone != null) {
            float f = MathHelper.interpolateAngle(partialTicks, entity.prevRenderYawOffset, entity.renderYawOffset);
            float f1 = MathHelper.interpolateAngle(partialTicks, entity.prevRotationYawHead, entity.rotationYawHead);
            boolean shouldSit = entity.isPassenger() && (entity.getRidingEntity() != null &&
                    entity.getRidingEntity().shouldRiderSit());
            float netHeadYaw = f1 - f;
            if (shouldSit && entity.getRidingEntity() instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity) entity.getRidingEntity();
                f = MathHelper.interpolateAngle(partialTicks, livingentity.prevRenderYawOffset,
                        livingentity.renderYawOffset);
                netHeadYaw = f1 - f;
                float f3 = MathHelper.wrapDegrees(netHeadYaw);
                if (f3 < -85.0F) {
                    f3 = -85.0F;
                }

                if (f3 >= 85.0F) {
                    f3 = 85.0F;
                }

                f = f1 - f3;
                if (f3 * f3 > 2500.0F) {
                    f += f3 * 0.2F;
                }
                netHeadYaw = f1 - f;
            }
            float headPitch = MathHelper.lerp(partialTicks, entity.prevRotationPitch, entity.rotationPitch);
            float rotateY = netHeadYaw * ((float) Math.PI / 180F);
            boolean isFlying = entity.getTicksElytraFlying() > 4;
            boolean isSwimming = entity.isActualySwimming();
            float rotateX;
            if (isFlying) {
                rotateX = (-(float) Math.PI / 4F);
//                } else if (this.swimAnimation > 0.0F) {
//                    if (isSwimming) {
//                        this.bipedHead.rotateAngleX = this.rotLerpRad(this.bipedHead.rotateAngleX, (-(float)Math.PI / 4F), this.swimAnimation);
//                    } else {
//                        this.bipedHead.rotateAngleX = this.rotLerpRad(this.bipedHead.rotateAngleX, headPitch * ((float)Math.PI / 180F), this.swimAnimation);
//                    }
            } else {
                rotateX = headPitch * ((float) Math.PI / 180F);
            }
            Vector4d headRotation = new Vector4d(rotateX, rotateY, 0.0, 1.0);
            Matrix4d headTransform = bone.calculateLocalTransform(new Vector4d(0.0, 0.0, 0.0, 1.0),
                    headRotation, new Vector4d(1.0, 1.0, 1.0, 1.0));
            int boneId = skeleton.getBoneId(getBoneName());
            int parentBoneId = skeleton.getBoneParentId(getBoneName());
            Matrix4d parentTransform;
            if (parentBoneId != -1) {
                parentTransform = new Matrix4d(outPose.getJointMatrix(parentBoneId));
            } else {
                parentTransform = new Matrix4d();
            }
            outPose.setJointMatrix(boneId, parentTransform.mulAffine(headTransform));
        }
    }
}
