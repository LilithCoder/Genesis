package com.hatsukoi.genesis.remoting;

import java.io.IOException;

/**
 * @author gaoweilin
 * @date 2022/06/07 Tue 1:52 AM
 */
public interface Client extends Endpoint, Channel {
    void reconnect() throws IOException;
}
