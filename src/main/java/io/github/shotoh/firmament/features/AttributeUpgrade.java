package io.github.shotoh.firmament.features;

import io.github.shotoh.firmament.core.auctions.AuctionItem;
import io.github.shotoh.firmament.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class AttributeUpgrade {
    private static long totalCost = 0;

    public static MessageEmbed calculateBestPrices(List<AuctionItem> list, String itemName, String attribute, int start, int end, int minimum) {
        totalCost = 0;
        List<AttributeItem> items = new ArrayList<>(list.stream()
                .filter(item -> item.item_name().equals(itemName) || item.item_name().equals("Attribute Shard"))
                .map(auctionItem -> AttributeItem.convertToAttributeItem(auctionItem, attribute))
                .filter(Objects::nonNull)
                .filter(attributeItem -> attributeItem.level() >= minimum)
                .filter(attributeItem -> attributeItem.level() < end)
                .sorted(Comparator.comparingLong(AttributeItem::pricePerLevel)).toList());
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Upgrading **%s** on **%s** from **%d** to **%d**".formatted(attribute, itemName, start, end));
        builder.setColor(Color.RED);
        for (int i = start; i < end; i++) {
            appendEmbedWithBestPrice(builder, items, i);
        }
        builder.setDescription("Total Price: **%s**".formatted(Utils.formatPrice(totalCost)));
        return builder.build();
    }

    public static void appendEmbedWithBestPrice(EmbedBuilder builder, List<AttributeItem> items, int level) {
        StringBuilder stringBuilder = new StringBuilder();
        int weight = calculateWeight(level);
        long cost = 0;
        int count = 0;
        boolean usedName = false;
        while (weight > 0 && !items.isEmpty()) {
            AttributeItem attributeItem = items.getFirst();
            int itemWeight = calculateWeight(attributeItem.level());
            if (itemWeight > weight) continue;
            weight -= itemWeight;
            cost += attributeItem.startingBid();
            items.removeFirst();
            stringBuilder.append("`/viewauction ").append(attributeItem.uuid())
                    .append("` (**").append(attributeItem.level()).append("**) (**")
                    .append(Utils.formatPrice(attributeItem.startingBid())).append("**)");
            if (attributeItem.itemName().equals("Attribute Shard")) stringBuilder.append(" [**S**]");
            count++;
            if (weight == 0 || items.isEmpty() || count == 8) {
                String name = "Upgrade to %d: **%s**".formatted(level + 1, Utils.formatPrice(cost));
                if (usedName) {
                    name = "";
                } else {
                    usedName = true;
                }
                if (count == 8) count = 0;
                builder.addField(name, stringBuilder.toString(), false);
                stringBuilder.setLength(0);
            } else {
                stringBuilder.append("\n");
            }
        }
        totalCost += cost;
    }

    public static long calculatePricePerLevel(long price, int level) {
        int weight = calculateWeight(level);
        if (weight == 0) return 0;
        return price / weight;
    }

    public static int calculateWeight(int level) {
        if (level <= 0 || level > 10) return 0;
        if (level == 1) return 1;
        return (int) Math.pow(2, level - 1);
    }
}
