package com.scriptor.core.plugins;

import java.io.File;
import java.io.IOException;

import org.json.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.scriptor.core.plugins.config.ScriptorPluginConfig;

public class ScriptorPlugin {
    private final JSONObject json;
    private final File sourceFile;

    private String name;
    private String description;
    private String version;
    private boolean enabled;
    private ScriptorPluginConfig config;

    public ScriptorPlugin(JSONObject json, File sourceFile) {
        this.json = json;
        this.sourceFile = sourceFile;

        this.name = json.optString("name", "Unnamed Plugin");
        this.description = json.optString("description", "No description provided");
        this.version = json.optString("version", "0.0.0");
        this.enabled = json.optBoolean("enabled", false);
        this.config = new ScriptorPluginConfig(json.optJSONObject("config"));
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        updateEnabledValue();
    }

    public ScriptorPluginConfig getConfig() {
        return this.config;
    }

    public JSONObject getConfigJSON() {
        return this.config.getJSON();
    }

    private void updateEnabledValue() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

            json.put("enabled", this.enabled);

            writer.writeValue(sourceFile, json.toMap());
        } catch (IOException e) {
        }
    }

    public File getFile() {
        return sourceFile;
    }
}
