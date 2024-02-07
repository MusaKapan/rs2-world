package com.einherji.rs2world.net.packets;

import com.einherji.rs2world.net.gateway.Gateway;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class PacketContext {

    private final Gateway gateway;
    private final SecureRandom randomGenerator = new SecureRandom();

    public PacketContext(Gateway gateway) {
        this.gateway = gateway;
    }

    public long generateServerSessionKey() {
        return randomGenerator.nextLong();
    }
}
