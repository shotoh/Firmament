package io.github.shotoh.firmament.features;

import io.github.shotoh.firmament.Firmament;
import io.github.shotoh.firmament.core.auctions.AuctionItem;
import me.nullicorn.nedit.NBTReader;
import me.nullicorn.nedit.type.NBTCompound;

import java.io.IOException;

public record AttributeItem(String uuid, String itemName, long startingBid, String attribute, int level, long pricePerLevel) {
    public static AttributeItem convertToAttributeItem(AuctionItem auctionItem, String targetAttribute) {
        try {
            NBTCompound tag = NBTReader.readBase64(auctionItem.item_bytes());
            if (tag.containsKey("i")) tag = tag.getList("i").getCompound(0).getCompound("tag");
            if (!tag.containsKey("ExtraAttributes")) return null;
            tag = tag.getCompound("ExtraAttributes");
            if (!tag.containsKey("attributes")) return null;
            tag = tag.getCompound("attributes");
            if (!tag.containsKey(targetAttribute)) return null;
            long startingBid = auctionItem.starting_bid();
            int level = tag.getInt(targetAttribute, 0);
            return new AttributeItem(auctionItem.uuid(), auctionItem.item_name(), startingBid,
                    targetAttribute, level, AttributeUpgrade.calculatePricePerLevel(startingBid, level));
        } catch (IOException e) {
            Firmament.LOGGER.warn("Error reading NBTCompound");
            throw new RuntimeException(e);
        }
    }
}