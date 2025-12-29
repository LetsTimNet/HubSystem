package net.letstim.hubSystem.realtime;

import net.letstim.hubSystem.HubSystem;
import net.letstim.hubSystem.config.RealTimeConfig;
import org.bukkit.command.PluginCommand;

public class RealTime {
    private static RealTime instance;

    private final HubSystem plugin;
    private final RealTimeConfig config;
    private WorldTimeSync worldTimeSync;

    public RealTime() {
        instance = this;
        this.plugin = HubSystem.getInstance();
        this.config = new RealTimeConfig(plugin);

        registerCommand();
        startSync();
    }

    private void registerCommand() {
        PluginCommand cmd = plugin.getCommand("timezone");
        if (cmd == null) {
            plugin.getLogger().severe("Command 'timezone' not found in plugin.yml!");
            return;
        }

        TimezoneCommand executor = new TimezoneCommand(this);
        cmd.setExecutor(executor);
        cmd.setTabCompleter(executor);
    }

    private void startSync() {
        stopSync();

        int intervalTicks = config.getSyncIntervalSeconds() * 20;
        worldTimeSync = new WorldTimeSync(this);
        worldTimeSync.runTaskTimer(plugin, 0L, intervalTicks);

        SolarCalculator.SunTimes sunTimes = SolarCalculator.calculateSunTimes(
            java.time.ZoneId.of(config.getTimezone())
        );

        plugin.getLogger().info("RealTime sync started:");
        plugin.getLogger().info("  Timezone: " + config.getTimezone());
        plugin.getLogger().info("  Interval: " + config.getSyncIntervalSeconds() + "s");
        plugin.getLogger().info("  Sunrise: " + sunTimes.getSunrise());
        plugin.getLogger().info("  Sunset: " + sunTimes.getSunset());
    }

    private void stopSync() {
        if (worldTimeSync == null) {
            return;
        }
        worldTimeSync.cancel();
        worldTimeSync = null;
    }

    public void restartSync() {
        stopSync();
        startSync();
    }

    public void disable() {
        stopSync();
    }

    public static RealTime getInstance() {
        return instance;
    }

    public RealTimeConfig getConfig() {
        return config;
    }

    public HubSystem getPlugin() {
        return plugin;
    }
}
