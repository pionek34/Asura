package test;

import dev.lastknell.core.proxy.util.ProxyScraper;

import java.net.MalformedURLException;
import java.util.ArrayList;

public class ProxyScraperText {
    public static void main(String[] args) {
        ArrayList<String> urls = new ArrayList<>();
        urls.add("https://raw.githubusercontent.com/monosans/proxy-list/main/proxies/socks4.txt");

        try {
            ProxyScraper scraper = new ProxyScraper(urls.stream().toList());
            scraper.scrape();
            System.out.println(scraper.getProxies());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
