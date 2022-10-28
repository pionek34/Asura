package dev.lastknell.core.proxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProxyManager {

    private volatile List<Proxy> proxies = Collections.synchronizedList(new ArrayList<Proxy>());
    private volatile int atIndex = 0;

    /**
     * @param proxies A list of PROXIES
     */
    public ProxyManager(ArrayList<Proxy> proxies) {
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
        proxies.remove(proxy);
    }

}
