package com.gertoxq.wynntrader.screens.trades;

import com.gertoxq.wynntrader.screens.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import java.util.regex.Pattern;

public class TradesScreen extends ContainerScreen<TradesScreenHandler> {

    public final static Pattern TITLE_PATTERN = Pattern.compile("\udaff\udfe8\ue013");

    public TradesScreen(TradesScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }
}
