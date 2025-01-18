package com.gertoxq.wynntrader.screens.market;

import com.gertoxq.wynntrader.custom.AllIDs;
import com.gertoxq.wynntrader.custom.CustomItem;
import com.gertoxq.wynntrader.custom.RolledID;
import com.gertoxq.wynntrader.screens.ContainerScreenHandler;
import com.gertoxq.wynntrader.screens.PageTracker;
import com.gertoxq.wynntrader.util.*;
import com.wynntils.core.components.Managers;
import com.wynntils.features.ui.ContainerScrollFeature;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static com.gertoxq.wynntrader.client.WynntraderClient.*;

public class MarketScreenHandler extends ContainerScreenHandler {

    public static List<MarketItem> remappedItems = new ArrayList<>();
    private static final AtomicInteger currentPage = new AtomicInteger(0);
    private static final Map<Integer, Map<Integer, MarketItem>> originalItems = new HashMap<>();
    public static boolean isFindingRealItem = false;
    private static int maxPageIndex = 99;
    private static CustomItem queriedItem = null;
    public static boolean finishedReading = false;
    public static boolean filterEnabled = false;
    public static boolean redirectedToSell = false;
    public static int CURR_SYNC_ID;
    public Set<PageTracker> trackers = new HashSet<>();
    public static boolean enabled = true;
    public static int redirectStartPage = 0;
    public static QueryBuilder queryInstance;
    public static boolean needsRemap = false;

