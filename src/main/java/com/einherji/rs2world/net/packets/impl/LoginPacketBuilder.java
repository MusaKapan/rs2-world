package com.einherji.rs2world.net.packets.impl;

import com.einherji.rs2world.net.clients.Client;
import com.einherji.rs2world.net.packets.Packet;
import com.einherji.rs2world.net.packets.PacketBuilder;
import com.einherji.rs2world.net.packets.PacketContext;
import com.einherji.rs2world.net.util.Rs2ReadBuffer;

public class LoginPacketBuilder implements PacketBuilder {

    @Override
    public Packet build(Rs2ReadBuffer buffer) {
        return null;
    }

    private static class LoginPacket implements Packet {

        @Override
        public void execute(Client client, PacketContext ctx) {

        }
    }
}
