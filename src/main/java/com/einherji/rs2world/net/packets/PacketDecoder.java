package com.einherji.rs2world.net.packets;

import com.einherji.rs2world.net.clients.ClientStatus;
import com.einherji.rs2world.net.packets.impl.ConnectionPacketBuilder;
import com.einherji.rs2world.net.packets.impl.LoginPacketBuilder;
import com.einherji.rs2world.net.util.Rs2ReadBuffer;
import org.springframework.stereotype.Component;

@Component
public class PacketDecoder {

    private final ConnectionPacketBuilder connectionPacketBuilder = new ConnectionPacketBuilder();
    private final LoginPacketBuilder loginPacketBuilder = new LoginPacketBuilder();

    public Packet decode(ClientStatus status, Rs2ReadBuffer buffer) {
        return switch (status) {
            case CONNECTED -> connectionPacketBuilder.build(buffer);
            case LOGGING_IN -> loginPacketBuilder.build(buffer);
            case LOGGED_IN -> null;
        };
    }

}