    public MarketScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(syncId, playerInventory, inventory);
        CURR_SYNC_ID = syncId;
        isFindingRealItem = false;
    }

    public QueryBuilder getQueryInstance() {
        return queryInstance == null ? createBuilder() : queryInstance;
    }

    public static void reinit() {
        originalItems.clear();
        enabled = true;
        remappedItems = new ArrayList<>();
        currentPage.set(0);
        queryInstance = null;
        isFindingRealItem = false;
        maxPageIndex = 99;
        queriedItem = null;
        finishedReading = false;
        filterEnabled = false;
        redirectedToSell = false;
    }

    public static List<MarketItem> getOriginalItems() {
        return originalItems.values().stream().flatMap(integerMarketItemMap -> integerMarketItemMap.values().stream()).toList();
    }

    public List<MarketItem> getRemappedItems() {
        return remappedItems;
    }

    public Integer getCurrentPage() {
        return currentPage.get();
    }

    public static Integer getStaticCurrentPage() {
        return currentPage.get();
    }

    public void readAllPages() {
        var tracker = createTracker(true);
        tracker.setOnComplete(() -> {
            if (refreshQueriedItem().isEmpty()) {
                enabled = false;
                return;
            }
            traverseReadPages();
        });
        tracker.isLoadComplete();
    }

    @Override
    public void setStackInSlot(int slot, int revision, ItemStack stack) {
        if (slot == GuiPos.Market.SORT.getIndex()) return;
        super.setStackInSlot(slot, revision, stack);
        if (/*!finishedReading &&*/ Utils.isBetween(slot, 0, 53)) {
            if (Utils.isBetween(slot, 0, 44)) {
                MarketItem item = new MarketItem(getCustomStack(slot), getStack(slot), getCurrentPage(), slot);
                if (item.item == null) return;
                if (!originalItems.containsKey(getCurrentPage())) {
                    originalItems.put(getCurrentPage(), new HashMap<>());
                }
                originalItems.get(getCurrentPage()).put(slot, item);
            }
            try {
                trackers.forEach(tracker -> tracker.onSlotUpdate(slot));
            } catch (ConcurrentModificationException ignored) {}
        }
    }

    @Override
    public void onClosed(PlayerEntity player) {
        needsRemap = false;
        Managers.Feature.getFeatureInstance(ContainerScrollFeature.class).setUserEnabled(wynnTilsScrollEnabled);
        try {
            trackers.forEach(PageTracker::clean);
        } catch (ConcurrentModificationException ignored) {}
        super.onClosed(player);
    }

    public QueryBuilder createBuilder() {
        return new QueryBuilder();
    }

    public PageTracker createTracker(boolean disposable) {
        return new PageTracker(disposable) {
            @Override
            protected void init() {
                trackers.add(this);
            }

            @Override
            public void clean() {
                super.clean();
                trackers.remove(this);
            }
        };
    }

    private void traverseReadPages() {
        var tracker = createTracker(true);
        client.player.sendMessage(Text.literal("cp: " + getCurrentPage()));
        if (!isLastPage()) {
            client.player.sendMessage(Text.literal("notlast"));
            tracker.setOnComplete(this::traverseReadPages);
            navigateNextPage();
        } else {
            maxPageIndex = getCurrentPage();
            client.player.sendMessage(Text.literal("finished reading"));
            finishedReading = true;
            moveToFirstPage();
        }
    }

    private void moveToFirstPage() {
        if (!isFirstPage()) {
            createTracker(true).setOnComplete(this::moveToFirstPage);
            navigatePrevPage();
        }
    }

    public void remapSlots() {
        try {
            if (getQueriedItem().isEmpty() || !finishedReading || remappedItems.isEmpty()) return;
            System.out.println("remapping");
            List<MarketItem> itemList = remappedItems;
            slots.replaceAll(slot -> {
                if (slot.inventory != this.getInventory() || slot.getIndex() > 44) {
                    if (slot.inventory == this.getInventory() && slot.getIndex() == GuiPos.Market.SORT.getIndex()) {
                        slot.setStack(Items.AIR.getDefaultStack());
                    }
                    return slot;
                }

                if (itemList.size() > slot.getIndex() + getCurrentPage() * 44) {
                    slot.setStack(itemList.get(slot.getIndex() + getCurrentPage() * 44).itemStack());
                } else {
                    slot.setStack(Items.AIR.getDefaultStack());
                }
                return slot;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void navigatePages(int amount, Runnable after) {
        if (amount == 0) return;
        boolean forward = 0 < amount;
        int step = (forward ? -1 : 1);
        if (!Utils.isBetween(getCurrentPage() - step, 0, maxPageIndex)) {
            return;
        }
        createTracker(true).setOnComplete(() -> {
            navigatePages(amount + step, after);
            if (Math.abs(amount) == 1) {
                after.run();
            }
        });
        if (forward) {
            navigateNextPage();
        } else {
            navigatePrevPage();
        }
    }

    public @Nullable String getSearchedString() {
        ItemStack searchStack = getStack(GuiPos.Market.SEARCH.getIndex());
        var lore = Utils.getLore(searchStack);
        if (lore == null) return null;
        String str = lore.stream().filter(text -> text.getString().contains("Name Contains: ")).findFirst().orElse(Text.empty()).getString();
        if (str.isEmpty()) return null;
        return str.split("Name Contains: ")[1];
    }

    public Optional<CustomItem> getQueriedItem() {
        return Optional.ofNullable(queriedItem);
    }

    public Optional<CustomItem> refreshQueriedItem() {
        if (getSearchedString() == null) return Optional.empty();
        int id = WynnData.getIdMap().getOrDefault(Utils.capitalizeWords(getSearchedString()), -1);
        var item = WynnData.getData().get(id);
        queriedItem = item == null ? null : item.baseItem();
        return Optional.ofNullable(queriedItem);
    }


    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex == GuiPos.Market.PAGE_DOWN.getIndex() && !isEmptySlot(slotIndex)) {
            currentPage.incrementAndGet();
            currentPage.set(Math.min(currentPage.get(), maxPageIndex));
            needsRemap = true;
            createTracker(true).setOnComplete(() -> needsRemap = false);
        } else if (slotIndex == GuiPos.Market.PAGE_UP.getIndex() && !isEmptySlot(slotIndex)) {
            currentPage.decrementAndGet();
            currentPage.set(Math.max(currentPage.get(), 0));
            needsRemap = true;
            createTracker(true).setOnComplete(() -> needsRemap = false);
        } else if (Utils.isBetween(slotIndex, 0, 44)) {
            if (!isFindingRealItem) {
                int remappedIndex = slotIndex + getCurrentPage() * 44;
                if (remappedItems.size() > remappedIndex) {
                    isFindingRealItem = true;
                    MarketItem clickedItem = remappedItems.get(remappedIndex);
                    int pageDiff = clickedItem.page - getCurrentPage();
                    int slot = clickedItem.slot;
                    if (pageDiff != 0) {
                        MarketScreenHandler.redirectStartPage = getCurrentPage();
                        navigatePages(pageDiff, () -> createTracker(true).setOnComplete(() -> navigateItem(slot)));
                    } else {
                        navigateItem(slot);
                    }
                }
            } else {
                redirectedToSell = true;
            }
            return;
        }

        super.onSlotClick(slotIndex, button, actionType, player);
    }

    public List<MarketItem> getCustomSellContents() {
        return GuiPos.Market.slots.stream().map(integer -> new MarketItem( getCustomStack(integer), getStack(integer), -1, integer))
                .filter(marketItem -> marketItem.item() != null).toList();
    }

    public CustomItem getCustomStack(int index) {
        return CustomItem.getItem(getStack(index));
    }

    public boolean isFirstPage() {
        return isEmptySlot(GuiPos.Market.PAGE_UP.getIndex());
    }

    public boolean isLastPage() {
        return isEmptySlot(GuiPos.Market.PAGE_DOWN.getIndex());
    }

    public boolean isOnlyPage() {
        return isFirstPage() && isLastPage();
    }

    public boolean isMiddlePage() {
        return !isFirstPage() && !isLastPage();
    }

    public void navigateItem(@Range(from = 0, to = 44) int index) {
        leftClickSlot(index);
    }

    public void navigateSell() {
        leftClickSlot(GuiPos.Market.SELL.getIndex());
    }

    public void navigateTrades() {
        leftClickSlot(GuiPos.Market.TRADES.getIndex());
    }

    public void navigateFilters() {
        rightClickSlot(GuiPos.Market.SEARCH.getIndex());
    }

    public void search() {
        leftClickSlot(GuiPos.Market.SEARCH.getIndex());
    }

    public void cycleSort() {
        leftClickSlot(GuiPos.Market.SORT.getIndex());
    }

    public void cycleSortBack() {
        rightClickSlot(GuiPos.Market.SORT.getIndex());
    }

    public void navigateNextPage() {
        leftClickSlot(GuiPos.Market.PAGE_DOWN.getIndex());
    }

    public void navigatePrevPage() {
        leftClickSlot(GuiPos.Market.PAGE_UP.getIndex());
    }

    public class Operator {
        private Stream<MarketItem> itemStream;
        Operator() {
            itemStream = getOriginalItems().stream(); // items.stream().filter(marketItem -> Objects.equals(marketItem.item.getBaseItemId(), getQueriedItem().get().getBaseItemId()));
        }

        public Stream<MarketItem> apply() {
            remappedItems = itemStream.toList();
            filterEnabled = true;
            return remappedItems.stream();
        }

        public Operator sortByPrice() {
            itemStream = itemStream.sorted(Comparator.comparingInt(value -> value.item.getPrice()));
            return this;
        }

        public Operator sortById(RolledID id, boolean increasing) {
            var list = itemStream.sorted(Comparator.comparingInt(value -> value.item.get(id))).toList();
            itemStream = increasing ? list.stream() : list.reversed().stream();
            return this;
        }

        public Operator filterUnidentified(boolean unidentified) {
            itemStream = itemStream.filter(marketItem ->
                    marketItem.item.isUnidentified() == unidentified);
            return this;
        }

        public Operator filterShiny(boolean shiny) {
            itemStream = itemStream.filter(marketItem ->
                    marketItem.item.isShiny() == shiny);
            return this;
        }

        public Operator filterByIdRaw(RolledID id, Relation relation, double value) {
            itemStream = itemStream.filter(marketItem -> {
                int rolledValue = marketItem.item.get(id);
                return relation.compare(rolledValue, value);
            });
            return this;
        }

        public Operator filterByIdRoll(RolledID id, Relation relation, @Range(from = 0, to = 100) double value, CustomItem basedOn) {
            itemStream = itemStream.filter(marketItem -> {
                int rolledValue = marketItem.item.get(id);
                double rollPercent = basedOn.getRollPercent(id, rolledValue);
                return relation.compare(rollPercent, value);
            });
            return this;
        }

    }

    public record MarketItem(CustomItem item, ItemStack itemStack, int page, @Range(from = 0, to = 44) int slot) implements Comparable<MarketItem> {
        @Override
        public int compareTo(@NotNull MarketScreenHandler.MarketItem o) {
            return 0;
        }
    }

    public class QueryBuilder {

        Operator operator;
        List<FilterInstance> filterInstances = new ArrayList<>();
        List<SortInstance> sortInstances = new ArrayList<>();
        private Boolean unidentified;
        private Boolean shiny;

        public QueryBuilder() {
            queryInstance = this;
        }

        public Operator build() {
            operator = new Operator();
            filterInstances.forEach(filterInstance -> {
                if (filterInstance.relative) {
                    operator.filterByIdRoll(filterInstance.id(), filterInstance.relation, filterInstance.value, refreshQueriedItem().get());
                } else {
                    operator.filterByIdRaw(filterInstance.id(), filterInstance.relation, filterInstance.value);
                }
            });
            if (unidentified != null) operator.filterUnidentified(unidentified);
            if (shiny != null) operator.filterShiny(shiny);
            Comparator<MarketItem> sorter = createSorter();
            if (sorter != null) {
                operator.itemStream = operator.itemStream.sorted(sorter);
            }
            return operator;
        }

        public void setUnidentified(Boolean unidentified) {
            this.unidentified = unidentified;
        }

        public void setShiny(Boolean shiny) {
            this.shiny = shiny;
        }

        private Comparator<MarketItem> createSorter() {
            Comparator<MarketItem> comparator = Comparator.naturalOrder();

            for (SortInstance sortInstance : sortInstances) {
                comparator = comparator.thenComparingInt(item -> {
                    int key = item.item.get(sortInstance.id());
                    return sortInstance.increasing ? key : -key;
                });
            }
            return comparator;
        }
    }
    public static class QueryInstance {
        private final RolledID id;

        QueryInstance(RolledID id) {
            this.id = id;
        }

        public RolledID id() {
            return id;
        }
    }
    public static final class FilterInstance extends QueryInstance {
        private final Relation relation;
        private final double value;
        private final boolean relative;

        FilterInstance(RolledID id, Relation relation, double value, boolean relative) {
            super(id);
            this.relation = relation;
            this.value = value;
            this.relative = id != AllIDs.PRICE && relative;
        }

        public Relation relation() {
            return relation;
        }

        public double value() {
            return value;
        }

        public boolean relative() {
            return relative;
        }

    }

    public static final class SortInstance extends QueryInstance {
        private final boolean increasing;

        SortInstance(RolledID id, boolean increasing) {
            super(id);
            this.increasing = increasing;
        }

        public boolean increasing() {
            return increasing;
        }
    }
}
