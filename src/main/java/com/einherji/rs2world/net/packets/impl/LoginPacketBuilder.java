package com.einherji.rs2world.net.packets.impl;

import com.einherji.rs2world.net.clients.Client;
import com.einherji.rs2world.net.packets.Packet;
import com.einherji.rs2world.net.packets.PacketBuilder;
import com.einherji.rs2world.net.packets.PacketContext;
import com.einherji.rs2world.net.util.Rs2ReadBuffer;

public class LoginPacketBuilder implements PacketBuilder {

    @Override
    public Packet build(Rs2ReadBuffer buffer) {
        if (buffer.getBuffer().remaining() < 2) {
            return null;
        }
        int loginType = buffer.readByte(); //16 or 18
        int packetLength = buffer.readByte(false);
        if (buffer.getBuffer().remaining() < packetLength) {
            return null;
        }
        buffer.readByte();
        int clientVersion = buffer.readShort();
        buffer.readByte();
        for (int i = 0; i < 9; i++) buffer.readInt();
        buffer.readByte();
        buffer.readByte();
        long clientSessionKey = buffer.readLong();
        long serverSessionKey = buffer.readLong();
        String username = buffer.readString();
        String password = buffer.readString();
        return new LoginPacket(loginType, clientVersion, clientSessionKey, serverSessionKey, username, password);
    }

    public record LoginPacket(int loginType,
                              int clientVersion,
                              long clientSessionKey,
                              long serverSessionKey,
                              String username,
                              String password)
            implements Packet, Runnable {

        @Override
        public void execute(Client client, PacketContext ctx) {
            ctx.registerLoginRequest(this);

        }

        @Override
        public void run() {

        }
    }
}
