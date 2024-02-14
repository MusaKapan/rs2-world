package com.einherji.rs2world.net.clients;

import com.einherji.rs2world.net.packets.Packet;
import com.einherji.rs2world.net.packets.PacketContext;
import com.einherji.rs2world.net.util.Rs2WriteBuffer;
import com.einherji.rs2world.util.Timer;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Client {

    private final UUID uuid;
    private final SocketChannel channel;
    private final SelectionKey selectionKey;
    private final Rs2WriteBuffer outBuffer;
    private final Timer timeoutTimer = new Timer();
    private final List<Packet> packetQueue = new ArrayList<>();

    private ClientStatus status;

    public Client(UUID uuid,
                  SocketChannel channel,
                  SelectionKey selectionKey,
                  Rs2WriteBuffer outBuffer) {
        this.uuid = uuid;
        this.channel = channel;
        this.selectionKey = selectionKey;
        this.outBuffer = outBuffer;
        this.status = ClientStatus.CONNECTED;
    }

    public void queuePacket(Packet packet) {
        Objects.requireNonNull(packet);
        synchronized (packetQueue) {
            packetQueue.add(packet);
        }
    }

    public void triggerQueuedPackets(PacketContext ctx) {
        synchronized (packetQueue) {
            packetQueue.forEach(packet -> packet.execute(this, ctx));
            packetQueue.clear();
        }
    }

    public void flushOutBuffer() {
        try {
            outBuffer.getBuffer().flip();
            channel.write(outBuffer.getBuffer());
        } catch(IOException ioe) {

        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public SelectionKey getSelectionKey() {
        return selectionKey;
    }

    public Rs2WriteBuffer getOutBuffer() {
        return outBuffer;
    }

    public ClientStatus getStatus() {
        return status;
    }

    public void setStatus(ClientStatus status) {
        this.status = status;
    }

    public Timer getTimeoutTimer() {
        return timeoutTimer;
    }
}
