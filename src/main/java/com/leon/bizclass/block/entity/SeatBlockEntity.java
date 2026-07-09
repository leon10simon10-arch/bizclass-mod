package com.leon.bizclass.block.entity;

import com.leon.bizclass.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SeatBlockEntity extends BlockEntity {

    @Nullable
    private BlockPos pairPos;

    public SeatBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SEAT_BLOCK_ENTITY.get(), pos, state);
    }

    @Nullable
    public BlockPos getPairPos() {
        return pairPos;
    }

    public void setPairPos(@Nullable BlockPos pairPos) {
        this.pairPos = pairPos;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (pairPos != null) {
            tag.putLong("PairPos", pairPos.asLong());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("PairPos")) {
            pairPos = BlockPos.of(tag.getLong("PairPos"));
        } else {
            pairPos = null;
        }
    }
}
