package net.glowstone.util.config;

import lombok.Data;

@Data
public class BrokerConfig {

    private final String type;
    private final ChannelConfig channel;
}
