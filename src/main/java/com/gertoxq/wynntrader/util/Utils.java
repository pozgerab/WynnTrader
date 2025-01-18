package com.gertoxq.wynntrader.util;

import com.gertoxq.wynntrader.custom.CustomItem;
import com.gertoxq.wynntrader.screens.BaseScreen;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.gertoxq.wynntrader.client.WynntraderClient.getConfigManager;

public class Utils {

    public static boolean isBetween(int number, int min, int max) {
        return number >= min && number <= max;
    }

    public static String capitalizeWords(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Arrays.stream(input.split(" "))
                .map(Utils::capitalize)
                .collect(Collectors.joining(" "));
    }

    public static String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public static void readScreenInfo(BaseScreen baseScreen) {
        String title = baseScreen.getScreen().getTitle().getString();
        JsonArray titleCodes = new JsonArray();
        for (int i = 0; i < title.length(); i++) {
            char ch = title.charAt(i);
            titleCodes.add(String.format("\\u%04x", (int) ch));
        }
        Gson gson = new Gson();
        for (int i = 0; i < baseScreen.getScreen().getTitle().getString().length(); i++) {
            char ch = baseScreen.getScreen().getTitle().getString().charAt(i);
            titleCodes.add(String.format("\\u%04x", (int) ch));
        }
        System.out.println("Title: " + baseScreen.getScreen().getTitle());
        List<JsonObject> dumps = getConfigManager().getConfig().getDumps();
        JsonArray slots = new JsonArray();
        JsonObject object = new JsonObject();
        object.add("encodedTitle", titleCodes);
        object.add("title", gson.toJsonTree(TextCodecs.CODEC.encodeStart(JsonOps.INSTANCE, baseScreen.getScreen().getTitle().copy()).getOrThrow()));
        AtomicBoolean stop = new AtomicBoolean(false);
        baseScreen.getHandler().slots.forEach(slot -> {
            if (stop.get()) return;
            if (slot.getIndex() == 53) stop.set(true);
            JsonObject item = new JsonObject();
            if (slot.getStack().isEmpty()) return;
            List<Text> texts = getLore(slot.getStack());
            if (texts == null) return;
            JsonArray array = new JsonArray();
            texts.forEach(text -> {
                JsonObject textMultiple = new JsonObject();
                textMultiple.addProperty("raw", text.getString());
                //textMultiple.add("text", gson.toJsonTree(TextCodecs.CODEC.encodeStart(JsonOps.INSTANCE, text).getOrThrow()));
                array.add(text.getString());
            });
            Text name = slot.getStack().getName();
            item.add("name", gson.toJsonTree(TextCodecs.CODEC.encodeStart(JsonOps.INSTANCE, name).getOrThrow()));
            item.addProperty("index", slot.getIndex());
            item.addProperty("count", slot.getStack().getCount());
            item.addProperty("material", slot.getStack().getItem().getName().toString());
            item.add("lore", array);
            slots.add(item);
        });
        object.add("slots", slots);
        dumps.add(object);
        getConfigManager().getConfig().setDumps(dumps);
        getConfigManager().saveConfig();
    }

    public static @Nullable List<Text> getLore(@NotNull ItemStack itemStack) {
        LoreComponent loreComp = itemStack.get(DataComponentTypes.LORE);
        if (loreComp == null) return null;
        //System.out.println(loreComp.lines());
        return loreComp.lines();
    }

    public static String removeFormat(@NotNull String str) {
        return str.replaceAll("§[0-9a-fA-Fklmnor]", "");
    }

    public static String removeNum(String str) {
        var rep = List.of(" 1", " 2", " 3", " III", " II", " I");
        AtomicReference<String> news = new AtomicReference<>(str);
        rep.forEach(s -> news.set(news.get().replace(s, "")));
        return news.get();
    }

    public static MutableText reduceTextList(List<Text> ogLore) {
        MutableText lore = Text.empty();
        for (int i = 0; i < ogLore.size(); i++) {
            Text line = ogLore.get(i);
            lore.append(line);
            if (i != ogLore.size() - 1) {
                lore.append("\n");
            }
        }
        return lore;
    }

    public static String getItemOriginalName(String string) {
        string = string.replaceAll("⬡ Shiny ", "");
        string = string.replaceAll("Unidentified ", "");
        return string;
    }

    public static @NotNull String removeTilFormat(@NotNull String string) {
        return string.replaceAll(CustomItem.tilsSchema, "").replace("*", "").replace("?", "").replace("À", "");
    }

    public final static ImmutableList<Integer> DIGIT_KEYS = ImmutableList.of(48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105);

}
