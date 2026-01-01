package net.letstim.hubSystem.realtime.events;

import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manager for all live events
 * Handles registration, starting, stopping and scheduling of events
 */
public class EventManager {
    private final Plugin plugin;
    private final Map<String, LiveEvent> events;

    public EventManager(Plugin plugin) {
        this.plugin = plugin;
        this.events = new HashMap<>();
    }

    /**
     * Register a new event
     */
    public void registerEvent(LiveEvent event) {
        events.put(event.getEventName().toLowerCase(), event);
        plugin.getLogger().info("Event registered: " + event.getEventName());
    }

    /**
     * Unregister an event
     */
    public void unregisterEvent(String eventName) {
        LiveEvent event = events.remove(eventName.toLowerCase());
        if (event != null && event.isActive()) {
            event.stop();
        }
    }

    /**
     * Start all registered events
     */
    public void startAll() {
        for (LiveEvent event : events.values()) {
            event.start();
        }
    }

    /**
     * Stop all registered events
     */
    public void stopAll() {
        for (LiveEvent event : events.values()) {
            if (event.isActive()) {
                event.stop();
            }
        }
    }

    /**
     * Check all events and activate/deactivate them based on their schedule
     */
    public void checkSchedules() {
        for (LiveEvent event : events.values()) {
            boolean shouldBeActive = event.shouldBeActive();
            boolean isActive = event.isActive();

            if (shouldBeActive && !isActive) {
                plugin.getLogger().info("Activating scheduled event: " + event.getEventName());
                event.start();
            } else if (!shouldBeActive && isActive) {
                plugin.getLogger().info("Deactivating scheduled event: " + event.getEventName());
                event.stop();
            }
        }
    }

    /**
     * Get an event by name
     */
    public Optional<LiveEvent> getEvent(String eventName) {
        return Optional.ofNullable(events.get(eventName.toLowerCase()));
    }

    /**
     * Get all registered event names
     */
    public List<String> getEventNames() {
        return new ArrayList<>(events.keySet());
    }

    /**
     * Get all active events
     */
    public List<LiveEvent> getActiveEvents() {
        return events.values().stream()
                .filter(LiveEvent::isActive)
                .collect(Collectors.toList());
    }
}

