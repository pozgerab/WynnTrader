package com.gertoxq.wynntrader.screens.sell;

import com.gertoxq.wynntrader.custom.CustomItem;
import com.gertoxq.wynntrader.screens.ContainerScreenHandler;
import com.gertoxq.wynntrader.screens.market.MarketScreenHandler;
import com.gertoxq.wynntrader.util.GuiPos;
import com.wynntils.core.persisted.upfixers.config.TradeMarketAutoOpenChatToTradeMarketQuickSearchUpfixer;
import com.wynntils.features.trademarket.TradeMarketQuickSearchFeature;
import com.wynntils.models.containers.containers.TradeMarketSellContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

public class SellScreenHandler extends ContainerScreenHandler {

    public SellScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(syncId, playerInventory, inventory);
    }

    public boolean isEmpty() {
        return isEmptySlot(GuiPos.Sell.SET_PRICE.getIndex());
    }

    public CustomItem getItem() {
        return CustomItem.getItem(getStack(GuiPos.Sell.ITEM.getIndex()));
    }

    public ItemStack getItemStack() {
        return getStack(GuiPos.Sell.ITEM.getIndex());
    }

    public void navigateBack() {
        this.leftClickSlot(GuiPos.Sell.BACK.getIndex());
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex == GuiPos.Sell.BACK.getIndex()) {
            MarketScreenHandler.redirectedToSell = true;
            MarketScreenHandler.isFindingRealItem = false;

        }
        super.onSlotClick(slotIndex, button, actionType, player);
    }
}
