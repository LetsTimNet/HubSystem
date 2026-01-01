package net.letstim.hubSystem.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Interface for debug subcommands
 */
public interface DebugSubcommand {
    /**
     * Execute the subcommand
     * @param sender Command sender
     * @param args Arguments (without the subcommand name)
     * @return true if successful
     */
    boolean execute(CommandSender sender, String[] args);

    /**
     * Get tab completions for this subcommand
     * @param sender Command sender
     * @param args Arguments (without the subcommand name)
     * @return List of completions
     */
    List<String> getTabCompletions(CommandSender sender, String[] args);

    /**
     * Get the name of this subcommand
     * @return subcommand name (e.g. "event", "config", etc.)
     */
    String getName();

    /**
     * Get the permission required for this subcommand
     * @return permission node
     */
    String getPermission();

    /**
     * Get the usage/help text for this subcommand
     * @return usage text
     */
    String getUsage();
}

