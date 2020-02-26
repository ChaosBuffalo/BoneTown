package com.chaosbuffalo.bonetown.entity;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.init.ModEntityTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;


public class TestAnimatedEntity extends Entity implements IBTAnimatedEntity {

    private int animationTicks;

    public TestAnimatedEntity(final EntityType<? extends TestAnimatedEntity> entityType, final World world) {
        super(entityType, world);
        setBoundingBox(new AxisAlignedBB(-1D, -1D, -1.0D, 1.0D, 1.0D, 1.0D));
        BoneTown.LOGGER.info("Creating test animated entity");
        ignoreFrustumCheck = true;
        animationTicks = 0;
    }

    public TestAnimatedEntity(World worldIn, double x, double y, double z){
        this(worldIn);
        setPosition(x, y, z);
    }

    public TestAnimatedEntity(final World world) {
        this(ModEntityTypes.TEST_ANIMATED_ENTITY.get(), world);
    }


    @Override
    public void tick() {
        super.tick();
        animationTicks++;
        BoneTown.LOGGER.info("Ticks is {}", animationTicks);
    }

    @Override
    public String getCurrentAnimation() {
        return "SpiderWalk";
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
