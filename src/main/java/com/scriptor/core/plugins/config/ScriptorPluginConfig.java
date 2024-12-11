package com.scriptor.core.plugins.config;

import org.json.JSONObject;

public class ScriptorPluginConfig {
    private final JSONObject json;

    public ScriptorPluginConfig(JSONObject json) {
        this.json = json;
    }

    public Object get(String key) {
        return this.json.opt(key);
    }

    public JSONObject getJSON() {
        return this.json;
    }
}