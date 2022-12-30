package dev.lastknell.core.config;

import dev.lastknell.core.manager.ConnectionsInfo;
import io.netty.bootstrap.Bootstrap;

import java.util.ArrayList;

public class AttackConfig {
    public final int protocolID;
    public final String srvIp;
    public final int port;
    public final ConnectionsInfo cpsinfo;
    public final int duration;
    public final int loopThreads;
    public final ArrayList<Bootstrap> bootstraps = new ArrayList<>();
    public final int delay;
    public final int connectionsPerDelay;

    public AttackConfig(String srvIp, int port, int protocolID, ConnectionsInfo cpsinfo, int duration, int loopThreads, Bootstrap bootstrap, int delay, int connectionsPerDelay) {
        this.protocolID = protocolID;
        this.srvIp = srvIp;
        this.port = port;
        this.cpsinfo = cpsinfo;
        this.duration = duration;
        this.loopThreads = loopThreads;
        this.bootstraps.add(bootstrap);
        this.delay = delay;
        this.connectionsPerDelay = connectionsPerDelay;
    }

    public void addBootstrap(Bootstrap bootstrap) {
        bootstraps.add(bootstrap);
    }
}
