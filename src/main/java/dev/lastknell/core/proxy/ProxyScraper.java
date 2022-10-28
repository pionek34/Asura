package dev.lastknell.core.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;

public class ProxyScraper {

    private ArrayList<URL> urls = new ArrayList<URL>();
    private @Getter HashSet<Proxy> proxies = new HashSet<Proxy>();

    /**
     * @param urls link to scrpae proxies from
     * @throws MalformedURLException
     */
    public ProxyScraper(ArrayList<String> urls) throws MalformedURLException {
        for (int i = 0; i < urls.size(); i++) {
            this.urls.add(new URL(urls.get(i)));
        }
    }

    public void scrape() {

        urls.forEach(url -> {
            try {
                BufferedReader read = new BufferedReader(new InputStreamReader(url.openStream()));
                String line;
                while ((line = read.readLine()) != null) {
                    Proxy p = getProxy(line);
                    if (p != null) {
                        proxies.add(p);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

    }

    public static Proxy getProxy(String line) {
        Proxy p = null;
        String[] parts = line.split(":");
        if (parts.length == 2) {
            if (isValidIPAddress(parts[0]) && Integer.parseInt(parts[1]) < 65535 && Integer.parseInt(parts[1]) > 0) {
                p = new Proxy(new InetSocketAddress(parts[0], Integer.parseInt(parts[1])));
            }
        }
        if (parts.length == 4) {
            if (isValidIPAddress(parts[0]) && Integer.parseInt(parts[1]) < 65535 && Integer.parseInt(parts[1]) > 0) {
                p = new Proxy(new InetSocketAddress(parts[0], Integer.parseInt(parts[1])), parts[2], parts[3]);
            }
        }
        return p;
    }

    public static boolean isValidIPAddress(String ip) {
        String zeroTo255 = "(\\d{1,2}|(0|1)\\" + "d{2}|2[0-4]\\d|25[0-5])";
        String regex = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;
        Pattern p = Pattern.compile(regex);
        if (ip == null) {
            return false;
        }
        Matcher matcher = p.matcher(ip);
        return matcher.matches();
    }
}
