package io.github.shotoh.firmament.features;

import io.github.shotoh.firmament.Firmament;
import io.github.shotoh.firmament.core.auctions.AuctionItem;
import io.github.shotoh.firmament.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.*;
import java.util.List;

public class AttributeUpgrade {
    public static final String[] VALID_ITEMS = new String[] {
            ""
    };
    public static final String[] VALID_ATTRIBUTES = new String[] {
            "Life Recovery", "Mana Steal", "Midas Touch",
            "Undead", "Warrior", "Deadeye", "Breeze", "Dominance",
            "Life Regeneration", "Lifeline", "Magic Find", "Mana Pool", "Mana Regeneration", "Vitality",
            "Speed", "Undead Resistance", "Veteran", "Blazing Fortune", "Fishing Experience", "Double Hook", "Fisherman",
            "Fishing Speed", "Hunter", "Trophy Hunter"
    };
    private static long totalCost = 0;

    public static MessageEmbed calculateBestPrices(List<AuctionItem> list, String itemName, String attribute,
                                                   int start, int end, int minimum, boolean overflow) {
        Firmament.LOGGER.info("Calculating best prices...");
        totalCost = 0;
        String formattedAttribute = attribute.replaceAll(" ", "_").toLowerCase(Locale.US);
        List<String> potentialNames = new ArrayList<>();
        addPotentialNames(potentialNames, itemName);
        List<AttributeItem> items = new ArrayList<>(list.stream()
                .filter(item -> Utils.containsInList(potentialNames, item.item_name()))
                .map(auctionItem -> AttributeItem.convertToAttributeItem(auctionItem, formattedAttribute))
                .filter(Objects::nonNull)
                .filter(attributeItem -> attributeItem.level() >= minimum)
                .filter(attributeItem -> attributeItem.level() < end)
                .sorted(Comparator.comparingLong(AttributeItem::pricePerLevel)).toList());
        int matches = items.size();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Upgrading **%s** on **%s** from **%d** to **%d**".formatted(attribute, itemName, start, end));
        builder.setColor(Color.RED);
        for (int i = start; i < end; i++) {
            appendEmbedWithBestPrice(builder, items, i, overflow);
        }
        builder.setDescription("Found **%d** matches out of **%d** auctions\nTotal Price: **%s**"
                .formatted(matches, list.size(), Utils.formatPrice(totalCost)));
        return builder.build();
    }

    public static void appendEmbedWithBestPrice(EmbedBuilder builder, List<AttributeItem> items, int level, boolean overflow) {
        Firmament.LOGGER.info("Appending embed with level: " + level);
        StringBuilder stringBuilder = new StringBuilder();
        int weight = calculateWeight(level);
        long cost = 0;
        int count = 0;
        int itemsIndex = 0;
        int firstFieldIndex = -1;
        while (weight > 0 && items.size() > itemsIndex) {
            AttributeItem attributeItem = items.get(itemsIndex);
            int itemWeight = calculateWeight(attributeItem.level());
            if (itemWeight > weight && !overflow) {
                itemsIndex++;
                continue;
            }
            weight -= itemWeight;
            cost += attributeItem.startingBid();
            items.remove(itemsIndex);
            stringBuilder.append("`/viewauction ").append(attributeItem.uuid())
                    .append("` (**").append(attributeItem.level()).append("**) (**")
                    .append(Utils.formatPrice(attributeItem.startingBid())).append("**)");
            String attributeItemName = attributeItem.itemName();
            if (attributeItemName.equals("Attribute Shard")) {
                stringBuilder.append(" [**S**]");
            } else if (attributeItemName.contains("Crimson")) {
                stringBuilder.append(" [**C**]");
            } else if (attributeItemName.contains("Aurora")) {
                stringBuilder.append(" [**A**]");
            } else if (attributeItemName.contains("Terror")) {
                stringBuilder.append(" [**T**]");
            } else if (attributeItemName.contains("Fervor")) {
                stringBuilder.append(" [**F**]");
            } else if (attributeItemName.contains("Hollow")) {
                stringBuilder.append(" [**H**]");
            }
            count++;
            if (weight <= 0 || items.size() <= itemsIndex || count == 8) {
                if (items.size() <= itemsIndex) stringBuilder.append("\n").append("**[!] Not enough items to fulfill upgrade!**");
                if (count == 8) count = 0;
                builder.addField("", stringBuilder.toString(), false);
                if (firstFieldIndex == -1) firstFieldIndex = builder.getFields().size() - 1;
                stringBuilder.setLength(0);
                if (weight <= 0 || items.size() <= itemsIndex) {
                    MessageEmbed.Field field = builder.getFields().get(firstFieldIndex);
                    builder.getFields().set(firstFieldIndex,
                            new MessageEmbed.Field("Upgrade to %d: **%s**".formatted(level + 1,
                                    Utils.formatPrice(cost)), field.getValue(), false));
                }
            } else {
                stringBuilder.append("\n");
            }
        }
        totalCost += cost;
    }

    public static void addPotentialNames(List<String> potentialNames, String name) {
        List<String> kuudraArmor = List.of("Crimson", "Aurora", "Fervor", "Terror", "Hollow");
        if (Utils.containsInList(kuudraArmor, name) &&
                Utils.containsInList(List.of("Helmet", "Chestplate", "Leggings", "Boots"), name)) {
            String type = name.split(" ")[1];
            for (String s : kuudraArmor) {
                potentialNames.add(s + " " + type);
            }
        } else {
            potentialNames.add(name);
        }
        potentialNames.add("Attribute Shard");
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
