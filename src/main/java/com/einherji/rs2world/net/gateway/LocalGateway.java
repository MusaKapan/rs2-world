package com.einherji.rs2world.net.gateway;

import com.einherji.rs2world.Rs2WorldProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Rs2WorldProfiles.LOCAL)
public class LocalGateway extends Gateway {



}
