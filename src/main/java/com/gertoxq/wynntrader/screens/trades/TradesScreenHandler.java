package com.gertoxq.wynntrader.screens.trades;

import com.gertoxq.wynntrader.screens.ContainerScreenHandler;
import com.gertoxq.wynntrader.screens.market.MarketScreenHandler;
import com.gertoxq.wynntrader.util.GuiPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.SlotActionType;

public class TradesScreenHandler extends ContainerScreenHandler {
    public TradesScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(syncId, playerInventory, inventory);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex == GuiPos.Trades.BACK.getIndex()) {
            MarketScreenHandler.redirectedToSell = true;
            MarketScreenHandler.isFindingRealItem = false;
        }
        super.onSlotClick(slotIndex, button, actionType, player);
    }
}
