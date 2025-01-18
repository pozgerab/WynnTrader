package com.gertoxq.wynntrader.client;

import com.gertoxq.wynntrader.config.Manager;
import com.gertoxq.wynntrader.custom.AllIDs;
import com.gertoxq.wynntrader.screens.BaseScreen;
import com.gertoxq.wynntrader.screens.market.MarketScreen;
import com.gertoxq.wynntrader.screens.market.MarketScreenHandler;
import com.gertoxq.wynntrader.screens.sell.SellScreen;
import com.gertoxq.wynntrader.screens.sell.SellScreenHandler;
import com.gertoxq.wynntrader.screens.trades.TradesScreen;
import com.gertoxq.wynntrader.screens.trades.TradesScreenHandler;
import com.gertoxq.wynntrader.ui.Clickable;
import com.gertoxq.wynntrader.util.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class WynntraderClient implements ClientModInitializer {

    public static MinecraftClient client;
    public static Manager configManager;
    public static boolean wynnTilsScrollEnabled = false;

    public static Manager getConfigManager() {
        return configManager;
    }

    @Override
    public void onInitializeClient() {
        client = MinecraftClient.getInstance();
        configManager = new Manager();
        AllIDs.load();
        WynnData.load();
        Task.init();
        try {
            HandledScreens.register(ScreenHandlerType.GENERIC_9X6, (GenericContainerScreenHandler handler, PlayerInventory playerInventory, Text title) -> {
                if (MarketScreen.TITLE_PATTERN.matcher(title.getString()).matches()) {
                    return new MarketScreen(new MarketScreenHandler(handler.syncId, playerInventory, handler.getInventory()), playerInventory, title);
                } else if (SellScreen.TITLE_PATTERN.matcher(title.getString()).matches()) {
                    return new SellScreen(new SellScreenHandler(handler.syncId, playerInventory, handler.getInventory()), playerInventory, title);
                } else if (TradesScreen.TITLE_PATTERN.matcher(title.getString()).matches()) {
                    return new TradesScreen(new TradesScreenHandler(handler.syncId, playerInventory, handler.getInventory()), playerInventory, title);
                }
                return new GenericContainerScreen(handler, playerInventory, title);
            });
        } catch (Exception e) {
            System.out.println("Duplicate registration but ignore bc it replaces the registry so it works: "+ e.getMessage());
        }
    }
}
