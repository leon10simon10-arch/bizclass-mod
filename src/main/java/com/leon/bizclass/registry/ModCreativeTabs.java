package com.leon.bizclass.registry;

import com.leon.bizclass.BizClassMod;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BizClassMod.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BUSINESS_CLASS_TAB =
            TABS.register("business_class", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.bizclass.business_class"))
                    .icon(() -> new ItemStack(ModItems.BUSINESS_SEAT_ITEM.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.BUSINESS_SEAT_ITEM.get());
                        output.accept(ModItems.IFE_SCREEN_ITEM.get());
                        output.accept(ModItems.ROW_PLACER.get());
                        output.accept(ModItems.ECONOMY_SEAT_ARMREST_IFE_ITEM.get());
                        output.accept(ModItems.ECONOMY_SEAT_BACK_ONLY_ITEM.get());
                    })
                    .build());
}
