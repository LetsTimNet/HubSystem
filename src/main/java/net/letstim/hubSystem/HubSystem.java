package net.letstim.hubSystem;

import net.letstim.hubSystem.realtime.RealTime;
import org.bukkit.plugin.java.JavaPlugin;


public final class HubSystem extends JavaPlugin {
    private static HubSystem instance;
    private RealTime realTime;

    @Override
    public void onEnable() {
        instance = this;
        realTime = new RealTime();
    }

    @Override
    public void onDisable() {
        realTime.disable();
    }

    public static HubSystem getInstance() { return instance; }
    public RealTime getRealTime() { return realTime; }
}
