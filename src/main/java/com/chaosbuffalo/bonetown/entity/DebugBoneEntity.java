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

public class DebugBoneEntity extends Entity {

    private int timesSeen;
    private boolean initialized;

    public DebugBoneEntity(final EntityType<? extends DebugBoneEntity> entityType, final World world) {
        super(entityType, world);
        setBoundingBox(new AxisAlignedBB(-1D, -1D, -1.0D, 1.0D, 1.0D, 1.0D));
        BoneTown.LOGGER.info("Creating test entity");
        ignoreFrustumCheck = true;
        timesSeen = 0;
        initialized = false;
    }

    public DebugBoneEntity(World worldIn, double x, double y, double z){
        this(worldIn);
        setPosition(x, y, z);
    }

    public DebugBoneEntity(final World world) {
        this(ModEntityTypes.DEBUG_BONE_ENTITY.get(), world);
    }


    @Override
    protected void registerData() {

    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        timesSeen = compound.getInt("timesSeen");
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.putInt("timesSeen", timesSeen);
    }

    @Override
    public void tick() {
        super.tick();
        if (!initialized){
            timesSeen += 1;
            initialized = true;
        }
        if (timesSeen > 1){
            remove();
        }
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        BoneTown.LOGGER.info("Creating spawn packet {}", this.getPositionVec().toString());
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}