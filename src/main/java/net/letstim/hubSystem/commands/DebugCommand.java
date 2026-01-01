package net.letstim.hubSystem.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Central debug command for the entire plugin
 * Supports modular subcommands
 */
public class DebugCommand implements CommandExecutor, TabCompleter {
    private final Map<String, DebugSubcommand> subcommands;

    public DebugCommand() {
        this.subcommands = new HashMap<>();
    }

    /**
     * Register a new debug subcommand
     */
    public void registerSubcommand(DebugSubcommand subcommand) {
        subcommands.put(subcommand.getName().toLowerCase(), subcommand);
    }

    /**
     * Unregister a debug subcommand
     */
    public void unregisterSubcommand(String name) {
        subcommands.remove(name.toLowerCase());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subcommandName = args[0].toLowerCase();
        DebugSubcommand subcommand = subcommands.get(subcommandName);

        if (subcommand == null) {
            sender.sendMessage(Component.text()
                    .append(Component.text("Unbekannter Debug-Befehl: ", NamedTextColor.RED))
                    .append(Component.text(subcommandName, NamedTextColor.YELLOW))
                    .build());
            sendHelp(sender);
            return true;
        }

        // Check permission
        if (!sender.hasPermission(subcommand.getPermission())) {
            sender.sendMessage(Component.text("Keine Berechtigung!", NamedTextColor.RED));
            return true;
        }

        // Execute subcommand with remaining args
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        return subcommand.execute(sender, subArgs);
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Component.text("=== Debug Commands ===", NamedTextColor.GOLD));

        if (subcommands.isEmpty()) {
            sender.sendMessage(Component.text("Keine Debug-Commands verfügbar.", NamedTextColor.GRAY));
            return;
        }

        for (DebugSubcommand subcommand : subcommands.values()) {
            if (sender.hasPermission(subcommand.getPermission())) {
                sender.sendMessage(Component.text()
                        .append(Component.text("/debug " + subcommand.getName(), NamedTextColor.YELLOW))
                        .append(Component.text(" - ", NamedTextColor.GRAY))
                        .append(Component.text(subcommand.getUsage(), NamedTextColor.WHITE))
                        .build());
            }
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Tab complete subcommand names
            completions.addAll(subcommands.values().stream()
                    .filter(sub -> sender.hasPermission(sub.getPermission()))
                    .map(DebugSubcommand::getName)
                    .toList());
        } else if (args.length > 1) {
            // Tab complete for specific subcommand
            String subcommandName = args[0].toLowerCase();
            DebugSubcommand subcommand = subcommands.get(subcommandName);

            if (subcommand != null && sender.hasPermission(subcommand.getPermission())) {
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                completions.addAll(subcommand.getTabCompletions(sender, subArgs));
            }
        }

        return completions;
    }
}

