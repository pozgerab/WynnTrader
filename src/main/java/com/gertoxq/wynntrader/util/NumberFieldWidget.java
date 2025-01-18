package com.gertoxq.wynntrader.util;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class NumberFieldWidget extends TextFieldWidget {

    protected boolean isSigned = true;
    protected boolean isDouble = false;

    public NumberFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        super(textRenderer, x, y, width, height, text);
    }

    public void setSigned(boolean signed) {
        isSigned = signed;
    }

    public boolean isSigned() {
        return isSigned;
    }

    public boolean isDouble() {
        return isDouble;
    }

    public void setDouble(boolean aDouble) {
        isDouble = aDouble;
    }

    public NumberFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, boolean signed, Text text) {
        super(textRenderer, x, y, width, height, text);
        this.isSigned = signed;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (isSigned && chr == '-' && getText().trim().isEmpty()) return super.charTyped(chr, modifiers);
        if (isDouble && chr == '.' && !getText().contains(".")) return super.charTyped(chr, modifiers);
        if (Character.isDigit(chr)) return super.charTyped(chr, modifiers);
        return false;
    }
}
