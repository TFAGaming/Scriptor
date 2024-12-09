package com.scriptor.core.plugins;

import org.json.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ScriptorPluginsHandler {
    private final List<ScriptorPlugin> plugins = new ArrayList<ScriptorPlugin>();
    private final String pluginsDirectory;

    public ScriptorPluginsHandler(String directory) {
        this.pluginsDirectory = directory;
    }

    public void loadPlugins() {
        plugins.clear();
        File directory = new File(pluginsDirectory);

        if (!directory.exists() || !directory.isDirectory()) {
            directory.mkdirs();
        }

        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
        if (files != null) {
            for (File file : files) {
                try {
                    String content = Files.readString(Path.of(file.getPath()));

                    JSONObject json = new JSONObject(content);
                    ScriptorPlugin plugin = new ScriptorPlugin(json, file);

                    plugins.add(plugin);
                } catch (IOException e) {
                    System.err.println("Error reading file: " + file.getName());
                    e.printStackTrace();
                } catch (Exception e) {
                    System.err.println("Error parsing JSON in file: " + file.getName());
                    e.printStackTrace();
                }
            }
        }
    }

    public ScriptorPluginConfig getMergedConfig() {
        JSONObject obj = new JSONObject();

        for (ScriptorPlugin plugin : plugins) {
            if (plugin.getEnabled()) {
                ScriptorPluginConfig config = plugin.getConfig();

                deepMerge(config.getJSON(), obj);
            }
        }

        return new ScriptorPluginConfig(obj);
    }

    public List<ScriptorPlugin> getPlugins() {
        return plugins;
    }

    private JSONObject deepMerge(JSONObject source, JSONObject target) {
        for (String key : JSONObject.getNames(source)) {
            Object value = source.get(key);
            if (!target.has(key)) {
                target.put(key, value);
            } else {
                if (value instanceof JSONObject) {
                    JSONObject valueJson = (JSONObject) value;
                    deepMerge(valueJson, target.getJSONObject(key));
                } else {
                    target.put(key, value);
                }
            }
        }
        return target;
    }
}