package com.einherji.rs2world.engine;

import com.einherji.rs2world.net.clients.ClientService;
import org.springframework.stereotype.Component;

@Component
public class Engine implements Runnable {

    private final EventService eventService;
    private final ClientService clientService;
    private long currentTick;

    public Engine(EventService eventService, ClientService clientService) {
        this.eventService = eventService;
        this.clientService = clientService;
        this.currentTick = 0L;
    }

    @Override
    public void run() {
        currentTick++;
        eventService.executeScheduledEvents();
        clientService.executeQueuedPackets();
    }

    public long getCurrentTick() {
        return currentTick;
    }

}
