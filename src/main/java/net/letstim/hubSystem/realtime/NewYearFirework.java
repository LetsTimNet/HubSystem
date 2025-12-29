package net.letstim.hubSystem.realtime;

import net.letstim.hubSystem.config.RealTimeConfig;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Random;

public class NewYearFirework {
    private final RealTime realTime;
    private final Random random;
    private BukkitTask checkTask;
    private BukkitTask fireworkTask;
    private boolean isActive = false;
    private boolean debugMode = false;
    private BukkitTask debugStopTask;

    public NewYearFirework(RealTime realTime) {
        this.realTime = realTime;
        this.random = new Random();
    }

    public void start() {
        // Check every second if it's New Year's Eve at midnight
        checkTask = new BukkitRunnable() {
            @Override
            public void run() {
                checkAndActivate();
            }
        }.runTaskTimer(realTime.getPlugin(), 0L, 20L); // Check every second
    }

    public void stop() {
        if (checkTask != null) {
            checkTask.cancel();
            checkTask = null;
        }
        stopDebug();
        stopFireworks();
    }

    private void checkAndActivate() {
        // Skip automatic check if in debug mode
        if (debugMode) {
            return;
        }

        RealTimeConfig config = realTime.getConfig();

        if (!config.isNewYearFireworkEnabled()) {
            if (isActive) {
                stopFireworks();
            }
            return;
        }

        ZoneId timezone = ZoneId.of(config.getTimezone());
        ZonedDateTime now = ZonedDateTime.now(timezone);

        // Check if it's December 31st at midnight (0:00 to 1:00)
        boolean isNewYearHour = (now.getMonthValue() == 12 && now.getDayOfMonth() == 31) ||
                                (now.getMonthValue() == 1 && now.getDayOfMonth() == 1 && now.getHour() == 0);

        if (isNewYearHour && now.getHour() == 0) {
            if (!isActive) {
                startFireworks();
            }
        } else {
            if (isActive) {
                stopFireworks();
            }
        }
    }

    private void startFireworks() {
        isActive = true;
        realTime.getPlugin().getLogger().info("Silvester Feuerwerk aktiviert! Frohes neues Jahr!");

        RealTimeConfig config = realTime.getConfig();
        int intervalTicks = config.getFireworkSpawnIntervalSeconds() * 20;

        fireworkTask = new BukkitRunnable() {
            @Override
            public void run() {
                spawnFireworks();
            }
        }.runTaskTimer(realTime.getPlugin(), 0L, intervalTicks);
    }

    private void stopFireworks() {
        isActive = false;
        if (fireworkTask != null) {
            fireworkTask.cancel();
            fireworkTask = null;
            realTime.getPlugin().getLogger().info("Silvester Feuerwerk beendet.");
        }
    }

    public void startDebug() {
        if (isActive) {
            realTime.getPlugin().getLogger().warning("Feuerwerk laeuft bereits!");
            return;
        }

        debugMode = true;
        startFireworks();
        realTime.getPlugin().getLogger().info("Debug-Modus: Feuerwerk fuer 1 Minute gestartet.");

        // Stop after 1 minute
        debugStopTask = new BukkitRunnable() {
            @Override
            public void run() {
                stopDebug();
            }
        }.runTaskLater(realTime.getPlugin(), 20L * 60); // 60 seconds = 1 minute
    }

    private void stopDebug() {
        if (debugStopTask != null) {
            debugStopTask.cancel();
            debugStopTask = null;
        }
        if (debugMode) {
            debugMode = false;
            stopFireworks();
            realTime.getPlugin().getLogger().info("Debug-Modus beendet.");
        }
    }

    private void spawnFireworks() {
        RealTimeConfig config = realTime.getConfig();

        String worldName = config.getFireworkWorld();
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            realTime.getPlugin().getLogger().warning("Welt '" + worldName + "' nicht gefunden!");
            return;
        }

        double centerX = config.getFireworkCenterX();
        double centerY = config.getFireworkCenterY();
        double centerZ = config.getFireworkCenterZ();
        int radius = config.getFireworkRadius();

        // Spawn 5-10 fireworks randomly in the radius
        int fireworkCount = 10 + random.nextInt(21); // 10 to 30

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

    public boolean isActive() {
        return isActive;
    }
}

