package com.einherji.rs2world.engine;

import com.einherji.rs2world.RS2World;

public class EventFuture {

    private final Event event;
    private boolean cancelled;
    private long scheduledExecutionTime;

    public EventFuture(Event event, long scheduledExecutionTime) {
        this.event = event;
        this.cancelled = false;
        this.scheduledExecutionTime = scheduledExecutionTime;
    }

    public void cancel() {
        this.cancelled = true;
    }

    public Event getEvent() {
        return event;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    void setScheduledExecutionTime(long scheduledExecutionTime) {
        this.scheduledExecutionTime = scheduledExecutionTime;
    }

    public long getDelay() {
        return scheduledExecutionTime - RS2World.currentGameTick();
    }

}
