package com.gertoxq.wynntrader.screens;

import com.gertoxq.wynntrader.custom.AllIDs;
import com.gertoxq.wynntrader.custom.ID;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import org.lwjgl.openal.AL;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Deprecated(forRemoval = true)
public class FilterScreen extends BaseScreen {
    public FilterScreen(GenericContainerScreen screen) {
        super(screen);
    }

    public static class Filter {

        public static final List<Filter> filterList = new ArrayList<>();
        static {
            filterList.add(new Filter(FilterType.NAME, "Name Contains"));
            filterList.addAll(IntStream.rangeClosed(0, 9).boxed().map(integer ->
                    new Filter(FilterType.LEVEL, "Level " + (integer * 10 + 1) + "-" + ((integer + 1) * 10) + " Only")).toList());
            filterList.addAll(
                    Stream.of(ID.Tier.Crafted, ID.Tier.Normal, ID.Tier.Unique, ID.Tier.Rare, ID.Tier.Legendary, ID.Tier.Set, ID.Tier.Fabled, ID.Tier.Mythic)
                            .map(tier -> new Filter(FilterType.RARITY, tier.name()+ " Items Only")).toList());
            filterList.addAll(Stream.of("Fishing", "Woodcutting", "Mining", "Farming", "Scribing", "Jeweling", "Alchemism", "Cooking", "Weaponsmithing",
                    "Tailoring", "Woodworking", "Armouring").map(string -> new Filter(FilterType.PROFESSION, string + " Skill Only")).toList());
            filterList.addAll(IntStream.rangeClosed(0, 4).boxed().map(integer -> new Filter(FilterType.MATERIAL_TIER, "Tier " + integer + " Only")).toList());
            filterList.add(new Filter(FilterType.REFINEMENT, "Unrefined Materials Only"));
            filterList.add(new Filter(FilterType.REFINEMENT, "Refined Materials Only"));
            filterList.addAll(Stream.of("Ingredients", "Tools", "Powders", "Other Items").map(string -> new Filter(FilterType.OTHER_TYPE, string + " Only")).toList());
            filterList.addAll(Stream.of(ID.ItemType.Potion, ID.ItemType.Scroll, ID.ItemType.Food, ID.ItemType.Ring, ID.ItemType.Bracelet, ID.ItemType.Necklace,
                    ID.ItemType.Helmet, ID.ItemType.Chestplate, ID.ItemType.Leggings, ID.ItemType.Boots, ID.ItemType.Spear, ID.ItemType.Dagger,
                    ID.ItemType.Bow, ID.ItemType.Wand, ID.ItemType.Relik, ID.ItemType.Tome, ID.ItemType.Charm).map(itemType ->
                    new Filter(FilterType.ITEM_TYPE, itemType.name() + " Type Only")).toList());
            filterList.addAll(Stream.of("Normal", "Loot Chest", "Dungeon", "Mob", "Never").map(string -> new Filter(FilterType.DROP, string + " Drops Only")).toList());
            /*filterList.addAll(Stream.of(
                    AllIDs.STR, AllIDs.DEX, AllIDs.INT, AllIDs.AGI, AllIDs.DEF, AllIDs.HPR_PCT, AllIDs.KB, AllIDs.MR, AllIDs.LS,
                    AllIDs.MS, AllIDs.XPB, AllIDs.LB, AllIDs.REF, AllIDs.THORNS, AllIDs.EXPD, AllIDs.SPD, AllIDs.ATKSPD, AllIDs.POISON,
                    AllIDs.HP, AllIDs.E_STEAL, AllIDs.HPR_RAW, AllIDs.HEAL_PCT
            ));*/
        }

        final FilterType filterType;
        final String name;
        private Filter(FilterType filterType, String name) {
            this.filterType = filterType;
            this.name = name;
        }

        public enum FilterType {
            NAME,
            LEVEL,
            RARITY,
            PROFESSION,
            MATERIAL_TIER,
            REFINEMENT,
            OTHER_TYPE,
            ITEM_TYPE,
            DROP,
            IDENTIFICATION
        }
    }
}
