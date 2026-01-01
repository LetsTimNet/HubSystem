package net.letstim.hubSystem.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.letstim.hubSystem.realtime.RealTime;
import net.letstim.hubSystem.realtime.events.EventManager;
import net.letstim.hubSystem.realtime.events.LiveEvent;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Debug subcommand for managing live events
 * Usage: /debug event <start|stop|status> <eventName>
 */
public class DebugEventSubcommand implements DebugSubcommand {
    private final RealTime realTime;

    public DebugEventSubcommand(RealTime realTime) {
        this.realTime = realTime;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sendUsage(sender);
            return true;
        }

        String action = args[0].toLowerCase();
        String eventName = args[1];

        EventManager eventManager = realTime.getEventManager();
        Optional<LiveEvent> eventOpt = eventManager.getEvent(eventName);

        if (eventOpt.isEmpty()) {
            sender.sendMessage(Component.text()
                    .append(Component.text("Event nicht gefunden: ", NamedTextColor.RED))
                    .append(Component.text(eventName, NamedTextColor.YELLOW))
                    .build());
            sender.sendMessage(Component.text()
                    .append(Component.text("Verfügbare Events: ", NamedTextColor.GRAY))
                    .append(Component.text(String.join(", ", eventManager.getEventNames()), NamedTextColor.YELLOW))
                    .build());
            return true;
        }

        LiveEvent event = eventOpt.get();

        switch (action) {
            case "start" -> handleStart(sender, event);
            case "stop" -> handleStop(sender, event);
            case "status" -> handleStatus(sender, event);
            default -> sendUsage(sender);
        }

        return true;
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("start", "stop", "status"));
        } else if (args.length == 2) {
            completions.addAll(realTime.getEventManager().getEventNames());
        }

        return completions;
    }

    @Override
    public String getName() {
        return "event";
    }

    @Override
    public String getPermission() {
        return "hubsystem.debug.event";
    }

    @Override
    public String getUsage() {
        return "<start|stop|status> <eventName> - Event verwalten";
    }

    private void handleStart(CommandSender sender, LiveEvent event) {
        if (event.isActive()) {
            sender.sendMessage(Component.text()
                    .append(Component.text("Event ", NamedTextColor.YELLOW))
                    .append(Component.text(event.getEventName(), NamedTextColor.GOLD))
                    .append(Component.text(" läuft bereits!", NamedTextColor.YELLOW))
                    .build());
            return;
        }

        event.start();
        sender.sendMessage(Component.text()
                .append(Component.text("Event ", NamedTextColor.GREEN))
                .append(Component.text(event.getEventName(), NamedTextColor.GOLD))
                .append(Component.text(" gestartet!", NamedTextColor.GREEN))
                .build());
    }

    private void handleStop(CommandSender sender, LiveEvent event) {
        if (!event.isActive()) {
            sender.sendMessage(Component.text()
                    .append(Component.text("Event ", NamedTextColor.YELLOW))
                    .append(Component.text(event.getEventName(), NamedTextColor.GOLD))
                    .append(Component.text(" läuft nicht!", NamedTextColor.YELLOW))
                    .build());
            return;
        }

        event.stop();
        sender.sendMessage(Component.text()
                .append(Component.text("Event ", NamedTextColor.GREEN))
                .append(Component.text(event.getEventName(), NamedTextColor.GOLD))
                .append(Component.text(" gestoppt!", NamedTextColor.GREEN))
                .build());
    }

    private void handleStatus(CommandSender sender, LiveEvent event) {
        boolean isActive = event.isActive();
        boolean shouldBeActive = event.shouldBeActive();

        sender.sendMessage(Component.text()
                .append(Component.text("Event: ", NamedTextColor.GRAY))
                .append(Component.text(event.getEventName(), NamedTextColor.GOLD))
                .build());

        sender.sendMessage(Component.text()
                .append(Component.text("Status: ", NamedTextColor.GRAY))
                .append(Component.text(isActive ? "AKTIV" : "INAKTIV",
                        isActive ? NamedTextColor.GREEN : NamedTextColor.RED))
                .build());

        sender.sendMessage(Component.text()
                .append(Component.text("Sollte aktiv sein: ", NamedTextColor.GRAY))
                .append(Component.text(shouldBeActive ? "JA" : "NEIN",
                        shouldBeActive ? NamedTextColor.GREEN : NamedTextColor.RED))
                .build());
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(Component.text("Usage: /debug event <start|stop|status> <eventName>", NamedTextColor.YELLOW));
    }
}

