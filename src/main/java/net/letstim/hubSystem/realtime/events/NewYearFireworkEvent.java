package net.letstim.hubSystem.realtime.events;

import net.letstim.hubSystem.config.RealTimeConfig;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Random;

/**
 * New Year Firework Event
 * Spawns fireworks on New Year's Eve at midnight
 */
public class NewYearFireworkEvent implements LiveEvent {
    private final Plugin plugin;
    private final RealTimeConfig config;
    private final Random random;
    private BukkitTask fireworkTask;
    private boolean isActive = false;

    public NewYearFireworkEvent(Plugin plugin, RealTimeConfig config) {
        this.plugin = plugin;
        this.config = config;
        this.random = new Random();
    }

    @Override
    public void start() {
        if (isActive) {
            plugin.getLogger().warning("NewYear Firework is already active!");
            return;
        }

        isActive = true;
        plugin.getLogger().info("Silvester Feuerwerk aktiviert! Frohes neues Jahr!");

        int intervalTicks = config.getFireworkSpawnIntervalSeconds() * 20;

        fireworkTask = new BukkitRunnable() {
            @Override
            public void run() {
                spawnFireworks();
            }
        }.runTaskTimer(plugin, 0L, intervalTicks);
    }

    @Override
    public void stop() {
        if (!isActive) {
            return;
        }

        isActive = false;
        if (fireworkTask != null) {
            fireworkTask.cancel();
            fireworkTask = null;
        }
        plugin.getLogger().info("Silvester Feuerwerk beendet.");
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public String getEventName() {
        return "NewYearFirework";
    }

    @Override
    public boolean shouldBeActive() {
        if (!config.isNewYearFireworkEnabled()) {
            return false;
        }

        ZoneId timezone = ZoneId.of(config.getTimezone());
        ZonedDateTime now = ZonedDateTime.now(timezone);

        // Active on December 31st at midnight OR January 1st at midnight (0:00 to 1:00)
        boolean isNewYearHour = (now.getMonthValue() == 12 && now.getDayOfMonth() == 31) ||
                                (now.getMonthValue() == 1 && now.getDayOfMonth() == 1 && now.getHour() == 0);

        return isNewYearHour && now.getHour() == 0;
    }

    private void spawnFireworks() {
        String worldName = config.getFireworkWorld();
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            plugin.getLogger().warning("Welt '" + worldName + "' nicht gefunden!");
            return;
        }

        double centerX = config.getFireworkCenterX();
        double centerY = config.getFireworkCenterY();
        double centerZ = config.getFireworkCenterZ();
        int radius = config.getFireworkRadius();

        // Spawn 10-30 fireworks randomly in the radius
        int fireworkCount = 10 + random.nextInt(21);

        for (int i = 0; i < fireworkCount; i++) {
            // Random position within radius
            double angle = random.nextDouble() * 2 * Math.PI;
            double distance = random.nextDouble() * radius;

            double x = centerX + Math.cos(angle) * distance;
            double z = centerZ + Math.sin(angle) * distance;
            double y = centerY + random.nextInt(20) - 10; // ±10 blocks vertical variation

            Location loc = new Location(world, x, y, z);

            // Spawn firework
            Firework firework = world.spawn(loc, Firework.class);
            FireworkMeta meta = firework.getFireworkMeta();

            // Random firework effect
            FireworkEffect effect = createRandomEffect();
            meta.addEffect(effect);
            meta.setPower(random.nextInt(2) + 1); // Power 1-2

            firework.setFireworkMeta(meta);
        }
    }

    private FireworkEffect createRandomEffect() {
        FireworkEffect.Type[] types = FireworkEffect.Type.values();
        FireworkEffect.Type type = types[random.nextInt(types.length)];

        Color color1 = getRandomColor();
        Color color2 = getRandomColor();
        Color fadeColor = getRandomColor();

        return FireworkEffect.builder()
                .with(type)
                .withColor(color1, color2)
                .withFade(fadeColor)
                .flicker(random.nextBoolean())
                .trail(random.nextBoolean())
                .build();
    }

    private Color getRandomColor() {
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return Color.fromRGB(r, g, b);
    }
}

