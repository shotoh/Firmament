package io.github.shotoh.firmament.core.auctions;

public record AuctionItem(String uuid, String item_name, long starting_bid, String item_bytes, boolean bin) {
}