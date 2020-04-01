package com.chaosbuffalo.bonetown.entity;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.init.BTEntityTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class TestEntity extends Entity {

    public TestEntity(final EntityType<? extends TestEntity> entityType, final World world) {
        super(entityType, world);
        setBoundingBox(new AxisAlignedBB(-1D, -1D, -1.0D, 1.0D, 1.0D, 1.0D));
        BoneTown.LOGGER.info("Creating test entity");
        ignoreFrustumCheck = true;
    }

    public TestEntity(World worldIn, double x, double y, double z){
        this(worldIn);
        setPosition(x, y, z);
    }

    public TestEntity(final World world) {
        this(BTEntityTypes.TEST_ENTITY.get(), world);
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
        BoneTown.LOGGER.info("Creating spawn packet {}", this.getPosition().toString());
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
