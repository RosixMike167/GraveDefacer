package it.ziopagnotta.gravedefacer.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class PluginConfig {
    private final File file;

    private YamlConfiguration config;

    public PluginConfig(@NotNull JavaPlugin plugin, @NotNull String configName) {
        file = new File(plugin.getDataFolder(), configName + ".yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(configName + ".yml", false);
        }

        reload();
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public boolean getBoolean(String path) { return config.getBoolean(path); }

    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    public YamlConfiguration asBukkitConfig() {
        return this.config;
    }
}
