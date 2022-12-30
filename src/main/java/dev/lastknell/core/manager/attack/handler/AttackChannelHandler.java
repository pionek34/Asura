package dev.lastknell.core.manager.attack.handler;

import dev.lastknell.core.manager.attack.AttackManager;
import dev.lastknell.core.methods.iAttackMethod;
import dev.lastknell.core.proxy.Proxy;
import io.netty.channel.Channel;

import java.util.ArrayList;

public class AttackChannelHandler {
    public static void acceptChannel(Channel channel, Proxy proxy) {
        ArrayList<iAttackMethod> methods = AttackManager.methods;
        for (iAttackMethod method : methods) {
            method.accept(channel, proxy);
        }
    }
}
