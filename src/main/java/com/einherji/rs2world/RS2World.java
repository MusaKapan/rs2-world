package com.einherji.rs2world;

import com.einherji.rs2world.engine.Engine;
import com.einherji.rs2world.net.Server;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
public class RS2World implements CommandLineRunner {

    private static Engine engine;
    private static Server server;
    private static ScheduledExecutorService executorService;
    private static ScheduledFuture serverFuture;
    private static ScheduledFuture engineFuture;

    public RS2World(Server serverRef,
                    Engine engineRef) {
        server = serverRef;
        engine = engineRef;
    }

    @Override
    public void run(String... args) throws Exception {
        executorService = Executors.newScheduledThreadPool(3);
        serverFuture = executorService.scheduleAtFixedRate(server, 0, 100, TimeUnit.MILLISECONDS);
        engineFuture = executorService.scheduleAtFixedRate(engine, 0, 600, TimeUnit.MILLISECONDS);
    }

    public static long currentGameTick() {
        return engine.getCurrentTick();
    }
}
