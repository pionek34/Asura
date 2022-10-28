package dev.lastknell.core;

import dev.lastknell.core.methods.IMethod;
import lombok.Getter;

public class AttackConfig {

    private @Getter IMethod method;
    private @Getter String srvIp;
    private @Getter int port;
    private @Getter int protocolID;
    private @Getter int duration;
    private @Getter int perDelay;
    private @Getter int delay;
    private @Getter int loopThreads;
    private @Getter int workerThreads;
    private @Getter int proxyType;
    
    /**
     * @param srvIp server ip resolved
     * @param port server port
     * @param method method duh!
     * @param protocolID protocol for version
     * @param duration duration of attack
     * @param perDelay connections per delay
     * @param delay delay in looping threads
     * @param loopThreads loopThreads 
     * @param workerThreads nettyThreads 
     * @param proxyType 0: SOCKS4 , 1 SOCKS5, 2: HTTP
     */
    public AttackConfig(String srvIp,
            int port,
            IMethod method,
            int protocolID,
            int duration,
            int perDelay,
            int delay,
            int loopThreads,
            int workerThreads,
            int proxyType) 
    {
        this.srvIp = srvIp;
        this.port = port;
        this.method = method;
        this.protocolID = protocolID;
        this.duration = duration;
        this.perDelay = perDelay;
        this.delay = delay;
        this.loopThreads = loopThreads;
        this.workerThreads = workerThreads;
        this.proxyType = proxyType;
    }
}
