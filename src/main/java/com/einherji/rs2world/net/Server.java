package com.einherji.rs2world.net;

import com.einherji.rs2world.net.clients.Client;
import com.einherji.rs2world.net.clients.ClientService;
import com.einherji.rs2world.net.login.LoginException;
import com.einherji.rs2world.net.login.LoginResponseCodes;
import com.einherji.rs2world.net.packets.Packet;
import com.einherji.rs2world.net.packets.PacketDecoder;
import com.einherji.rs2world.net.util.Rs2Buffer;
import com.einherji.rs2world.net.util.Rs2ReadBuffer;
import com.einherji.rs2world.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public final class Server implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private static final int PORT = 43594;
    private static final int WORKERS = 3;
    private static final int BUFFER_SIZE = 2048;
    private static final int ACCEPT_ATTEMPTS = 5;
    private static final long ACCEPT_DELAY = 10000;

    private final ServerSocketChannel serverSocketChannel;
    private final Selector selector;
    private final ExecutorService workers;
    private final ThreadLocal<Rs2Buffer> workerBuffers;
    private final ClientService clientService;
    private final PacketDecoder packetDecoder;
    private final Timer acceptTimer = new Timer();

    public Server(ClientService clientService, PacketDecoder packetDecoder) {
        this.clientService = clientService;
        this.packetDecoder = packetDecoder;
        workers = Executors.newFixedThreadPool(WORKERS);
        workerBuffers = ThreadLocal.withInitial(() -> Rs2Buffer.createReadBuffer(ByteBuffer.allocateDirect(BUFFER_SIZE)));
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            selector.selectNow();
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                if (key.isAcceptable() && acceptTimer.elapsed(ACCEPT_DELAY)) {
                    acceptTimer.reset();
                    workers.submit(this::acceptConnections);
                }
                if (key.isReadable()) {
                    workers.submit(() -> read(key));
                }
                keys.remove();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO: host validation: needs to happen after the gateway
    private void acceptConnections() {
        ByteBuffer threadBuffer = workerBuffers.get().getBuffer();
        boolean worldFull = false;
        for (int i = 0; i < ACCEPT_ATTEMPTS; i++) {
            SocketChannel channel = null;
            try {
                channel = serverSocketChannel.accept();
                if (channel == null) return;
                channel.configureBlocking(false);

                if (worldFull) {
                    handleException(channel, threadBuffer, LoginResponseCodes.WORLD_FULL);
                    continue;
                }

                UUID uuid = UUID.randomUUID();
                SelectionKey key = channel.register(selector, SelectionKey.OP_READ, uuid);
                clientService.create(key, channel, uuid);
            } catch (IOException ioe) {
                LOGGER.error("Encountered error during accept cycle: ", ioe);
            } catch(LoginException le) {
                handleException(channel, threadBuffer, le.getResponseCode());
                if (le.getResponseCode() == LoginResponseCodes.WORLD_FULL)
                    worldFull = true;
            }
        }
    }

    private void read(SelectionKey key) {
        Rs2ReadBuffer buffer = (Rs2ReadBuffer) workerBuffers.get();
        Client client = clientService.get((UUID) key.attachment());
        if (client == null) {
            key.cancel();
            return;
        }
        try {
            buffer.getBuffer().clear();
            int bytesRead = client.getChannel().read(buffer.getBuffer());
            if (bytesRead == -1) {
                //TODO: properly terminate client
                client.getSelectionKey().cancel();
                return;
            }
        } catch(IOException ioe) {
            LOGGER.error("Encountered error during read operation: ", ioe);
        }
        client.getTimeoutTimer().reset();
        buffer.getBuffer().flip();
        Packet packet;
        while ((packet = packetDecoder.decode(client.getStatus(), buffer)) != null) {
            client.queuePacket(packet);
        }
    }

    private void handleException(SocketChannel channel, ByteBuffer buffer, byte responseCode) {
        if (channel == null) return;
        try {
            buffer.clear();
            buffer.put(responseCode);
            buffer.flip();
            channel.write(buffer);
            channel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
