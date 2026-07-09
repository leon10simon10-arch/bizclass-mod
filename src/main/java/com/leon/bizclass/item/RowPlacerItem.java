package com.leon.bizclass.item;

import com.leon.bizclass.block.SeatBlock;
import com.leon.bizclass.block.SeatType;
import com.leon.bizclass.block.entity.SeatBlockEntity;
import com.leon.bizclass.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Right-click a floor block to lay down the whole locked-in business row in one go:
 * WINDOW_LEFT - AISLE_LEFT - POD_LEFT - POD_RIGHT - AISLE_RIGHT - WINDOW_RIGHT,
 * six seat blocks (four alternating singles + the shared double pod), correctly
 * oriented and paired so every door works and nothing can drift out of alignment.
 */
public class RowPlacerItem extends Item {

    private static final SeatType[] ROW_ORDER = {
            SeatType.WINDOW_LEFT, SeatType.AISLE_LEFT,
            SeatType.POD_LEFT, SeatType.POD_RIGHT,
            SeatType.AISLE_RIGHT, SeatType.WINDOW_RIGHT
    };

    public RowPlacerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.FAIL;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockPos basePos = context.getClickedPos().relative(context.getClickedFace());
        Direction forward = player.getDirection();
        Direction rowAxis = forward.getClockWise();
        Direction leftDoor = forward.getCounterClockWise();
        Direction rightDoor = forward.getClockWise();

        // Validate the whole span is clear before placing anything.
        for (int i = 0; i < ROW_ORDER.length; i++) {
            BlockPos checkPos = basePos.relative(rowAxis, i);
            BlockState existing = level.getBlockState(checkPos);
            if (!existing.canBeReplaced()) {
                player.displayClientMessage(
                        Component.literal("Not enough clear space for the full row (needs 6 blocks in a line)."), true);
                return InteractionResult.FAIL;
            }
        }

        BlockPos[] placedPositions = new BlockPos[ROW_ORDER.length];

        for (int i = 0; i < ROW_ORDER.length; i++) {
            SeatType type = ROW_ORDER[i];
            BlockPos pos = basePos.relative(rowAxis, i);
            placedPositions[i] = pos;

            Direction doorFacing = type.isLeftSingle() ? leftDoor
                    : type.isRightSingle() ? rightDoor
                    : forward;

            BlockState state = ModBlocks.BUSINESS_SEAT.get().defaultBlockState()
                    .setValue(SeatBlock.SEAT_TYPE, type)
                    .setValue(SeatBlock.FACING, doorFacing)
                    .setValue(SeatBlock.DOOR_OPEN, false)
                    .setValue(SeatBlock.RECLINED, false)
                    .setValue(SeatBlock.OCCUPIED, false);

            level.setBlock(pos, state, Block.UPDATE_ALL);
        }

        // Link the pod pair so their door opens/closes together.
        linkPair(level, placedPositions[2], placedPositions[3]);

        if (!player.getAbilities().instabuild) {
            context.getItemInHand().shrink(0); // row placer is reusable, not consumed
        }

        player.displayClientMessage(Component.literal(
                "Business row placed: window-aisle-pod-pod-aisle-window, all locked in."), true);

        return InteractionResult.CONSUME;
    }

    private void linkPair(Level level, BlockPos a, BlockPos b) {
        BlockEntity beA = level.getBlockEntity(a);
        BlockEntity beB = level.getBlockEntity(b);
        if (beA instanceof SeatBlockEntity seatA) {
            seatA.setPairPos(b);
        }
        if (beB instanceof SeatBlockEntity seatB) {
            seatB.setPairPos(a);
        }
    }
}
