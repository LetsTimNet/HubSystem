package net.letstim.hubSystem.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class RealTimeConfig {
    private final JavaPlugin plugin;
    private final File configFile;
    private FileConfiguration config;

    public RealTimeConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "RealTime.yml");
        load();
    }

    public void load() {
        if (!configFile.exists()) {
            createDefault();
            return;
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    private void createDefault() {
        configFile.getParentFile().mkdirs();
        config = new YamlConfiguration();

        config.set("timezone", "Europe/Berlin");
        config.set("sync-interval-seconds", 1);

        addTimezoneComments();
        save();
    }

    private void addTimezoneComments() {
        List<String> comments = new ArrayList<>();
        comments.add("RealTime Configuration");
        comments.add("");
        comments.add("timezone: Die Zeitzone für die Weltzeit-Synchronisation");
        comments.add("  Sonnenauf- und Untergangszeiten werden automatisch");
        comments.add("  basierend auf der Zeitzone und dem Datum berechnet.");
        comments.add("");
        comments.add("sync-interval-seconds: Wie oft die Zeit synchronisiert wird (in Sekunden)");
        comments.add("");
        comments.add("Gültige Timezones:");

        List<String> sortedZones = ZoneId.getAvailableZoneIds().stream()
                .sorted()
                .toList();

        for (int i = 0; i < Math.min(sortedZones.size(), 100); i++) {
            comments.add("  - " + sortedZones.get(i));
        }

        comments.add("  ... und " + (sortedZones.size() - 100) + " weitere");
        comments.add("");
        comments.add("Vollständige Liste: https://en.wikipedia.org/wiki/List_of_tz_database_time_zones");

        config.setComments("timezone", comments);
    }

    private void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save RealTime.yml: " + e.getMessage());
        }
    }

    public String getTimezone() {
        return config.getString("timezone", "Europe/Berlin");
    }

    public void setTimezone(String timezone) {
        config.set("timezone", timezone);
        save();
    }

    public int getSyncIntervalSeconds() {
        return config.getInt("sync-interval-seconds", 1);
    }
}
