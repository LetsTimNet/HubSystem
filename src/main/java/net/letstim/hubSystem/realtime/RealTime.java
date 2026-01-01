package net.letstim.hubSystem.realtime;

import net.letstim.hubSystem.HubSystem;
import net.letstim.hubSystem.commands.DebugEventSubcommand;
import net.letstim.hubSystem.config.RealTimeConfig;
import net.letstim.hubSystem.realtime.events.EventManager;
import net.letstim.hubSystem.realtime.events.NewYearFireworkEvent;
import org.bukkit.command.PluginCommand;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class RealTime {
    private static RealTime instance;

    private final HubSystem plugin;
    private final RealTimeConfig config;
    private final EventManager eventManager;
    private WorldTimeSync worldTimeSync;
    private BukkitTask eventCheckTask;

    public RealTime() {
        instance = this;
        this.plugin = HubSystem.getInstance();
        this.config = new RealTimeConfig(plugin);
        this.eventManager = new EventManager(plugin);

        registerEvents();
        registerCommands();
        startSync();
        startEventScheduler();
    }

    private void registerEvents() {
        // Register New Year Firework Event
        NewYearFireworkEvent newYearEvent = new NewYearFireworkEvent(plugin, config);
        eventManager.registerEvent(newYearEvent);

        plugin.getLogger().info("Registered " + eventManager.getEventNames().size() + " events");
    }

    private void registerCommands() {
        PluginCommand timezoneCmd = plugin.getCommand("timezone");
        if (timezoneCmd == null) {
            plugin.getLogger().severe("Command 'timezone' not found in plugin.yml!");
        } else {
            TimezoneCommand executor = new TimezoneCommand(this);
            timezoneCmd.setExecutor(executor);
            timezoneCmd.setTabCompleter(executor);
        }

        // Register event subcommand with central debug command
        DebugEventSubcommand eventSubcommand = new DebugEventSubcommand(this);
        plugin.getDebugCommand().registerSubcommand(eventSubcommand);
        plugin.getLogger().info("Registered 'event' debug subcommand");
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

    private void startEventScheduler() {
        stopEventScheduler();

        // Check every 60 seconds if events should be started or stopped
        eventCheckTask = new BukkitRunnable() {
            @Override
            public void run() {
                eventManager.checkSchedules();
            }
        }.runTaskTimer(plugin, 0L, 60 * 20L); // Every 60 seconds

        plugin.getLogger().info("Event scheduler started");
    }

    private void stopEventScheduler() {
        if (eventCheckTask != null) {
            eventCheckTask.cancel();
            eventCheckTask = null;
        }
    }

    public void restartSync() {
        stopSync();
        startSync();
    }

    public void disable() {
        stopSync();
        stopEventScheduler();
        eventManager.stopAll();
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

    public EventManager getEventManager() {
        return eventManager;
    }
}
