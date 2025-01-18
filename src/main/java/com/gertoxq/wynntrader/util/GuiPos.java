package com.gertoxq.wynntrader.util;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GuiPos {

    final int index;
    final String title;
    final boolean nullable;
    protected GuiPos(int pos, String title) {
        this.index = pos;
        this.title = title;
        this.nullable = false;
    }

    public GuiPos(int pos, String title, boolean nullable) {
        this.index = pos;
        this.title = title;
        this.nullable = nullable;
    }

    public int getIndex() {
        return index;
    }

    public String getTitle() {
        return title;
    }

    public boolean isNullable() {
        return nullable;
    }

    public static class Market {
        public static final GuiPos SELL = new GuiPos(45, "Sell an Item");
        public static final GuiPos TRADES = new GuiPos(46, "View Trades");
        public static final GuiPos SEARCH = new GuiPos(47, "Search and Filter");
        public static final GuiPos PAGE_UP = new GuiPos(51, "Previous Page", true);
        public static final GuiPos SORT = new GuiPos(52, "Sort");
        public static final GuiPos PAGE_DOWN = new GuiPos(53, "Next Page", true);
        public static final String noFilterSortName = "Level Filter";
        public static final String sortName = "Sort Results";
        public static final ImmutableList<Integer> slots = ImmutableList.copyOf(IntStream.rangeClosed(0, 44).boxed().toList());
    }

    public static class Sell {
        public static final GuiPos BACK = new GuiPos(46, "Back");
        public static final GuiPos SET_PRICE = new GuiPos(28, "Set Price", true);
        public static final GuiPos ITEM = new GuiPos(22, "Empty Item Slot");
        public static final GuiPos CONFIRM = new GuiPos(38, "Confirm");
        public static final GuiPos AMOUNT = new GuiPos(31, "Set Amount", true);
        public static final GuiPos DECREASE_AMOUNT = new GuiPos(30, "Decrease Amount", true);
        public static final GuiPos INCREASE_AMOUNT = new GuiPos(32, "Increase Amount",true);
        public static final GuiPos PRICE_CHECK = new GuiPos(52, "Price Check");

        public static final String validConfirm = "Sell Order Summary";
        public static final String invalidConfirm = "Waiting for an Item";
        public static final String emptyItem = ITEM.title;
    }

    public static class Trades {
        public static final GuiPos BACK = new GuiPos(46, "Back");
        public static final ImmutableList<Integer> alwaysUnlockedSlots = ImmutableList.of(11, 12, 13, 14, 15);
        public static final ImmutableList<Integer> marketSlots = ImmutableList.of(11, 12, 13, 14, 15,
                                                                            20, 21, 22, 23, 24,
                                                                            29, 30, 31, 32, 33);
        public static final String AVAILABLE_SLOT = "Available Slot";
        public static final String UNAVAILABLE_SLOT = "Locked Trade Slot";
    }

    public static class Buy {

        public static final GuiPos BACK = new GuiPos(46, "Back");
        public static final GuiPos SET_PRICE = new GuiPos(28, "Set Price");
        public static final GuiPos ITEM = new GuiPos(22, "Empty Item Slot");
        public static final GuiPos CONFIRM = new GuiPos(38, "Buy Order Summary");
        public static final GuiPos AMOUNT = new GuiPos(31, "Set Amount");
        public static final GuiPos DECREASE_AMOUNT = new GuiPos(30, "Decrease Amount");
        public static final GuiPos INCREASE_AMOUNT = new GuiPos(32, "Increase Amount");
        public static final GuiPos PRICE_CHECK = new GuiPos(52, "Price Check");
    }

    public static class Order {

        public static final GuiPos BACK = new GuiPos(46, "Back");
        public static final GuiPos ITEM = new GuiPos(28, "ITEM");
        public static final GuiPos WITHDRAW = new GuiPos(28, "Withdraw Items");
        public static final ImmutableList<Integer> CLAIMS = ImmutableList.of(21, 22, 23, 24, 25,
                                                                            30, 31, 32, 33, 34);
        public static final String UNFILLED = "Cancel Order";

    }

    public static class Filter {

        public static final GuiPos BACK = new GuiPos(46, "Back");
        public static final GuiPos PAGE_UP = new GuiPos(50, "Previous Page", true);
        public static final GuiPos PAGE_DOWN = new GuiPos(52, "Next Page", true);
        public static final ImmutableList<Integer> ACTIVE_FILTERS = ImmutableList.of(0,1,2, 9,10,11, 18,19,20, 27,28,29, 36,37,38);
        public static final ImmutableList<Integer> INACTIVE_FILTERS = ImmutableList.of(
                 4,  5,  6,  7,  8,
                13, 14, 15, 16, 17,
                22, 23, 24, 25, 26,
                31, 32, 33, 34, 35,
                40, 41, 42, 42, 44);
    }
}
