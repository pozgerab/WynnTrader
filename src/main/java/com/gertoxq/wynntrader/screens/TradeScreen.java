package com.gertoxq.wynntrader.screens;

import com.gertoxq.wynntrader.util.GuiPos;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static com.gertoxq.wynntrader.util.Utils.getLore;
import static com.gertoxq.wynntrader.util.Utils.removeFormat;

public class TradeScreen extends BaseScreen {

    final List<Integer> unlockedSlots;

    public TradeScreen(GenericContainerScreen screen) {
        super(screen);
        if (isThirdRowUnlocked()) {
            unlockedSlots = new ArrayList<>(GuiPos.Trades.marketSlots);
        } else if (isSecondRowUnlocked()) {
            unlockedSlots = GuiPos.Trades.marketSlots.subList(0, 10);
        } else unlockedSlots = GuiPos.Trades.alwaysUnlockedSlots;
    }

    public boolean isRowUnlocked(int row) {
        return !getStack(GuiPos.Trades.marketSlots.get(row * 5)).getName().getString().contains(GuiPos.Trades.UNAVAILABLE_SLOT);
    }

    public boolean isSecondRowUnlocked() {
        return isRowUnlocked(1);
    }

    public boolean isThirdRowUnlocked() {
        return isRowUnlocked(2);
    }

    public boolean isSlotAvailable(int index) {
        if (index > unlockedSlots.getLast()) return false;
        return getStack(unlockedSlots.get(index)).getName().getString().contains(GuiPos.Trades.AVAILABLE_SLOT);
    }

    public boolean isSlotFulfilled(int index) {
        if (index > unlockedSlots.getLast()) return false;
        List<Text> lore = getLore(getStack(unlockedSlots.get(index)));
        if (lore == null) return false;
        return removeFormat(lore.getFirst().getString()).startsWith("Fulfilled");
    }

    public void navigateToSlot(int index) {
        if (index > unlockedSlots.getLast()) return;
        getClicker().leftClick(unlockedSlots.get(index));
    }

    public void navigateBack() {
        getClicker().leftClick(GuiPos.Trades.BACK.getIndex());
    }

}
