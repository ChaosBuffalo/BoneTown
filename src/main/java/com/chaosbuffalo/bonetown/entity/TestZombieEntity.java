package com.chaosbuffalo.bonetown.entity;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.chaosbuffalo.bonetown.core.bonemf.BoneMFSkeleton;
import com.chaosbuffalo.bonetown.core.model.BTAnimatedModel;
import com.chaosbuffalo.bonetown.entity.animation_state.AnimationComponent;
import com.chaosbuffalo.bonetown.entity.animation_state.AnimationState;
import com.chaosbuffalo.bonetown.entity.animation_state.AnimationUtils;
import com.chaosbuffalo.bonetown.entity.animation_state.layers.FullBodyPoseLayer;
import com.chaosbuffalo.bonetown.entity.animation_state.layers.HeadTrackingLayer;
import com.chaosbuffalo.bonetown.entity.animation_state.layers.LocomotionLayer;
import com.chaosbuffalo.bonetown.entity.animation_state.layers.SubTreePoseLayer;
import com.chaosbuffalo.bonetown.entity.animation_state.messages.PopStateMessage;
import com.chaosbuffalo.bonetown.entity.animation_state.messages.PushStateMessage;
import com.chaosbuffalo.bonetown.init.BTEntityTypes;
import com.chaosbuffalo.bonetown.init.BTModels;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TestZombieEntity extends ZombieEntity implements IBTAnimatedEntity<TestZombieEntity>, IHasHandBones {

    private AnimationComponent<TestZombieEntity> animationComponent;
    BTAnimatedModel animatedModel;
    BoneMFSkeleton skeleton;
    private static final ResourceLocation IDLE_ANIM = new ResourceLocation(BoneTown.MODID, "biped.idle");
    private static final ResourceLocation RUN_ANIM = new ResourceLocation(BoneTown.MODID, "biped.running");
    private static final ResourceLocation ZOMBIE_ARMS_ANIM = new ResourceLocation(BoneTown.MODID,
            "biped.zombie_arms");
    private static final ResourceLocation BACKFLIP_ANIM = new ResourceLocation(BoneTown.MODID,
            "biped.backflip");

    public TestZombieEntity(final EntityType<? extends TestZombieEntity> type, final World worldIn) {
        super(type, worldIn);
        animatedModel = (BTAnimatedModel) BTModels.BIPED;
        skeleton = animatedModel.getSkeleton().orElse(null);
        animationComponent = new AnimationComponent<>(this);
        setupAnimationComponent();
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return getAnimationComponent().applyRootMotionToBoundingBox(super.getBoundingBox());
    }


    @Override
    public ActionResultType applyPlayerInteraction(PlayerEntity player, Vec3d vec, Hand hand) {
        if (!getEntityWorld().isRemote() && hand.equals(player.getActiveHand())){
            animationComponent.updateState(new PushStateMessage("flip"));
            setNoAI(true);
        }
        return super.applyPlayerInteraction(player, vec, hand);
    }

    protected void setupAnimationComponent() {
        AnimationState<TestZombieEntity> defaultState = new AnimationState<>("default", this);
        HeadTrackingLayer<TestZombieEntity> headTrackingLayer = new HeadTrackingLayer<>("head", this,
                "bn_head");
        LocomotionLayer<TestZombieEntity> locomotionLayer = new LocomotionLayer<>("locomotion",
                IDLE_ANIM, RUN_ANIM,
                this, true);
        SubTreePoseLayer<TestZombieEntity> armsLayer = new SubTreePoseLayer<>("arms",
                ZOMBIE_ARMS_ANIM, this, true, "bn_chest");
        defaultState.addLayer(locomotionLayer);
        defaultState.addLayer(armsLayer);
        defaultState.addLayer(headTrackingLayer);
        animationComponent.addAnimationState(defaultState);
        animationComponent.pushState("default");
        AnimationState<TestZombieEntity> flipState = new AnimationState<>("flip", this, boundingBox -> {
            IPose pose = getAnimationComponent().getCurrentPose();
            AxisAlignedBB poseBox = AnimationUtils.GetBBoxForPose(pose);
            double diff = 1.75 - (poseBox.maxY - poseBox.minY);
            if (diff > 0){
                return boundingBox.contract(0.0, diff / 2.0, 0.0).contract(0.0, -diff / 2.0, 0.0);
            } else {
                return boundingBox;
            }
        });
        FullBodyPoseLayer<TestZombieEntity> flipLayer = new FullBodyPoseLayer<>("flip", BACKFLIP_ANIM,
                this, false);
        flipState.addLayer(flipLayer);
        flipLayer.setEndCallback(() -> {
            World world = getEntityWorld();
            if (!world.isRemote()){
                animationComponent.updateState(new PopStateMessage());
                setNoAI(false);
            }
        });
        animationComponent.addAnimationState(flipState);
    }

    public TestZombieEntity(World worldIn, double x, double y, double z){
        this(worldIn);
        setPosition(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();
        animationComponent.update();
    }

    public TestZombieEntity(final World world) {
        this(BTEntityTypes.TEST_ZOMBIE.get(), world);
    }

    @Override
    public AnimationComponent<TestZombieEntity> getAnimationComponent() {
        return animationComponent;
    }

    @Nullable
    @Override
    public BoneMFSkeleton getSkeleton() {
        return skeleton;
    }

    @Override
    public String getRightHandBoneName() {
        return "bn_hand_r";
    }

    @Override
    public String getLeftHandBoneName() {
        return "bn_hand_l";
    }
}
