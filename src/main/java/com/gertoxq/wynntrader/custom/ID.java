package com.gertoxq.wynntrader.custom;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openal.AL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ID {

    protected final static List<ID> allIDs = new ArrayList<>();
    public final @NotNull String name;
    public final @NotNull String displayName;
    final @NotNull PutOn on;
    final @NotNull Object defaultValue;
    final StaticMetric metric;
    final boolean rolled;

    protected ID(@NotNull PutOn on, @NotNull Object defaultValue, @NotNull String name, @NotNull String displayName, StaticMetric metric, boolean rolled) {
        this.name = name;
        this.displayName = displayName;
        this.on = on;
        this.defaultValue = defaultValue;
        this.metric = metric;
        this.rolled = rolled;
        allIDs.add(this);
    }

    protected ID(PutOn on, Object defaultValue, String name, boolean rolled) {
        this(on, defaultValue, name, "", Metric.OTHERSTR, rolled);
    }

    public static List<ID> values() {
        return ImmutableList.copyOf(allIDs);
    }

    public static List<ID> getByMetric(StaticMetric metric) {
        return allIDs.stream().filter(ids -> ids.metric == metric).toList();
    }

    public static ID getByName(String name) {
        return allIDs.stream().filter(ids -> name.equals(ids.name)).findAny().orElse(null);
    }

    public boolean isStatic() {
        return List.of(AllIDs.PRICE, AllIDs.OVERALL_ROLL).contains(this);
    }

    public static TypedID<Integer> getByDisplay(String displayName, List<TypedID<Integer>> possibles) {
        Optional<TypedID<Integer>> foundId = possibles.stream().filter(id -> id.displayName.equals(displayName)).findAny();
        if (foundId.isPresent()) {
            return foundId.get();
        }

        if (displayName.contains(" Cost")) {
            List<TypedID<Integer>> costReds = possibles.stream().filter(id1 -> id1.displayName.contains("&")).toList();
            if (Character.isDigit(displayName.charAt(0))) {
                int index;
                index = Integer.parseInt(String.valueOf(displayName.charAt(0)));
                return costReds.stream().filter(potentialRed -> potentialRed.displayName.contains("&"+index)).findAny().get();
            } else {
                for (var costReductionID : costReds) {
                    int nO;
                    try {
                        nO = Integer.parseInt(costReductionID.displayName.split("&")[1]) - 1;
                    } catch (Exception ignored) {
                        continue;
                    }
                    if (Arrays.stream(Cast.values()).anyMatch(cast -> displayName.equals(cast.abilities.get(nO) + " Cost"))) {
                        break;
                    }
                }
            }
        }
        return null;
    }

    public static ID getByNameIgnoreCase(String name) {
        return allIDs.stream().filter(ids -> name.equalsIgnoreCase(ids.name)).findAny().orElse(null);
    }

    public static <T, R> @NotNull List<DoubleID<T, R>> getByDoubleMetric(DoubleTypedMetric<T, R> metric) {
        List<DoubleID<T, R>> result = new ArrayList<>();

        for (DoubleID<?, ?> doubleID : DoubleID.getDoubleIds()) {
            if (doubleID.getMetric().equals(metric)) {
                // This cast is safe because we're checking the equality of metrics
                @SuppressWarnings("unchecked")
                DoubleID<T, R> matchedDoubleID = (DoubleID<T, R>) doubleID;
                result.add(matchedDoubleID);
            }
        }

        return result;
    }

    public static <T> @NotNull List<TypedID<T>> getByTypedMetric(TypedMetric<T> metic) {
        List<TypedID<T>> result = new ArrayList<>();
        for (TypedID<?> id : TypedID.getTypedIds()) {
            if (id.getMetric().equals(metic)) {
                @SuppressWarnings("unchecked")
                TypedID<T> matchedID = (TypedID<T>) id;
                result.add(matchedID);
            }
        }
        return result;
    }

    public boolean isReq() {
        List<ID> ids = List.of(AllIDs.LVL, AllIDs.CLASS_REQ, AllIDs.AGI_REQ, AllIDs.DEF_REQ, AllIDs.DEX_REQ, AllIDs.INT_REQ, AllIDs.STR_REQ);
        return ids.contains(this);
    }

    public boolean isSpellCostReduction() {
        return Stream.of(AllIDs.SP_PCT1, AllIDs.SP_PCT2, AllIDs.SP_PCT3, AllIDs.SP_PCT4, AllIDs.SP_RAW1, AllIDs.SP_RAW2, AllIDs.SP_RAW3, AllIDs.SP_RAW4).map(rolledID -> rolledID.name).toList().contains(name);
    }

    public <T, R> boolean matchesTypes(Class<T> raw, Class<R> parsed) {
        return this instanceof DoubleID<?, ?> doub && doub.type == parsed && doub.getParsedType() == raw;
    }

    @SuppressWarnings("unchecked")
    public <T, R> DoubleID<T, R> convertToDoubleID(Class<T> raw, Class<R> parsed) {
        if (matchesTypes(raw, parsed)) {
            return (DoubleID<T, R>) this;
        }
        return null;
    }

    public boolean isMorePositive() {
        List<ID> ids = List.of(AllIDs.SP_PCT1, AllIDs.SP_PCT2, AllIDs.SP_PCT3, AllIDs.SP_PCT4, AllIDs.SP_RAW1, AllIDs.SP_RAW2, AllIDs.SP_RAW3, AllIDs.SP_RAW4);
        return !ids.contains(this);
    }

    public enum PutOn {
        ALL,
        WEAPON,
        ARMOR,
        CONSUMABLE
    }

    public enum ATKSPDS {
        SUPER_SLOW,
        VERY_SLOW,
        SLOW,
        NORMAL,
        FAST,
        VERY_FAST,
        SUPER_FAST

    }

    public enum Tier {
        Normal("§f", Formatting.WHITE),
        Unique("§e", Formatting.YELLOW),
        Rare("§d", Formatting.LIGHT_PURPLE),
        Legendary("§b", Formatting.AQUA),
        Fabled("§c", Formatting.RED),
        Mythic("§5", Formatting.DARK_PURPLE),
        Set("§a", Formatting.GREEN),
        Crafted("§3", Formatting.DARK_AQUA);
        public final String color;
        public final Formatting format;

        Tier(String color, Formatting format) {
            this.color = color;
            this.format = format;
        }
    }

    public enum ItemType {
        Helmet,
        Chestplate,
        Leggings,
        Boots,
        Ring,
        Bracelet,
        Necklace,
        Wand,
        Spear,
        Bow,
        Dagger,
        Relik,
        Potion,
        Scroll,
        Food,
        Tome,
        Charm;

        public static final List<ItemType> armors = List.of(Helmet, Chestplate, Leggings, Boots);
        public static final List<ItemType> weapons = List.of(Wand, Spear, Bow, Dagger, Relik);

        public @Nullable Cast getCast() {
            return Cast.findByWeapon(this);
        }

        public boolean isWeapon() {
            return weapons.contains(this);
        }

        public boolean isArmor() {
            return armors.contains(this);
        }
    }

    public static class StaticMetric {
        public final String sign;

        private StaticMetric(String name) {
            this.sign = name;
        }
    }

    public static class TypedMetric<R> extends StaticMetric {
        private TypedMetric(String name) {
            super(name);
        }

        @Contract(" -> new")
        public static <T> @NotNull TypedMetric<T> createOther() {
            return new TypedMetric<>("other");
        }
    }

    public static class DoubleTypedMetric<T, R> extends TypedMetric<R> {
        final DoubleID.Parser<T, R> translator;

        private DoubleTypedMetric(String name, DoubleID.Parser<T, R> translator) {
            super(name);
            this.translator = translator;
        }
    }

    public static class Metric {
        public static final TypedMetric<Integer> PERCENT = new TypedMetric<>(" %");
        public static final TypedMetric<Integer> RAW = new TypedMetric<>("");
        public static final TypedMetric<Integer> PRICE = new TypedMetric<>("");
        public static final DoubleTypedMetric<DoubleID.Range, String> RANGE = new DoubleTypedMetric<>("range", DoubleID.Parser.rangeParser());
        public static final TypedMetric<Integer> PERXS = new TypedMetric<>("");
        public static final StaticMetric REQ = new StaticMetric("req");
        public static final TypedMetric<String> OTHERSTR = new TypedMetric<>("other");
        public static final TypedMetric<Integer> OTHERINT = new TypedMetric<>("other");
        public static final DoubleTypedMetric<Tier, String> TIER = new DoubleTypedMetric<>("tier", DoubleID.Parser.enumParser(Tier.Normal));
        public static final DoubleTypedMetric<ItemType, String> TYPE = new DoubleTypedMetric<>("type", DoubleID.Parser.enumParser(ItemType.Helmet));
        public static final DoubleTypedMetric<ATKSPDS, String> ATTACK_SPEED = new DoubleTypedMetric<>("attack_speed", DoubleID.Parser.enumNullableParser("", ATKSPDS.class));
        public static final DoubleTypedMetric<Cast, String> CAST = new DoubleTypedMetric<>("cast", DoubleID.Parser.enumNullableParser("", Cast.class));
        public static final TypedMetric<Boolean> BOOL = new TypedMetric<>("bool");
    }
}
