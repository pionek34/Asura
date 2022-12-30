package dev.lastknell.util.proxy;

import dev.lastknell.core.proxy.Proxy;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyScraper {

    private final ArrayList<URL> urls = new ArrayList<>();
    private final @Getter ArrayList<Proxy> proxies = new ArrayList<>();

    /**
     * @param urls link to scrape proxies from
     */
    public ProxyScraper(List<String> urls) throws MalformedURLException {
        for (String url : urls) {
            this.urls.add(new URL(url));
        }
    }

    public void scrape() {
        ExecutorService executorService = Executors.newFixedThreadPool(urls.size());

        urls.forEach(url -> executorService.submit(() ->{
            try {
                BufferedReader read = new BufferedReader(new InputStreamReader(url.openStream()));
                String line;
                while ((line = read.readLine()) != null) {
                    Proxy p = getProxy(line);
                    if (p != null && !proxies.contains(p)) {
                            synchronized (this) {
                                proxies.add(p);
                            }
                    }
                }
                read.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }));

        executorService.shutdown();
        while (!executorService.isTerminated()){

        }
    }

    public static Proxy getProxy(String line) {
        Proxy p = null;
        try {
            String[] parts = line.split(":");
            switch (parts.length) {
                case 2:
                    if (isValidIPAddress(parts[0]) && Integer.parseInt(parts[1]) < 65535 && Integer.parseInt(parts[1]) > 0) {
                        p = new Proxy(new InetSocketAddress(parts[0], Integer.parseInt(parts[1])), null, null);
                    }
                    break;
                case 4:
                    if (isValidIPAddress(parts[0]) && Integer.parseInt(parts[1]) < 65535 && Integer.parseInt(parts[1]) > 0) {
                        p = new Proxy(new InetSocketAddress(parts[0], Integer.parseInt(parts[1])), parts[2], parts[3]);
                    }
                    break;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
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