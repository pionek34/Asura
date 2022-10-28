package dev.lastknell.core;

import dev.lastknell.core.methods.IMethod;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class AttackConfig {

    private @Getter IMethod method;
    private @Getter String srvIp;
    private @Getter int port;
    private @Getter int duration;
    private @Getter int perDelay;
    private @Getter int delay;
    private @Getter int loopThreads;
    private @Getter int workerThreads;
    private @Getter Class<? extends SocketChannel> sClass;
    private @Getter int proxyType;
    
    /**
     * @param srvIp server ip resolved
     * @param port server port
     * @param method method duh!
     * @param duration duration of attack
     * @param perDelay connections per delay
     * @param delay delay in looping threads
     * @param loopThreads loopThreads 
     * @param workerThreads nettyThreads 
     * @param sClass socketChannel class NioSocketChannel or EpollSocketChannel
     * @param proxyType 0: NONE, 1: SOCKS5, 2: SOCK4, 3: HTTP
     */
    public AttackConfig(String srvIp,
            int port,
            IMethod method,
            int duration,
            int perDelay,
            int delay,
            int loopThreads,
            int workerThreads,
            Class<? extends SocketChannel> sClass,
            int proxyType) 
    {
        this.srvIp = srvIp;
        this.port = port;
        this.method = method;
        this.duration = duration;
        this.perDelay = perDelay;
        this.delay = delay;
        this.loopThreads = loopThreads;
        this.workerThreads = workerThreads;
        this.sClass = sClass;
        this.proxyType = proxyType;
    }
}
