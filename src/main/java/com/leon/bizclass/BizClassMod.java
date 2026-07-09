package com.leon.bizclass;

import com.leon.bizclass.registry.ModBlockEntities;
import com.leon.bizclass.registry.ModBlocks;
import com.leon.bizclass.registry.ModCreativeTabs;
import com.leon.bizclass.registry.ModEntities;
import com.leon.bizclass.registry.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod(BizClassMod.MOD_ID)
public class BizClassMod {

    public static final String MOD_ID = "bizclass";
    public static final Logger LOGGER = LogUtils.getLogger();

    public BizClassMod(IEventBus modEventBus) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);

        LOGGER.info("Business Class Seats mod loaded — 1-2-1 alternating suite row ready.");
    }
}
