package com.gertoxq.wynntrader.util;

import com.gertoxq.wynntrader.Wynntrader;
import com.gertoxq.wynntrader.custom.AllIDs;
import com.gertoxq.wynntrader.custom.CustomItem;
import com.gertoxq.wynntrader.custom.ID;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WynnData {

    private static final Map<Integer, ItemData> dataMap = new HashMap<>();
    private static final Map<String, Integer> nameToId = new HashMap<>();
    private static final Map<String, ID.Tier> tierMap = new HashMap<>();

    public static Map<Integer, ItemData> getData() {
        return dataMap;
    }

    public static Map<String, Integer> getIdMap() {
        return nameToId;
    }

    public static Map<String, ID.Tier> getTierMap() {
        return tierMap;
    }

    public static void load() {
        InputStream dataStream = Wynntrader.class.getResourceAsStream("/" + "wynntrader.data.json");
        try {
            assert dataStream != null;
            ((JsonObject) JsonParser.parseReader(
                    new InputStreamReader(dataStream, StandardCharsets.UTF_8))).asMap().forEach((s, jsonElement) -> {
                int id = Integer.parseInt(s);
                String encodedItem = jsonElement.getAsString();

                CustomItem item = CustomItem.getCustomFromHash(encodedItem, a -> a);
                item.set(AllIDs.OVERALL_ROLL, 0);
                item.set(AllIDs.PRICE, 0);

                nameToId.put(item.getName(), id);
                tierMap.put(item.getName(), item.get(AllIDs.TIER));

                dataMap.put(id, new ItemData(id, item));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public record ItemData(int id,
                           CustomItem baseItem) {}


    public static List<ID.ItemType> types = List.of(ID.ItemType.Helmet, ID.ItemType.Chestplate, ID.ItemType.Leggings, ID.ItemType.Boots, ID.ItemType.Ring, ID.ItemType.Ring, ID.ItemType.Bracelet, ID.ItemType.Necklace);

}
