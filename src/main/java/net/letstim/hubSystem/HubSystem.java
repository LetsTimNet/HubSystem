package net.letstim.hubSystem;

import net.letstim.hubSystem.commands.DebugCommand;
import net.letstim.hubSystem.realtime.RealTime;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;


public final class HubSystem extends JavaPlugin {
    private static HubSystem instance;
    private RealTime realTime;
    private DebugCommand debugCommand;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize debug command
        debugCommand = new DebugCommand();
        registerDebugCommand();

        // Initialize RealTime system
        realTime = new RealTime();
    }

    private void registerDebugCommand() {
        PluginCommand cmd = getCommand("hubdebug");
        if (cmd == null) {
            getLogger().severe("Command 'debug' not found in plugin.yml!");
        } else {
            cmd.setExecutor(debugCommand);
            cmd.setTabCompleter(debugCommand);
            getLogger().info("Debug command registered successfully");
        }
    }

    @Override
    public void onDisable() {
        realTime.disable();
    }

    public static HubSystem getInstance() { return instance; }
    public RealTime getRealTime() { return realTime; }
    public DebugCommand getDebugCommand() { return debugCommand; }
}


