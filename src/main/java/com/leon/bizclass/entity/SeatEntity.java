package com.leon.bizclass.entity;

import com.leon.bizclass.block.SeatBlockLike;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Invisible, non-solid single-passenger "chair" entity. One is spawned when a player sits down
 * on a SeatBlock and removed (clearing the block's OCCUPIED flag) when they dismount.
 */
public class SeatEntity extends Entity {

    private static final EntityDataAccessor<Optional<BlockPos>> SEAT_POS =
            SynchedEntityData.defineId(SeatEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);

    private int lifeTicks = 0;

    public SeatEntity(EntityType<? extends SeatEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setInvisible(true);
    }

    public void setSeatPos(BlockPos pos) {
        this.entityData.set(SEAT_POS, Optional.of(pos));
    }

    @Nullable
    public BlockPos getSeatPos() {
        return this.entityData.get(SEAT_POS).orElse(null);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SEAT_POS, Optional.empty());
    }

    @Override
    public void tick() {
        super.tick();
        lifeTicks++;

        BlockPos pos = getSeatPos();
        if (pos == null) {
            this.discard();
            return;
        }

        // Safety net: if the block here is no longer a seat, or nobody is riding after the first tick, clean up.
        if (!(this.level().getBlockState(pos).getBlock() instanceof SeatBlockLike)) {
            standAndRemove();
            return;
        }

        if (lifeTicks > 1 && this.getPassengers().isEmpty()) {
            standAndRemove();
        }
    }

    private void standAndRemove() {
        BlockPos pos = getSeatPos();
        if (!this.level().isClientSide && pos != null) {
            var state = this.level().getBlockState(pos);
            if (state.getBlock() instanceof SeatBlockLike seatLike) {
                seatLike.clearOccupied(this.level(), pos, state);
            }
        }
        this.discard();
    }

    @Override
    public void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        standAndRemove();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        // Seats are transient (noSave) — nothing persisted.
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        // Seats are transient (noSave) — nothing persisted.
    }

    @Override
    public boolean isPushable() {
        return false;
    }
}
