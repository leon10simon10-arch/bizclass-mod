package com.leon.bizclass.registry;

import com.leon.bizclass.BizClassMod;
import com.leon.bizclass.block.entity.SeatBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(net.minecraft.core.registries.BuiltInRegistries.BLOCK_ENTITY_TYPE.key(), BizClassMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SeatBlockEntity>> SEAT_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("seat", () -> BlockEntityType.Builder.of(
                    SeatBlockEntity::new, ModBlocks.BUSINESS_SEAT.get()).build(null));
}
