package com.leon.bizclass.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Implemented by any block that can be sat on via SeatEntity. Lets the shared rider entity
 * clear the OCCUPIED flag on dismount without needing to know which concrete seat block it is.
 */
public interface SeatBlockLike {
    void clearOccupied(Level level, BlockPos pos, BlockState state);
}
