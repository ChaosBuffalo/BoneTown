package com.chaosbuffalo.bonetown.entity;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.bonemf.BoneMFSkeleton;
import com.chaosbuffalo.bonetown.core.model.BTAnimatedModel;
import com.chaosbuffalo.bonetown.entity.animation_state.AnimationComponent;
import com.chaosbuffalo.bonetown.entity.animation_state.AnimationState;
import com.chaosbuffalo.bonetown.entity.animation_state.layers.HeadTrackingLayer;
import com.chaosbuffalo.bonetown.entity.animation_state.layers.LocomotionLayer;
import com.chaosbuffalo.bonetown.init.BTEntityTypes;
import com.chaosbuffalo.bonetown.init.BTModels;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TestZombieEntity extends ZombieEntity implements IBTAnimatedEntity<TestZombieEntity>, IHasHandBones {

    private AnimationComponent<TestZombieEntity> animationComponent;
    BTAnimatedModel animatedModel;
    BoneMFSkeleton skeleton;
    private static final ResourceLocation IDLE_ANIM = new ResourceLocation(BoneTown.MODID, "biped.idle");
    private static final ResourceLocation RUN_ANIM = new ResourceLocation(BoneTown.MODID, "biped.running");

    public TestZombieEntity(final EntityType<? extends TestZombieEntity> type, final World worldIn) {
        super(type, worldIn);
        animatedModel = (BTAnimatedModel) BTModels.BIPED;
        skeleton = animatedModel.getSkeleton().orElse(null);
        animationComponent = new AnimationComponent<>(this);
        setupAnimationComponent();
    }

    protected void setupAnimationComponent() {
        AnimationState<TestZombieEntity> defaultState = new AnimationState<>("default", this);
        HeadTrackingLayer<TestZombieEntity> headTrackingLayer = new HeadTrackingLayer<>("head", this,
                "bn_head");
        LocomotionLayer<TestZombieEntity> locomotionLayer = new LocomotionLayer<>("locomotion",
                IDLE_ANIM, RUN_ANIM,
                this, true);
        defaultState.addLayer(locomotionLayer);
        defaultState.addLayer(headTrackingLayer);
        animationComponent.addAnimationState(defaultState);
        animationComponent.setState("default");
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
