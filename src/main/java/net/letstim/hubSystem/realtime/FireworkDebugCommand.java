package net.letstim.hubSystem.realtime;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class FireworkDebugCommand implements CommandExecutor {
    private final RealTime realTime;

    public FireworkDebugCommand(RealTime realTime) {
        this.realTime = realTime;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("hubsystem.firework.debug")) {
            sender.sendMessage(Component.text("Keine Berechtigung.", NamedTextColor.RED));
            return true;
        }

        realTime.startFireworkDebug();
        sender.sendMessage(Component.text("Silvester-Feuerwerk wurde fuer 1 Minute gestartet!", NamedTextColor.GREEN));

        return true;
    }
}

