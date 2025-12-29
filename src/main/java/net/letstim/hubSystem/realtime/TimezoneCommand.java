package net.letstim.hubSystem.realtime;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class TimezoneCommand implements CommandExecutor, TabCompleter {
    private final RealTime realTime;

    public TimezoneCommand(RealTime realTime) {
        this.realTime = realTime;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            showCurrentTimezone(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("reload")) {
            if (!sender.hasPermission("hubsystem.timezone.admin")) {
                sender.sendMessage(Component.text("Keine Berechtigung.", NamedTextColor.RED));
                return true;
            }
            reloadConfig(sender);
            return true;
        }

        setTimezone(sender, args[0]);
        return true;
    }

    private void showCurrentTimezone(CommandSender sender) {
        String timezone = realTime.getConfig().getTimezone();

        java.time.ZonedDateTime now = java.time.ZonedDateTime.now(ZoneId.of(timezone));
        String time = String.format("%02d:%02d:%02d", now.getHour(), now.getMinute(), now.getSecond());

        sender.sendMessage(Component.text()
                .append(Component.text("Aktuelle Zeitzone: ", NamedTextColor.GRAY))
                .append(Component.text(timezone, NamedTextColor.YELLOW))
                .build());
        sender.sendMessage(Component.text()
                .append(Component.text("Uhrzeit: ", NamedTextColor.GRAY))
                .append(Component.text(time, NamedTextColor.YELLOW))
                .build());
    }

    private void setTimezone(CommandSender sender, String timezoneInput) {
        if (!sender.hasPermission("hubsystem.timezone.admin")) {
            sender.sendMessage(Component.text("Keine Berechtigung.", NamedTextColor.RED));
            return;
        }

        try {
            ZoneId.of(timezoneInput);
        } catch (Exception e) {
            sender.sendMessage(Component.text()
                    .append(Component.text("Ungueltige Zeitzone: ", NamedTextColor.RED))
                    .append(Component.text(timezoneInput, NamedTextColor.YELLOW))
                    .build());
            return;
        }

        realTime.getConfig().setTimezone(timezoneInput);
        realTime.restartSync();

        sender.sendMessage(Component.text()
                .append(Component.text("Zeitzone geaendert zu: ", NamedTextColor.GREEN))
                .append(Component.text(timezoneInput, NamedTextColor.YELLOW))
                .build());
    }

    private void reloadConfig(CommandSender sender) {
        realTime.getConfig().load();
        realTime.restartSync();

        sender.sendMessage(Component.text()
                .append(Component.text("RealTime Config neu geladen.", NamedTextColor.GREEN))
                .build());
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            return new ArrayList<>();
        }

        List<String> completions = new ArrayList<>();

        if (sender.hasPermission("hubsystem.timezone.admin")) {
            completions.add("reload");

            // Häufige Zeitzonen
            completions.add("Europe/Berlin");
            completions.add("Europe/London");
            completions.add("America/New_York");
            completions.add("America/Los_Angeles");
            completions.add("Asia/Tokyo");
        }

        String input = args[0].toLowerCase();
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(input))
                .toList();
    }
}
