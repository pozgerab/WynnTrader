package com.gertoxq.wynntrader.screens.market;

import com.gertoxq.wynntrader.client.WynntraderClient;
import com.gertoxq.wynntrader.custom.CustomItem;
import com.gertoxq.wynntrader.custom.DoubleID;
import com.gertoxq.wynntrader.custom.ID;
import com.gertoxq.wynntrader.custom.RolledID;
import com.gertoxq.wynntrader.screens.ContainerScreen;
import com.gertoxq.wynntrader.ui.Button;
import com.gertoxq.wynntrader.ui.Clickable;
import com.gertoxq.wynntrader.ui.SelectableListWidget;
import com.gertoxq.wynntrader.util.*;
import com.wynntils.core.components.Managers;
import com.wynntils.features.trademarket.TradeMarketQuickSearchFeature;
import com.wynntils.features.ui.ContainerScrollFeature;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public class MarketScreen extends ContainerScreen<MarketScreenHandler> {

    public final static Pattern TITLE_PATTERN = Pattern.compile("\udaff\udfe8\ue011");
    public ItemSelector SEARCH_FIELD = null;
    public String query = "";
    public static FilterListWidget filterList;

    public MarketScreen(MarketScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        client.player.sendMessage(Text.literal("START INIT"));
        WynntraderClient.wynnTilsScrollEnabled = Managers.Feature.getFeatureInstance(ContainerScrollFeature.class).isEnabled();
        Managers.Feature.getFeatureInstance(ContainerScrollFeature.class).setUserEnabled(false);
        Managers.Feature.getFeatureInstance(TradeMarketQuickSearchFeature.class).setUserEnabled(true);
        SEARCH_FIELD = addDrawableChild(new ItemSelector(0,0, 100, 20,
                getScreenHandler().refreshQueriedItem().map(CustomItem::getNameText).orElse(Text.empty())));

        if (!MarketScreenHandler.redirectedToSell) {
            MarketScreenHandler.reinit();

            getScreenHandler().readAllPages();
        } else {
            MarketScreenHandler.redirectedToSell = false;
            getScreenHandler().createTracker(true).setOnComplete(() -> getScreenHandler()
                    .navigatePages(MarketScreenHandler.redirectStartPage - getScreenHandler().getCurrentPage(), () -> {
                    }));
        }
        getScreenHandler().createTracker(true).setOnComplete(() -> {
            if (getScreenHandler().refreshQueriedItem().isPresent()) {
                SEARCH_FIELD.setText(query);
                filterList = addDrawableChild(new FilterListWidget(client, 200, 200, width / 3 * 2, 40));
                var applyBtn = addDrawableChild(new Button(width / 3 * 2, 245, 200, 20, Text.literal("APPLY").styled(style -> style.withColor(Formatting.GREEN).withBold(true)), button -> {
                    MarketScreenHandler.QueryBuilder queryBuilder = getScreenHandler().createBuilder();
                    filterList.children().stream().filter(filterEntry -> !filterEntry.isSort).forEach(filterEntry -> {
                        MarketScreenHandler.FilterInstance filterInstance = filterEntry.getFilterInstance();
                        if (filterInstance.value() != Integer.MIN_VALUE) {
                            queryBuilder.filterInstances.add(filterInstance);
                        }
                    });
                    filterList.children().stream().filter(filterEntry -> filterEntry.isSort).forEach(filterEntry -> {
                        MarketScreenHandler.SortInstance sortInstance = filterEntry.getSortInstance();
                        queryBuilder.sortInstances.add(sortInstance);
                    });
                    queryBuilder.build().apply();
                }));
                var idAdder = addDrawableChild(new IDSelector(width / 3 * 2, 270, 140, 20, Text.empty()));
                var addFilter = addDrawableChild(new Button(width / 3 * 2 + 145, 270, 20, 20, Text.literal("F"), button -> {
                    if (idAdder.getSelectedOrNull() != null) {
                        RolledID id = idAdder.getSelectedOrNull().getValue();
                        DoubleID.Range rollRange = getScreenHandler().getQueriedItem().get().getRollRange(id);
                        filterList.addFilter(id, rollRange);
                    }
                }));
                var addSort = addDrawableChild(new Button(width / 3 * 2 + 170, 270, 20, 20, Text.literal("S"), button -> {
                    if (idAdder.getSelectedOrNull() != null) filterList.addSort(idAdder.getSelectedOrNull().getValue(), false);
                }));
            }
        });

        addDrawableChild(this.createButton(Clickable.AXISPOS.START, Clickable.AXISPOS.END, 100, 20, 0, 0, Text.literal("PAGE"),
                button -> {
                    client.player.sendMessage(Text.literal(getScreenHandler().getCurrentPage().toString()));
                }));
        addDrawableChild(this.createButton(Clickable.AXISPOS.START, Clickable.AXISPOS.END, 100, 20, 0, -20, Text.literal("REMAP"),
                button -> {
                    getScreenHandler().remapSlots();
                }));
        addDrawableChild(this.createButton(Clickable.AXISPOS.START, Clickable.AXISPOS.END, 60, 20, 0, -40, Text.literal("APPLY FILTER"), button -> {

                }));
        addDrawableChild(this.createButton(Clickable.AXISPOS.START, Clickable.AXISPOS.START, 60, 20, 0, 40, Text.literal("I & REmapped"), button -> {

            System.out.println(getScreenHandler().getRemappedItems().stream().map(marketItem -> {
                return List.of(marketItem.item(), marketItem.page(), marketItem.slot());
            }).toList());
            System.out.println(MarketScreenHandler.getOriginalItems().stream().map(marketItem -> {
                return List.of(marketItem.item(), marketItem.page(), marketItem.slot());
            }).toList());
        }));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        try {
            getScreenHandler().remapSlots();
            super.render(context, mouseX, mouseY, delta);
        } catch (Exception e) {
            System.out.println("Try block just in case, heres the error anyway, error in wynntrader.MarketScreen#render: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (getFocused() instanceof TextFieldWidget widget) return widget.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public MarketScreenHandler getScreenHandler() {
        return super.getScreenHandler();
    }

    public class FilterListWidget extends ElementListWidget<FilterListWidget.FilterEntry> {
        public FilterListWidget(MinecraftClient minecraftClient, int width, int height, int x, int y) {
            super(minecraftClient, width, height, y, 45);
            setX(x);
            getScreenHandler().getQueryInstance().filterInstances.forEach(filterInstance -> addFilter(filterInstance.id(), getScreenHandler().getQueriedItem().get().getRollRange(filterInstance.id()), filterInstance.relation(), (int) filterInstance.value(), filterInstance.relative()));
            getScreenHandler().getQueryInstance().sortInstances.forEach(sortInstance -> addSort(sortInstance.id(), sortInstance.increasing()));
        }

        @Override
        protected void drawSelectionHighlight(DrawContext context, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {
            var x1 = getX() + (width - entryWidth) / 2;
            var x2 = getX() + (width + entryWidth) / 2;
            context.fill(x1, y - 2, x2, y + entryHeight + 2, Formatting.DARK_GRAY.getColorValue());
        }

        public void addFilter(RolledID id, DoubleID.Range range) {
            super.addEntry(new FilterEntry(id, range));
        }

        public void addFilter(RolledID id, DoubleID.Range range, Relation relation, Integer value, Boolean roll) {
            super.addEntry(new FilterEntry(id, range, relation, value, roll));
        }

        public void addSort(RolledID id, boolean increasing) {
            super.addEntry(new FilterEntry(id, increasing));
        }

        @Override
        public int getRowWidth() {
            return width;
        }

        @Override
        protected boolean isSelectedEntry(int index) {
            var selected = getSelectedOrNull();
            return selected != null && selected == children().get(index);
        }

        public class FilterEntry extends ElementListWidget.Entry<FilterEntry> {
            private final DirectionalLayoutWidget layout = DirectionalLayoutWidget.horizontal().spacing(5);
            private final DirectionalLayoutWidget titleLayout = DirectionalLayoutWidget.horizontal().spacing(5);
            private final List<ClickableWidget> children = new ArrayList<>();
            private final RolledID identification;
            private CyclingButtonWidget<Integer> relationButton;
            private CyclingButtonWidget<Boolean> rollOrRawButton;
            private NumberFieldWidget valueInput;
            private final TextWidget idText;
            private CyclingButtonWidget<Boolean> increasingButton;
            private CyclingButtonWidget<Integer> unitCycleBtn;
            private final boolean isSort;

            private CyclingButtonWidget<Integer> priorityButton;

            public FilterEntry(RolledID identification, DoubleID.Range rollRange) {
                this(identification, rollRange, null, null, null);
            }

            public FilterEntry(RolledID identification, DoubleID.Range rollRange, @Nullable Relation relation, @Nullable Integer value, @Nullable Boolean roll) {
                isSort = false;
                boolean isPriceType = identification.getMetric() == ID.Metric.PRICE;
                this.identification = identification;
                this.relationButton = CyclingButtonWidget.<Integer>builder(index ->
                        Text.literal(Relation.values()[index % Relation.values().length].sign)).values(0, 1, 2, 3, 4).omitKeyText().initially( relation == null ? 0 : relation.ordinal())
                    .build(0, 0, 20, 20, Text.empty());
                this.valueInput = new NumberFieldWidget(textRenderer, 0, 0,85, 20, Text.empty());
                if (isPriceType) {
                    valueInput.setSigned(false);
                    valueInput.setDouble(true);
                }
                if (value != null) valueInput.setText(value.toString());
                var rangeText = Text.literal(rollRange.min().toString()).styled(style -> style.withColor(Formatting.RED))
                        .append(Text.literal(" - ").styled(style -> style.withColor(Formatting.DARK_GRAY))).append(Text.literal(rollRange.max().toString()).styled(style -> style.withColor(Formatting.GREEN)));
                var percentRangeText = Text.literal("0").styled(style -> style.withColor(Formatting.RED))
                        .append(Text.literal(" - ").styled(style -> style.withColor(Formatting.DARK_GRAY))).append(Text.literal("100").styled(style -> style.withColor(Formatting.GREEN))).append(Text.literal(" %").styled(style -> style.withColor(Formatting.DARK_GRAY)));

                this.rollOrRawButton = CyclingButtonWidget.onOffBuilder(Text.literal("ROLL%").styled(style -> style.withColor(Formatting.DARK_AQUA).withItalic(true)), Text.literal("RAW").styled(style -> style.withColor(Formatting.YELLOW))).omitKeyText().initially(roll == null || roll).build(0, 0, 40, 20, Text.empty(), (button, rollValue) -> {
                    valueInput.setPlaceholder(rollValue ? percentRangeText : rangeText);
                });
                valueInput.setPlaceholder(rollOrRawButton.getValue() ? percentRangeText : rangeText);
                var deleteButton = new Button(0, 0, 20, 20, Text.literal("X").styled(style -> style.withColor(Formatting.RED).withBold(true)), button -> {
                    FilterListWidget.this.removeEntry(this);
                });
                this.idText = new TextWidget(110, 20, Text.literal(identification.getAsString(getScreenHandler().getQueriedItem().get().getType().getCast())), textRenderer).alignLeft();
                TextWidget rangeTextWidget = new TextWidget(55, 20, rangeText, textRenderer).alignRight();

                layout.add(relationButton);
                layout.add(valueInput);
                if (!identification.isStatic()) {
                    layout.add(rollOrRawButton);
                } else {
                    rollOrRawButton.setValue(false);
                    valueInput.setPlaceholder(Text.literal("0 - ").styled(style -> style.withColor(Formatting.DARK_GRAY)));
                }
                if (isPriceType) {
                    List<String> units = List.of("²", "²½", "¼²", "stx");
                    unitCycleBtn = CyclingButtonWidget.<Integer>builder(val -> Text.literal(units.get(val)).styled(style -> style.withColor(Formatting.GREEN))).omitKeyText().values(0, 1, 2, 3).initially(0).build(0,0, 40, 20, Text.empty());
                    layout.add(unitCycleBtn);
                }
                layout.add(deleteButton);
                layout.refreshPositions();
                layout.forEachChild(children::add);
                titleLayout.add(idText);
                if (!identification.isStatic()) {
                    titleLayout.add(rangeTextWidget);
                }
                titleLayout.refreshPositions();
                titleLayout.forEachChild(children::add);
            }

            public MarketScreenHandler.FilterInstance getFilterInstance() {
                int value;
                if (identification.getMetric() == ID.Metric.PRICE) {
                    value = valueInput.getText().isEmpty() ? 0 : (int) (Double.parseDouble(valueInput.getText()) * Math.pow(64, unitCycleBtn.getValue()));
                } else {
                    value = valueInput.getText().isEmpty() ? Integer.MIN_VALUE : Integer.parseInt(valueInput.getText());
                }
                return new MarketScreenHandler.FilterInstance(identification, Relation.values()[relationButton.getValue()], value, rollOrRawButton.getValue());
            }

            public FilterEntry(RolledID identification, boolean increasing) {
                isSort = true;
                this.identification = identification;
                this.idText = new TextWidget(130, 20, Text.literal(identification.getAsString(getScreenHandler().getQueriedItem().get().getType().getCast())), textRenderer).alignLeft();
                var titleText = new TextWidget(50, 20, Text.literal("Sort BY"), textRenderer).alignLeft();
                this.increasingButton = CyclingButtonWidget.onOffBuilder(Text.literal("Increasing ▲"), Text.literal("Decreasing ▼")).omitKeyText().initially(increasing).build(0, 0, 100, 20, Text.empty());
                int sortInstances = (int) FilterListWidget.this.children().stream().map(filterEntry -> filterEntry.priorityButton).filter(Objects::nonNull).count();
                this.priorityButton = CyclingButtonWidget.<Integer>builder(integer -> Text.literal("No. " + integer)).omitKeyText().values(sortInstances + 1).tooltip(value -> Tooltip.of(Text.literal("When ordering, this is going to be the sorter's " + value + ". priority"))).build(0, 0, 50, 20, Text.empty());
                var deleteButton = new Button(0, 0, 20, 20, Text.literal("X").styled(style -> style.withColor(Formatting.RED).withBold(true)), button -> {
                    FilterListWidget.this.removeEntry(this);
                });
                layout.add(increasingButton);
                layout.add(priorityButton);
                layout.add(deleteButton);
                layout.refreshPositions();
                layout.forEachChild(children::add);
                titleLayout.add(titleText);
                titleLayout.add(idText);
                titleLayout.refreshPositions();
                titleLayout.forEachChild(children::add);
            }

            public MarketScreenHandler.SortInstance getSortInstance() {
                return new MarketScreenHandler.SortInstance(identification, increasingButton.getValue());
            }

            public RolledID getIdentification() {
                return identification;
            }

            @Override
            public List<? extends Selectable> selectableChildren() {
                return children;
            }

            @Override
            public List<? extends Element> children() {
                return children;
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                titleLayout.setPosition(x + 5, y - 1);
                titleLayout.forEachChild(widget -> widget.render(context, mouseX, mouseY, tickDelta));
                layout.setPosition(x + 5, y + 20);
                layout.forEachChild(widget -> widget.render(context, mouseX, mouseY, tickDelta));
            }
        }
    }

    public class IDSelector extends AutoCompleteField<RolledID> {

        public IDSelector(int x, int y, int width, int height, Text text) {
            super(x, y, width, height, 15, text, 60);
            setPlaceholder(Text.literal("Select Id"));
        }

        @Override
        public List<RolledID> getOptions(String input) {
            if (getScreenHandler().refreshQueriedItem().isEmpty()) return List.of();
            return getScreenHandler().refreshQueriedItem().get().presentIDs.stream()
                    .filter(rolledID -> textGetter(rolledID).toLowerCase().contains(input.toLowerCase())).toList();
        }

        @Override
        public String textGetter(RolledID option) {
            return option.getAsString(getScreenHandler().getQueriedItem().get().getType().getCast());
        }

        @Override
        public void renderChild(SelectableListWidget<RolledID>.Entry entry, DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.drawTextWithShadow(textRenderer, textGetter( entry.getValue() ).trim(), x + 2, y + 2, Formatting.WHITE.getColorValue());
        }
    }

    public class ItemSelector extends AutoCompleteField<MutableText> {

        public ItemSelector(int x, int y, int width, int height, Text text) {
            super(x, y, width, height, 15, text, 100);
        }

        @Override
        public List<MutableText> getOptions(String input) {
            return WynnData.getIdMap().keySet().stream()
                    .filter(string -> string.toLowerCase().contains(input.strip().toLowerCase()))
                    .map(string -> Text.literal(string).styled(style -> style.withColor(WynnData.getTierMap().get(string).format)))
                    .toList();
        }

        @Override
        public void changedListener(String input) {
            query = input;
            super.changedListener(input);
        }

        @Override
        public void setSelected(SelectableListWidget<MutableText>.@Nullable Entry entry) {
            super.setSelected(entry);
            if (entry == null) return;
            getScreenHandler().search();
            Task.runAfter(() -> ChatManager.sendMessage(textGetter(entry.getValue())), 20);
        }

        @Override
        public String textGetter(MutableText option) {
            return option.getString();
        }

        @Override
        public void renderChild(SelectableListWidget<MutableText>.Entry entry, DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.drawTextWithShadow(textRenderer, entry.getValue(), x + 1, y + entryHeight / 2 - 4, Formatting.WHITE.getColorIndex());
            context.drawBorder(x, y, 100, entryHeight, Formatting.BLACK.getColorIndex());
        }
    }
}
