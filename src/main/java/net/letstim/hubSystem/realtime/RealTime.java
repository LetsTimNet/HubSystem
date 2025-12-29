package net.letstim.hubSystem.realtime;

import net.letstim.hubSystem.HubSystem;
import net.letstim.hubSystem.config.RealTimeConfig;
import org.bukkit.command.PluginCommand;

public class RealTime {
    private static RealTime instance;

    private final HubSystem plugin;
    private final RealTimeConfig config;
    private WorldTimeSync worldTimeSync;
    private NewYearFirework newYearFirework;

    public RealTime() {
        instance = this;
        this.plugin = HubSystem.getInstance();
        this.config = new RealTimeConfig(plugin);

        registerCommand();
        startSync();
        startNewYearFirework();
    }

    private void registerCommand() {
        PluginCommand timezoneCmd = plugin.getCommand("timezone");
        if (timezoneCmd == null) {
            plugin.getLogger().severe("Command 'timezone' not found in plugin.yml!");
        } else {
            TimezoneCommand executor = new TimezoneCommand(this);
            timezoneCmd.setExecutor(executor);
            timezoneCmd.setTabCompleter(executor);
        }

        PluginCommand fireworkDebugCmd = plugin.getCommand("fireworkdebug");
        if (fireworkDebugCmd == null) {
            plugin.getLogger().severe("Command 'fireworkdebug' not found in plugin.yml!");
        } else {
            FireworkDebugCommand executor = new FireworkDebugCommand(this);
            fireworkDebugCmd.setExecutor(executor);
        }
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

    private void startNewYearFirework() {
        stopNewYearFirework();
        newYearFirework = new NewYearFirework(this);
        newYearFirework.start();
        plugin.getLogger().info("NewYear Firework system started.");
    }

    private void stopNewYearFirework() {
        if (newYearFirework == null) {
            return;
        }
        newYearFirework.stop();
        newYearFirework = null;
    }

    public void restartSync() {
        stopSync();
        startSync();
    }

    public void startFireworkDebug() {
        if (newYearFirework != null) {
            newYearFirework.startDebug();
        }
    }

    public void disable() {
        stopSync();
        stopNewYearFirework();
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
