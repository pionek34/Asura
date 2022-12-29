package test;

import dev.lastknell.core.proxy.Proxy;
import dev.lastknell.core.proxy.ProxyManager;
import dev.lastknell.core.proxy.util.ProxyScraper;

import java.net.MalformedURLException;
import java.util.ArrayList;

public class ProxyScraperText {

    public static ProxyManager makeProxyManager() {
        ArrayList<String> urls = new ArrayList<>();
        //now add all urls u want to scrape proxyes from in this list
        urls.add("https://raw.githubusercontent.com/monosans/proxy-list/main/proxies/socks4.txt");
        ArrayList<Proxy> proxies = new ArrayList<>();
        try {
            ProxyScraper scraper = new ProxyScraper(urls);
            // scrape proxies from links
            scraper.scrape();


            //this line is priority!
            Thread.sleep(3000L);
            // get proxies
            proxies = new ArrayList<>(scraper.getProxies());

            System.out.println(proxies);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // make proxy manager
        return new ProxyManager(proxies);
    }

    public static void main(String[] args) {
        makeProxyManager();
    }
}
