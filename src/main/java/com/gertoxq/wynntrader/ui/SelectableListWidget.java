package com.gertoxq.wynntrader.ui;

import com.gertoxq.wynntrader.client.WynntraderClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;

public abstract class SelectableListWidget<T> extends AlwaysSelectedEntryListWidget<SelectableListWidget<T>.Entry> {

    private final int right;

    public SelectableListWidget(int width, int height, int x, int y, int itemHeight, List<T> items) {
        super(WynntraderClient.client, width, height, y, itemHeight);
        this.setX(x);
        this.right = x + width;
        items.forEach(t -> addEntry(new Entry(t)));
    }

    @Override
    public boolean removeEntryWithoutScrolling(Entry entry) {
        return super.removeEntryWithoutScrolling(entry);
    }

    public Entry addEntryToTop(T entry) {
        var newEntry = new Entry(entry);
        super.addEntryToTop(newEntry);
        return newEntry;
    }

    public void addEntry(T entry) {
        super.addEntry(new Entry(entry));
    }

    @Override
    public int getRowWidth() {
        return width;
    }

    public void dispose() {

    }

    @Override
    protected int getDefaultScrollbarX() {
        return getX() + width;
    }

    @Override
    public void clearEntries() {
        super.clearEntries();
    }

    @Override
    public void replaceEntries(Collection<Entry> newEntries) {
        super.replaceEntries(newEntries);
    }

    public Entry create(T value) {
        return new Entry(value);
    }

    @Override
    protected void renderEntry(DrawContext context, int mouseX, int mouseY, float delta, int index, int x, int y, int entryWidth, int entryHeight) {
        super.renderEntry(context, mouseX, mouseY, delta, index, x, y, entryWidth, entryHeight);
    }

    @Override
    public Entry getEntry(int index) {
        return super.getEntry(index);
    }

    @Override
    public int getEntryCount() {
        return super.getEntryCount();
    }

    @Override
    public boolean isSelectedEntry(int index) {
        return super.isSelectedEntry(index);
    }

    public abstract void renderChild(Entry entry, DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta);

    public class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> {

        private final T value;

        public Entry(T entry) {
            this.value = entry;
        }

        public T getValue() {
            return value;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            this.onPressed();
            return true;
        }

        void onPressed() {
            SelectableListWidget.this.setSelected(this);
            setFocused(true);
        }

        @Override
        public Text getNarration() {
            return Text.empty();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            SelectableListWidget.this.renderChild(this, context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
        }
    }
}
