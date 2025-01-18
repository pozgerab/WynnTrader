package com.gertoxq.wynntrader.config;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class ConfigType {
    private List<JsonObject> dumps = new ArrayList<>();

    public List<JsonObject> getDumps() {
        return dumps;
    }

    public void setDumps(List<JsonObject> dumps) {
        this.dumps = dumps;
    }
}
