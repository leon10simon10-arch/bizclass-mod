package com.leon.bizclass.client;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/** Kept in a client-only class so the dedicated server never loads client rendering classes. */
@OnlyIn(Dist.CLIENT)
public class IfeScreenOpener {
    public static void open() {
        Minecraft.getInstance().setScreen(new IfeScreen());
    }
}
