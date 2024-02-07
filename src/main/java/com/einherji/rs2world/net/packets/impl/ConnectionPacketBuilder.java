package com.einherji.rs2world.net.packets.impl;

import com.einherji.rs2world.net.clients.Client;
import com.einherji.rs2world.net.clients.ClientStatus;
import com.einherji.rs2world.net.packets.Packet;
import com.einherji.rs2world.net.packets.PacketBuilder;
import com.einherji.rs2world.net.packets.PacketContext;
import com.einherji.rs2world.net.util.Rs2ReadBuffer;

public class ConnectionPacketBuilder implements PacketBuilder {

    @Override
    public Packet build(Rs2ReadBuffer buffer) {
        if (buffer.getBuffer().remaining() < 2) return null;
        int requestType = buffer.readByte(false);
        return new ConnectionPacket(requestType);
    }

    private record ConnectionPacket(int requestType) implements Packet {

        @Override
        public void execute(Client client, PacketContext ctx) {

            if (requestType != 14) {
                //TODO: properly terminate client
            }
            client.getOutBuffer().writeLong(0L);
            client.getOutBuffer().writeByte(0);
            client.getOutBuffer().writeLong(ctx.generateServerSessionKey());
            client.setStatus(ClientStatus.LOGGING_IN);
            client.flushOutBuffer();
        }
    }
}
