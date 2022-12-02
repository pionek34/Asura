package dev.lastknell.core.proxy;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProxyManager {

    private final CopyOnWriteArrayList<Proxy> proxies;
    private volatile int atIndex = 0;

    public ProxyManager(ArrayList<Proxy> proxies) {
        this.proxies = new CopyOnWriteArrayList<>();
        this.proxies.addAll(proxies);
    }

    public Proxy getProxy() {
        int get = this.atIndex++;
        if (get > this.proxies.size() - 1) {
            get = 0;
            this.atIndex = 1;
        }
        return this.proxies.get(get);
    }

    public void removeProxy(Proxy proxy) {
        this.proxies.remove(proxy);
    }
}
