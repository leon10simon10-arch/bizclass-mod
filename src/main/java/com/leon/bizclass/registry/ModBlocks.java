package com.leon.bizclass.registry;

import com.leon.bizclass.BizClassMod;
import com.leon.bizclass.block.IfeScreenBlock;
import com.leon.bizclass.block.SeatBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(BizClassMod.MOD_ID);

    // One block registration; the six suite positions are selected via the SEAT_TYPE blockstate
    // property (see SeatType) so window/aisle/pod suites all come from the same block.
    public static final DeferredBlock<SeatBlock> BUSINESS_SEAT = BLOCKS.register("business_seat",
            () -> new SeatBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLUE)
                    .strength(2.0f)
                    .sound(SoundType.WOOL)
                    .noOcclusion()));

    public static final DeferredBlock<IfeScreenBlock> IFE_SCREEN = BLOCKS.register("ife_screen",
            () -> new IfeScreenBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(1.5f)
                    .sound(SoundType.METAL)
                    .noOcclusion()));

    public static final DeferredBlock<com.leon.bizclass.block.EconomySeatBlock> ECONOMY_SEAT_ARMREST_IFE =
            BLOCKS.register("economy_seat_armrest_ife",
                    () -> new com.leon.bizclass.block.EconomySeatBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_LIGHT_GRAY)
                            .strength(1.5f)
                            .sound(SoundType.WOOL)
                            .noOcclusion(), true));

    public static final DeferredBlock<com.leon.bizclass.block.EconomySeatBlock> ECONOMY_SEAT_BACK_ONLY =
            BLOCKS.register("economy_seat_back_only",
                    () -> new com.leon.bizclass.block.EconomySeatBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_LIGHT_GRAY)
                            .strength(1.5f)
                            .sound(SoundType.WOOL)
                            .noOcclusion(), false));
}
