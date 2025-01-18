package com.gertoxq.wynntrader.screens.sell;

import com.gertoxq.wynntrader.screens.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import java.util.regex.Pattern;

public class SellScreen extends ContainerScreen<SellScreenHandler> {

    public static final Pattern TITLE_PATTERN = Pattern.compile("\udaff\udfe8\ue014.+");

    public SellScreen(SellScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }
}
