package com.chaosbuffalo.bonetown.entity;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.bonemf.BoneMFNode;
import com.chaosbuffalo.bonetown.core.bonemf.BoneMFSkeleton;
import com.chaosbuffalo.bonetown.core.model.BTAnimatedModel;
import com.chaosbuffalo.bonetown.entity.animation_state.AnimationComponent;
import com.chaosbuffalo.bonetown.entity.animation_state.AnimationState;
import com.chaosbuffalo.bonetown.entity.animation_state.FullBodyPoseLayer;
import com.chaosbuffalo.bonetown.init.BTEntityTypes;
import com.chaosbuffalo.bonetown.init.BTModels;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import javax.annotation.Nullable;


public class TestAnimatedEntity extends Entity implements IBTAnimatedEntity<TestAnimatedEntity> {

    private boolean bonesDrawn;
    BTAnimatedModel animatedModel;
    BoneMFSkeleton skeleton;
    private AnimationComponent<TestAnimatedEntity> animationComponent;
    private static final ResourceLocation TEST_ANIM = new ResourceLocation(BoneTown.MODID, "biped.running");

    public TestAnimatedEntity(final EntityType<? extends TestAnimatedEntity> entityType, final World world) {
        super(entityType, world);
        setBoundingBox(new AxisAlignedBB(-1D, -1D, -1.0D, 1.0D, 1.0D, 1.0D));
        BoneTown.LOGGER.info("Creating test animated entity");
        ignoreFrustumCheck = true;
        bonesDrawn = false;
        animatedModel = (BTAnimatedModel) BTModels.BIPED;
        skeleton = animatedModel.getSkeleton().orElse(null);
        animationComponent = new AnimationComponent<>(this);
        setupAnimationComponent();
    }

    protected void setupAnimationComponent(){
        FullBodyPoseLayer<TestAnimatedEntity> testAnim = new FullBodyPoseLayer<>("test", TEST_ANIM,
                this,true);
        AnimationState<TestAnimatedEntity> testState = new AnimationState<>("test_state", this);
        testState.addLayer(testAnim);
        animationComponent.addAnimationState(testState);
        animationComponent.setState("test_state");

    }

    public TestAnimatedEntity(World worldIn, double x, double y, double z){
        this(worldIn);
        setPosition(x, y, z);
    }

    public TestAnimatedEntity(final World world) {
        this(BTEntityTypes.TEST_ANIMATED_ENTITY.get(), world);
    }


    public void createBoneDebug(World worldIn){
        if (!worldIn.isRemote()){
            BoneMFSkeleton skeleton = getSkeleton();
            if (skeleton == null){
                return;
            }
            for (BoneMFNode bone : skeleton.getBones()){
                Vector3f position = new Vector3f();
                Matrix4f boneTransform = new Matrix4f(bone.calculateGlobalTransform());

                Matrix4f posMatrix = new Matrix4f();
                posMatrix.translate((float)getPosX(), (float)getPosY(), (float)getPosZ());
                posMatrix.mul(boneTransform);
                posMatrix.getTranslation(position);


                Entity entity = BTEntityTypes.DEBUG_BONE_ENTITY.get().create(worldIn);

                if (entity != null){
                    entity.setGlowing(true);
                    BoneTown.LOGGER.info("Spawning bone {} at {}", bone.getName(), position.toString());
                    entity.setPosition( position.x, position.y, position.z);
                    entity.setCustomName(new StringTextComponent(bone.getName()));
                    entity.setCustomNameVisible(true);
                    worldIn.addEntity(entity);
                }
            }
        }
    }


    @Override
    public void tick() {
        super.tick();
        if (!bonesDrawn){
            createBoneDebug(getEntityWorld());
            bonesDrawn = true;
        }
        animationComponent.update();
    }

    @Override
    public AnimationComponent<TestAnimatedEntity> getAnimationComponent() {
        return animationComponent;
    }


    @Nullable
    @Override
    public BoneMFSkeleton getSkeleton() {
        return skeleton;
    }


    @Override
    protected void registerData() {

    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        if (compound.contains("anim_component")){
            animationComponent.deserializeNBT(compound.getCompound("anim_component"));
        }
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.put("anim_component", animationComponent.serializeNBT());

    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
