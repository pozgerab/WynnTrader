package com.gertoxq.wynntrader.ui;

import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget.PressAction;
import net.minecraft.server.function.Procedure;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

public record Clickable(BooleanSupplier condition) {
    public static Clickable DEF = new Clickable(() -> true);

    public void addTo(Screen screen, AXISPOS xAxis, AXISPOS yAxis, int width, int height, int xOffset, int yOffset, Text message, PressAction action) {
        if (condition.getAsBoolean()) {
            Screens.getButtons(screen).add(new Button(
                    (xAxis == AXISPOS.START ? 0 : xAxis == AXISPOS.CENTER ? screen.width / 2 - width / 2 : screen.width - width) + xOffset,
                    (yAxis == AXISPOS.START ? 0 : yAxis == AXISPOS.CENTER ? screen.height / 2 - height / 2 : screen.height - height) + yOffset,
                    width, height, message, action
            ));
        }
    }

    public void addTo(Screen screen, AXISPOS xAxis, AXISPOS yAxis, int width, int height, Text message, PressAction action) {
        addTo(screen, xAxis, yAxis, width, height, 0, 0, message, action);
    }

    public enum AXISPOS {
        START, CENTER, END
    }

}
