package dev.lastknell.core.proxy;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ProxyManager {

    private final CopyOnWriteArrayList<Proxy> proxies;
    private final AtomicInteger atIndex;

    public ProxyManager(ArrayList<Proxy> proxies) {
        this.proxies = new CopyOnWriteArrayList<>();
        this.proxies.addAll(proxies);
        this.atIndex = new AtomicInteger(0);
    }

    public Proxy getProxy() {
        int get = this.atIndex.getAndIncrement();
        if (get > this.proxies.size() - 1) {
            get = 0;
            this.atIndex.set(1);
        }
        return this.proxies.get(get);
    }

    public void removeProxy(Proxy proxy) {
        this.proxies.remove(proxy);
    }
}
