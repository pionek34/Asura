package dev.lastknell.core.manager.attack;

import dev.lastknell.core.config.AttackConfig;
import dev.lastknell.core.manager.ConnectionsInfo;
import dev.lastknell.core.manager.attack.handler.AttackHandlerThread;
import dev.lastknell.core.methods.iAttackMethod;

import java.util.ArrayList;

public class AttackManager {
    public static ArrayList<iAttackMethod> methods = new ArrayList<>();

    public static ConnectionsInfo startAttack(iAttackMethod method, AttackConfig config) {
        ConnectionsInfo info = new ConnectionsInfo();
        methods.add(method);
        Thread thread = new Thread(new AttackHandlerThread(config, info, method));
        thread.setName("Attack Thread");
        thread.setDaemon(true);
        thread.setPriority(5);
        thread.start();
        return info;
    }
}
