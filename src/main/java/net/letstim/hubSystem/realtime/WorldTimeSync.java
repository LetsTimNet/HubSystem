package net.letstim.hubSystem.realtime;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class WorldTimeSync extends BukkitRunnable {
    private final RealTime realTime;
    private boolean firstRun = true;

    public WorldTimeSync(RealTime realTime) {
        this.realTime = realTime;
    }

    @Override
    public void run() {
        for (World world : Bukkit.getWorlds()) {
            syncWorld(world);
        }

        if (firstRun) {
            firstRun = false;
        }
    }

    @SuppressWarnings("removal")
    private void syncWorld(World world) {
        if (firstRun) {
            org.bukkit.GameRule<Boolean> daylightCycle = org.bukkit.GameRule.DO_DAYLIGHT_CYCLE;
            world.setGameRule(daylightCycle, false);
        }

        long minecraftTime = calculateMinecraftTime();
        world.setTime(minecraftTime);
    }

    private long calculateMinecraftTime() {
        ZoneId timezone = ZoneId.of(realTime.getConfig().getTimezone());
        ZonedDateTime now = ZonedDateTime.now(timezone);

        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();

        SolarCalculator.SunTimes sunTimes = SolarCalculator.calculateSunTimes(timezone);
        double sunriseTime = sunTimes.getSunriseDecimal();
        double sunsetTime = sunTimes.getSunsetDecimal();
        double currentTime = hour + (minute / 60.0) + (second / 3600.0);

        boolean isDay = currentTime >= sunriseTime && currentTime <= sunsetTime;

        if (isDay) {
            double dayLength = sunsetTime - sunriseTime;
            double progress = currentTime - sunriseTime;
            double percentage = progress / dayLength;
            return (long) (percentage * 12000);
        }

        boolean isAfterSunset = currentTime > sunsetTime;
        double nightLength = (24.0 - sunsetTime) + sunriseTime;
        double progress = isAfterSunset ? currentTime - sunsetTime : (24.0 - sunsetTime) + currentTime;
        double percentage = progress / nightLength;
        return 12000 + (long) (percentage * 12000);
    }
}
