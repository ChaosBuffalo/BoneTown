package com.chaosbuffalo.bonetown.entity;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.animation.BTSkeleton;
import com.chaosbuffalo.bonetown.core.assimp.nodes.BTBoneNode;
import com.chaosbuffalo.bonetown.core.bonemf.BoneMFNode;
import com.chaosbuffalo.bonetown.core.bonemf.BoneMFSkeleton;
import com.chaosbuffalo.bonetown.core.model.BTAnimatedModel;
import com.chaosbuffalo.bonetown.init.ModEntityTypes;
import com.chaosbuffalo.bonetown.init.ModMeshData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Optional;


public class TestAnimatedEntity extends Entity implements IBTAnimatedEntity {

    private int animationTicks;
    private boolean bonesDrawn;
    BTAnimatedModel animatedModel;

    public TestAnimatedEntity(final EntityType<? extends TestAnimatedEntity> entityType, final World world) {
        super(entityType, world);
        setBoundingBox(new AxisAlignedBB(-1D, -1D, -1.0D, 1.0D, 1.0D, 1.0D));
        BoneTown.LOGGER.info("Creating test animated entity");
        ignoreFrustumCheck = true;
        animationTicks = 0;
        bonesDrawn = false;
        animatedModel = (BTAnimatedModel) ModMeshData.BIPED;
    }

    public TestAnimatedEntity(World worldIn, double x, double y, double z){
        this(worldIn);
        setPosition(x, y, z);
    }

    public TestAnimatedEntity(final World world) {
        this(ModEntityTypes.TEST_ANIMATED_ENTITY.get(), world);
    }


    public void createBoneDebug(World worldIn){
        if (!worldIn.isRemote()){
            getSkeleton().ifPresent((BoneMFSkeleton skeleton) -> {
                for (BoneMFNode bone : skeleton.getBones()){
                    Vector3f position = new Vector3f();
                    Matrix4f boneTransform = new Matrix4f(bone.getGlobalTransform());

                    Matrix4f posMatrix = new Matrix4f();
                    posMatrix.translate((float)getPosX(), (float)getPosY(), (float)getPosZ());
                    posMatrix.mul(boneTransform);
                    posMatrix.getTranslation(position);


                    Entity entity = ModEntityTypes.DEBUG_BONE_ENTITY.get().create(worldIn);

                    if (entity != null){
                        entity.setGlowing(true);
                        BoneTown.LOGGER.info("Spawning bone {} at {}", bone.getName(), position.toString());
                        entity.setPosition( position.x, position.y, position.z);
                        entity.setCustomName(new StringTextComponent(bone.getName()));
                        entity.setCustomNameVisible(true);
                        worldIn.addEntity(entity);
                    }
                }
            });



        }

    }


    @Override
    public void tick() {
        super.tick();
        animationTicks++;
        if (!bonesDrawn){
            createBoneDebug(getEntityWorld());
            bonesDrawn = true;
        }
    }

    @Override
    public String getCurrentAnimation() {
        return "running2";
    }

    @Override
    public int getAnimationTicks() {
        return animationTicks;
    }

    @Override
    public boolean doLoopAnimation() {
        return true;
    }

    @Override
    public Optional<BoneMFSkeleton> getSkeleton() {
        return animatedModel.getSkeleton();
    }


    @Override
    protected void registerData() {

    }

    @Override
    protected void readAdditional(CompoundNBT compound) {

    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {

    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
