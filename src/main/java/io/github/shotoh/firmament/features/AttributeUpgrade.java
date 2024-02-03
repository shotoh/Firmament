package io.github.shotoh.firmament.features;

import io.github.shotoh.firmament.core.auctions.AuctionItem;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.List;

public class AttributeUpgrade {
    public static void calculateBestPrices(List<AuctionItem> list, String itemName, String attribute, int start, int end) {
        List<AuctionItem> items = new ArrayList<>(list);
        //items.stream().filter(item -> item.item_name().equals(itemName) || item.item_name().equals("Attribute Shard"))
        //        .filter()
    }
}
