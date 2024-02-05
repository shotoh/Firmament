package io.github.shotoh.firmament.utils;

import io.github.shotoh.firmament.Firmament;
import io.github.shotoh.firmament.core.auctions.AuctionItem;
import io.github.shotoh.firmament.core.auctions.AuctionPage;
import net.dv8tion.jda.api.entities.User;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

public class Utils {
    public static boolean isDeveloper(User user) {
        return user.getId().equals("301924109007454217");
    }

    public static List<AuctionItem> scanAuctions() {
        Firmament.LOGGER.info("Scanning auctions...");
        List<AuctionItem> list = new ArrayList<>();
        try {
            AuctionPage firstPage = getAuctionPage(0);
            if (firstPage == null || !firstPage.success()) return list;
            int totalPages = firstPage.totalPages();
            // creates futures between page 1 to total pages and collects them into a list
            List<CompletableFuture<AuctionPage>> futures = IntStream.range(0, totalPages)
                    .mapToObj(i -> CompletableFuture.supplyAsync(() -> getAuctionPage(i)))
                    .toList();
            // waits for all the futures to invoke (finish)
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
            // streams through all futures, gets all pages, then gets all auctions, filters bins
            futures.stream().map(future -> {
                try {
                    return future.get().auctions();
                } catch (Exception e) {
                    return new ArrayList<AuctionItem>();
                }
            }).flatMap(Collection::stream).filter(AuctionItem::bin).forEach(list::add);
        } catch (ExecutionException | InterruptedException e) {
            Firmament.LOGGER.warn("Error scanning auctions");
            throw new RuntimeException(e);
        }
        return list;
    }

    public static AuctionPage getAuctionPage(int page) {
        Firmament.LOGGER.info("Getting auction page: " + page);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet("https://api.hypixel.net/v2/skyblock/auctions?page=" + page);
            httpGet.addHeader("content-type", "application/json; charset=UTF-8");
            try (ClassicHttpResponse httpResponse = httpClient.execute(httpGet)) {
                return Firmament.GSON.fromJson(new InputStreamReader(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8), AuctionPage.class);
            }
        } catch (IOException e) {
            Firmament.LOGGER.warn("Error scanning auction page");
            throw new RuntimeException(e);
        }
    }

    public static String formatPrice(long price) {
        double div = 1.0;
        String suffix = "";
        if (price >= 1000000) {
            div = 1000000.0;
            suffix = "M";
        } else if (price >= 1000) {
            div = 1000.0;
            suffix = "K";
        }
        String val = price / div + "";
        if (val.contains(".") && val.substring(val.indexOf(".")).length() > 2) val = val.substring(0, val.indexOf(".") + 2);
        return val + suffix;
    }

    public static boolean containsInList(List<String> list, String string) {
        for (String s : list) {
            if (string.contains(s)) return true;
        }
        return false;
    }

    /*
    CompletableFuture.supplyAsync(() -> {
                while (!currentAuctions.isEmpty()) {
                    int index = ThreadLocalRandom.current().nextInt(currentAuctions.size());
                    AuctionItem item = currentAuctions.get(index);
                    try {
                        NBTTagCompound tag = CompressedStreamTools.readCompressed(
                                new ByteArrayInputStream(Base64.getDecoder().decode(item.getItemBytes()))
                        );
                        scans.getAndIncrement();
                        PriceInfo priceInfo = mod.getPriceChecker().checkPrice(tag);
                        long price = priceInfo.getPrice();
                        price -= price * HySniperConfig.flippingPricePercent * 0.01;
                        StringBuilder builder = priceInfo.getBuilder();
                        if (price - item.getStartingBid() >= HySniperConfig.flippingMinimumProfit * 1000000L) {
                            builder.append("Total Price: ").append(Utils.formatPrice(price));
                            flipAlert(item, price);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    oldAuctions.add(item.getUUID());
                    currentAuctions.remove(item);
                }
                Utils.addMessage("§7Completed " + scans.get() + " auction scans!");
                return null;
            }).get(45, TimeUnit.SECONDS);
     */
}
