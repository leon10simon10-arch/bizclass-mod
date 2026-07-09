package com.leon.bizclass.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IfeScreen extends Screen {

    private static final String[] MENU_ITEMS = {
            "Flight Map", "Movies", "Music", "Games", "Cabin Lighting", "Call Crew"
    };

    protected IfeScreen() {
        super(Component.literal("In-Flight Entertainment"));
    }

    @Override
    protected void init() {
        int startY = this.height / 2 - 70;
        int buttonWidth = 160;
        int x = this.width / 2 - buttonWidth / 2;

        for (int i = 0; i < MENU_ITEMS.length; i++) {
            String label = MENU_ITEMS[i];
            this.addRenderableWidget(Button.builder(Component.literal(label), b -> {
                        if (this.minecraft != null && this.minecraft.player != null) {
                            this.minecraft.player.displayClientMessage(
                                    Component.literal("IFE: now playing \"" + label + "\""), true);
                        }
                    })
                    .bounds(x, startY + i * 24, buttonWidth, 20)
                    .build());
        }

        this.addRenderableWidget(Button.builder(Component.literal("Close"), b -> this.onClose())
                .bounds(x, startY + MENU_ITEMS.length * 24 + 8, buttonWidth, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 90, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
