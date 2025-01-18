package com.gertoxq.wynntrader.custom;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Cast {
    Warrior("Warrior", "Knight", List.of("Bash", "Charge", "Uppercut", "War Scream"), ID.ItemType.Spear),
    Archer("Archer", "Hunter", List.of("Arrow Storm", "Escape", "Arrow Bomb", "Arrow Shield"), ID.ItemType.Bow),
    Assassin("Assassin", "Ninja", List.of("Spin Attack", "Dash", "Multi Hit", "Smoke Bomb"), ID.ItemType.Dagger),
    Mage("Mage", "Dark Wizard", List.of("Heal", "Teleport", "Meteor", "Ice Snake"), ID.ItemType.Wand),
    Shaman("Shaman", "Skyseer", List.of("Totem", "Haul", "Aura", "Uproot"), ID.ItemType.Relik);
    public final String name;
    public final List<String> aliases = new ArrayList<>();
    public final String alias;
    public final List<String> abilities;
    public final ID.ItemType weapon;

    Cast(String name, String alias, List<String> abilityNames, ID.ItemType weapon) {
        this.name = name;
        this.abilities = abilityNames;
        this.alias = alias;
        this.weapon = weapon;
        this.aliases.add(name);
        this.aliases.add(alias);
    }

    public static @Nullable Cast findByWeapon(ID.ItemType weapon) {
        return Arrays.stream(Cast.values()).filter(cast -> cast.weapon.equals(weapon)).findAny().orElse(null);
    }

    @Nullable
    public static Cast find(String alias) {
        return Arrays.stream(Cast.values()).filter(cast -> cast.aliases.stream().map(String::toLowerCase).toList().contains(alias.toLowerCase())).findFirst().orElse(null);
    }
}
