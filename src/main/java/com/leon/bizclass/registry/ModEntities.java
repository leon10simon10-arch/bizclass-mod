package com.leon.bizclass.registry;

import com.leon.bizclass.BizClassMod;
import com.leon.bizclass.entity.SeatEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.key(), BizClassMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<SeatEntity>> SEAT_ENTITY =
            ENTITY_TYPES.register("seat_rider", () -> EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC)
                    .sized(0.1f, 0.1f)
                    .noSave()
                    .noSummon()
                    .clientTrackingRange(10)
                    .build("seat_rider"));
}
