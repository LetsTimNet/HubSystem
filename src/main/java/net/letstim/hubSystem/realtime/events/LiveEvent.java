package net.letstim.hubSystem.realtime.events;

/**
 * Interface for all live events that can be started and stopped
 */
public interface LiveEvent {
    /**
     * Start the event
     */
    void start();

    /**
     * Stop the event
     */
    void stop();

    /**
     * Check if the event is currently active
     * @return true if the event is running
     */
    boolean isActive();

    /**
     * Get the name of the event
     * @return the event name
     */
    String getEventName();

    /**
     * Check if this event should be active at the current time
     * @return true if the event should be active now
     */
    boolean shouldBeActive();
}

