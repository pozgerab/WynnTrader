package com.gertoxq.wynntrader.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;

import static com.gertoxq.wynntrader.client.WynntraderClient.*;

public class ScreenClicker {
    private final HandledScreen<?> screen;

    public ScreenClicker(HandledScreen<?> screen) {
        this.screen = screen;
    }

    public void leftClick(int slot) {
        try {
            assert client.player != null;
            client.player.networkHandler.sendPacket(
                    new ClickSlotC2SPacket(this.screen.getScreenHandler().syncId, this.screen.getScreenHandler().getRevision(), slot, 0, SlotActionType.PICKUP, new ItemStack(Items.AIR), new Int2ObjectArrayMap<>()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rightClick(int slot) {
        try {
            assert client.player != null;
            client.player.networkHandler.sendPacket(
                    new ClickSlotC2SPacket(this.screen.getScreenHandler().syncId, this.screen.getScreenHandler().getRevision(), slot, 1, SlotActionType.PICKUP, new ItemStack(Items.AIR), new Int2ObjectArrayMap<>()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
