package com.einherji.rs2world.engine;

import com.einherji.rs2world.RS2World;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventService {

    private final Map<Long, List<EventFuture>> eventMap;

    public EventService() {
        this.eventMap = new HashMap<>();
    }

    public EventFuture schedule(int delay, Event event) {
        Assert.isTrue(delay > 0, "Attempted to schedule an event with negative delay!");
        long targetTick = RS2World.currentGameTick() + delay;
        List<EventFuture> eventList = eventMap.computeIfAbsent(targetTick, k -> new ArrayList<>());
        EventFuture future = new EventFuture(event, targetTick);
        eventList.add(future);
        return future;
    }

    void executeScheduledEvents() {
        List<EventFuture> eventList = eventMap.get(RS2World.currentGameTick());
        if (eventList == null) return;
        eventList.forEach(ft -> {
            if (!ft.isCancelled()) {
                int delay = ft.getEvent().execute();
                if (delay > 0) {
                    long targetTick = RS2World.currentGameTick() + delay;
                    ft.setScheduledExecutionTime(targetTick);
                    List<EventFuture> targetEventList = eventMap.computeIfAbsent(targetTick, k -> new ArrayList<>());
                    targetEventList.add(ft);
                }
            }
        });
        eventMap.remove(RS2World.currentGameTick());
    }

}
