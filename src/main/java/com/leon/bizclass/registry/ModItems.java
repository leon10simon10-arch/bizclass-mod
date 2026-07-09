package com.leon.bizclass.registry;

import com.leon.bizclass.BizClassMod;
import com.leon.bizclass.item.RowPlacerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BizClassMod.MOD_ID);

    public static final DeferredItem<BlockItem> BUSINESS_SEAT_ITEM = ITEMS.register("business_seat",
            () -> new BlockItem(ModBlocks.BUSINESS_SEAT.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> IFE_SCREEN_ITEM = ITEMS.register("ife_screen",
            () -> new BlockItem(ModBlocks.IFE_SCREEN.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> ECONOMY_SEAT_ARMREST_IFE_ITEM = ITEMS.register("economy_seat_armrest_ife",
            () -> new BlockItem(ModBlocks.ECONOMY_SEAT_ARMREST_IFE.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> ECONOMY_SEAT_BACK_ONLY_ITEM = ITEMS.register("economy_seat_back_only",
            () -> new BlockItem(ModBlocks.ECONOMY_SEAT_BACK_ONLY.get(), new Item.Properties()));

    // Places the whole locked-in 1-2-1 row (2 alternating left singles, the shared pod,
    // 2 alternating right singles) in one go, five seats + pod wide, all correctly oriented.
    public static final DeferredItem<RowPlacerItem> ROW_PLACER = ITEMS.register("business_row_placer",
            () -> new RowPlacerItem(new Item.Properties().stacksTo(1)));
}
