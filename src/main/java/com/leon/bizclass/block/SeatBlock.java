package com.leon.bizclass.block;

import com.leon.bizclass.block.entity.SeatBlockEntity;
import com.leon.bizclass.entity.SeatEntity;
import com.leon.bizclass.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import com.mojang.serialization.MapCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * A single business-class suite. Which of the six row positions it represents is stored in
 * SEAT_TYPE. Every suite has a working sliding door, a personal recline state (lie-flat) and
 * can seat one player. Pod suites (POD_LEFT / POD_RIGHT) share their door state with their
 * linked partner block so opening one side of the pod opens both.
 */
public class SeatBlock extends BaseEntityBlock implements SeatBlockLike {

    public static final MapCodec<SeatBlock> CODEC = simpleCodec(SeatBlock::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public static final EnumProperty<SeatType> SEAT_TYPE = EnumProperty.create("seat_type", SeatType.class);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty DOOR_OPEN = BooleanProperty.create("door_open");
    public static final BooleanProperty RECLINED = BooleanProperty.create("reclined");
    public static final BooleanProperty OCCUPIED = BooleanProperty.create("occupied");

    public SeatBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(SEAT_TYPE, SeatType.AISLE_LEFT)
                .setValue(FACING, Direction.NORTH)
                .setValue(DOOR_OPEN, false)
                .setValue(RECLINED, false)
                .setValue(OCCUPIED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SEAT_TYPE, FACING, DOOR_OPEN, RECLINED, OCCUPIED);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    // ---- Placement: auto-lock alternating window/aisle pattern with neighbours ----
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getHorizontalDirection().getOpposite();

        SeatType chosen = SeatType.AISLE_LEFT;

        Direction left = facing.getCounterClockWise();
        Direction right = facing.getClockWise();

        BlockState leftNeighbor = level.getBlockState(pos.relative(left));
        BlockState rightNeighbor = level.getBlockState(pos.relative(right));

        if (leftNeighbor.getBlock() instanceof SeatBlock) {
            SeatType neighborType = leftNeighbor.getValue(SEAT_TYPE);
            if (neighborType.isLeftSingle() || neighborType.isRightSingle()) {
                chosen = neighborType.alternateSingle();
            } else if (neighborType.isPod()) {
                chosen = SeatType.AISLE_RIGHT;
            }
        } else if (rightNeighbor.getBlock() instanceof SeatBlock) {
            SeatType neighborType = rightNeighbor.getValue(SEAT_TYPE);
            if (neighborType.isLeftSingle() || neighborType.isRightSingle()) {
                chosen = neighborType.alternateSingle();
            } else if (neighborType.isPod()) {
                chosen = SeatType.AISLE_LEFT;
            }
        }

        return this.defaultBlockState()
                .setValue(SEAT_TYPE, chosen)
                .setValue(FACING, facing)
                .setValue(DOOR_OPEN, false)
                .setValue(RECLINED, false)
                .setValue(OCCUPIED, false);
    }

    // ---- Interaction: sit / stand, sneak = toggle door, sneak while seated = recline,
    //      seated + normal click = open the seatback IFE screen ----
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            if (state.getValue(OCCUPIED) && !player.isShiftKeyDown()) {
                openSeatbackScreen();
            }
            return InteractionResult.SUCCESS;
        }

        if (player.isShiftKeyDown() && !state.getValue(OCCUPIED)) {
            toggleDoor(level, pos, state);
            return InteractionResult.CONSUME;
        }

        if (state.getValue(OCCUPIED)) {
            if (player.isShiftKeyDown()) {
                toggleRecline(level, pos, state);
            }
            // Non-sneak click while occupied just opens the seatback IFE screen (handled client-side above).
            return InteractionResult.CONSUME;
        }

        if (!state.getValue(DOOR_OPEN)) {
            toggleDoor(level, pos, state);
        }

        sitDown(level, pos, state, player);
        return InteractionResult.CONSUME;
    }

    @OnlyIn(Dist.CLIENT)
    private void openSeatbackScreen() {
        com.leon.bizclass.client.IfeScreenOpener.open();
    }

    private void toggleDoor(Level level, BlockPos pos, BlockState state) {
        boolean newDoorState = !state.getValue(DOOR_OPEN);
        level.setBlock(pos, state.setValue(DOOR_OPEN, newDoorState), Block.UPDATE_ALL);
        level.playSound(null, pos, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundSource.BLOCKS, 0.6f, 1.3f);

        // Pods share one door: propagate to the linked partner block if present.
        if (state.getValue(SEAT_TYPE).isPod()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SeatBlockEntity seatEntity && seatEntity.getPairPos() != null) {
                BlockPos pairPos = seatEntity.getPairPos();
                BlockState pairState = level.getBlockState(pairPos);
                if (pairState.getBlock() instanceof SeatBlock) {
                    level.setBlock(pairPos, pairState.setValue(DOOR_OPEN, newDoorState), Block.UPDATE_ALL);
                }
            }
        }
    }

    private void toggleRecline(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(RECLINED, !state.getValue(RECLINED)), Block.UPDATE_ALL);
        level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.5f, 0.8f);
    }

    private void sitDown(Level level, BlockPos pos, BlockState state, Player player) {
        SeatEntity seat = new SeatEntity(ModEntities.SEAT_ENTITY.get(), level);
        seat.setPos(pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5);
        seat.setSeatPos(pos);
        level.addFreshEntity(seat);
        player.startRiding(seat);
        level.setBlock(pos, state.setValue(OCCUPIED, true), Block.UPDATE_ALL);
    }

    /** Called by SeatEntity when the player dismounts, to clear the OCCUPIED flag. */
    @Override
    public void clearOccupied(Level level, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof SeatBlock) {
            level.setBlock(pos, state.setValue(OCCUPIED, false).setValue(RECLINED, false), Block.UPDATE_ALL);
        }
    }

    // ---- Block entity: stores the pod-pair link ----
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SeatBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return null;
    }
}
