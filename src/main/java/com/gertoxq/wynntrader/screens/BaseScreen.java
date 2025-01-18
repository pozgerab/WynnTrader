package com.gertoxq.wynntrader.screens;

import com.gertoxq.wynntrader.util.ScreenClicker;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class BaseScreen {

    protected final GenericContainerScreen screen;
    protected final ScreenHandler handler;

    public BaseScreen(GenericContainerScreen screen) {
        this.screen = screen;
        this.handler = screen.getScreenHandler();
    }

    public ItemStack getStack(int index) {
        return handler.slots.get(index).getStack();
    }

    public boolean isEmptySlot(int index) {
        return !handler.slots.get(index).hasStack();
    }

    public static void renderItem(DrawContext context, ItemStack item, int x, int y, int targetSize, float xOffset, float yOffset) {

        int size = 16;
        float scale = (float) targetSize / 16;

        float centerX = x + (float) size / 2 + xOffset;
        float centerY = y + (float) size / 2 + yOffset;

        context.getMatrices().push();

        context.getMatrices().translate(centerX, centerY, 0);

        context.getMatrices().scale(scale, scale, 1.0f);

        context.getMatrices().translate(-centerX, -centerY, 0);

        context.drawItem(item, (int) (x + xOffset), (int) (y + yOffset));
        context.getMatrices().pop();
    }

    public Screen getScreen() {
        return screen;
    }

    public ScreenHandler getHandler() {
        return handler;
    }

    public ScreenClicker getClicker() {
        return new ScreenClicker(screen);
    }
}
