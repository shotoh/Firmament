package io.github.shotoh.firmament.core.auctions;

import java.util.List;

public record AuctionPage (boolean success, int totalPages, long lastUpdated, List<AuctionItem> auctions) {}