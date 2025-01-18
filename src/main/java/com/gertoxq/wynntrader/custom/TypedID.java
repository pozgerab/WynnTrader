package com.gertoxq.wynntrader.custom;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TypedID<T> extends ID {

    private static final List<TypedID<?>> typedIds = new ArrayList<>();
    final Class<T> type;
    final T defaultValue;
    private final TypedMetric<T> metric;

    @SuppressWarnings("unchecked")
    protected TypedID(PutOn on, T defaultValue, String name, String displayName, TypedMetric<T> metric, boolean rolled) {
        super(on, defaultValue, name, displayName, metric, rolled);
        this.type = (Class<T>) defaultValue.getClass();
        this.defaultValue = defaultValue;
        this.metric = metric;
        typedIds.add(this);
    }

    TypedID(PutOn on, T defaultValue, String name, boolean rolled) {
        this(on, defaultValue, name, "", TypedMetric.createOther(), rolled);
    }

    public String getAsString(@Nullable Cast cast) {
        if (displayName.contains("&")) {

            int nO = Integer.parseInt(displayName.split("&")[1]);
            if (cast == null) {
                return nO + ". Spell Cost" + getMetric().sign;
            } else {
                String abilName = cast.abilities.get(nO - 1);
                return abilName + " Cost" + getMetric().sign;
            }
        }
        return displayName + getMetric().sign;
    }

    public static List<TypedID<?>> getTypedIds() {
        return typedIds;
    }

    public Class<T> getType() {
        return type;
    }

    public TypedMetric<T> getMetric() {
        return metric;
    }
}
