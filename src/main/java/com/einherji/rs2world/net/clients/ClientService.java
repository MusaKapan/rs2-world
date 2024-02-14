package com.einherji.rs2world.net.clients;

import com.einherji.rs2world.net.packets.PacketContext;
import com.einherji.rs2world.net.util.Rs2Buffer;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class ClientService {

    private final Map<UUID, Client> clientUuidMap;
    private final PacketContext packetContext;

    public ClientService(PacketContext packetContext) {
        this.packetContext = packetContext;
        clientUuidMap = new HashMap<>();
    }

    public Client create(SelectionKey key, SocketChannel channel, UUID uuid) {
        //TODO: pool the creation of clients and/or their internal buffers
        Client client = new Client(uuid, channel, key, Rs2Buffer.createWriteBuffer(ByteBuffer.allocate(2048)));
        synchronized (clientUuidMap) {
            clientUuidMap.put(client.getUuid(), client);
        }
        return client;

    }

    public Client get(UUID uuid) {
        return clientUuidMap.get(uuid);
    }

    public void executeQueuedPackets() {
        clientUuidMap.values().forEach(client -> client.triggerQueuedPackets(packetContext));
    }

}
