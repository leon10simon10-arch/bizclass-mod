package com.leon.bizclass.block;

import com.leon.bizclass.entity.SeatEntity;
import com.leon.bizclass.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * A single economy seat. Two style variants exist as separate registered blocks
 * (see ModBlocks): one with a fold-up armrest IFE screen in addition to the seatback
 * screen, and one with only the seatback screen. Both seat one player and open the
 * IFE menu with a normal (non-sneak) click while seated. No door, no recline — simpler
 * than the business suites, matching an economy cabin.
 */
public class EconomySeatBlock extends Block implements SeatBlockLike {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty OCCUPIED = BooleanProperty.create("occupied");

    private final boolean armrestIfe;

    public EconomySeatBlock(Properties properties, boolean armrestIfe) {
        super(properties);
        this.armrestIfe = armrestIfe;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OCCUPIED, false));
    }

    public boolean hasArmrestIfe() {
        return armrestIfe;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OCCUPIED);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(OCCUPIED, false);
    }

    // ---- Interaction: sit / stand, seated + normal click opens the seatback IFE screen ----
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            if (state.getValue(OCCUPIED) && !player.isShiftKeyDown()) {
                openSeatbackScreen();
            }
            return InteractionResult.SUCCESS;
        }

        if (state.getValue(OCCUPIED)) {
            // Non-sneak click while occupied just opens the seatback IFE screen (handled client-side above).
            return InteractionResult.CONSUME;
        }

        sitDown(level, pos, state, player);
        return InteractionResult.CONSUME;
    }

    @OnlyIn(Dist.CLIENT)
    private void openSeatbackScreen() {
        com.leon.bizclass.client.IfeScreenOpener.open();
    }

    private void sitDown(Level level, BlockPos pos, BlockState state, Player player) {
        SeatEntity seat = new SeatEntity(ModEntities.SEAT_ENTITY.get(), level);
        seat.setPos(pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5);
        seat.setSeatPos(pos);
        level.addFreshEntity(seat);
        player.startRiding(seat);
        level.setBlock(pos, state.setValue(OCCUPIED, true), Block.UPDATE_ALL);
        level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.4f, 1.0f);
    }

    @Override
    public void clearOccupied(Level level, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof EconomySeatBlock) {
            level.setBlock(pos, state.setValue(OCCUPIED, false), Block.UPDATE_ALL);
        }
    }
}
