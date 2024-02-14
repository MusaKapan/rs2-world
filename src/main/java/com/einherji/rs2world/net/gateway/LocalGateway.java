package com.einherji.rs2world.net.gateway;

import com.einherji.rs2world.Rs2WorldProfiles;
import com.einherji.rs2world.net.packets.impl.LoginPacketBuilder.LoginPacket;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Rs2WorldProfiles.LOCAL)
public class LocalGateway implements Gateway {

    @Override
    public void registerLoginRequest(LoginPacket packet) {

    }
}
