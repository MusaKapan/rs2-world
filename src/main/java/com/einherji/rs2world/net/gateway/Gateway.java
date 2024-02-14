package com.einherji.rs2world.net.gateway;

import com.einherji.rs2world.net.packets.impl.LoginPacketBuilder.LoginPacket;

/**
 * A 'Gateway' is a collection of actions that may or may not need to be delegated to an external resource,
 * such as a login server for logging in across several worlds, chat server to be able to send/receive messages
 * from players on different worlds, clan chat, orchestrating scheduled system updates across all worlds etc
 *
 * Most private servers will simply have a "local" or "standalone" gateway, meaning that all these actions are dealt with
 * on the world server itself because there is no need for outside communication. If, however, the need arises for multiple
 * worlds which share the same user data and allow communication across them, then a "remote" gateway will be necessary
 * to orchestrate and provide in all these functions.
 */
public interface Gateway {

    void registerLoginRequest(LoginPacket packet);

}
