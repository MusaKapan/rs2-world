package com.einherji.rs2world.net.packets;

import com.einherji.rs2world.net.util.Rs2ReadBuffer;

public interface PacketBuilder {

    Packet build(Rs2ReadBuffer buffer);

}
