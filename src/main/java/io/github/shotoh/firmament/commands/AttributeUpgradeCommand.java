package io.github.shotoh.firmament.commands;

import io.github.shotoh.firmament.core.auctions.AuctionItem;
import io.github.shotoh.firmament.features.AttributeUpgrade;
import io.github.shotoh.firmament.utils.Utils;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.discord.jda5.JDA5CommandManager;
import org.incendo.cloud.discord.jda5.JDAInteraction;
import org.incendo.cloud.discord.jda5.ReplySetting;
import org.incendo.cloud.discord.slash.CommandScope;
import org.incendo.cloud.discord.slash.DiscordChoices;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.permission.PredicatePermission;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.List;

public class AttributeUpgradeCommand implements Command {
    @Override
    public void register(@NonNull JDA5CommandManager<JDAInteraction> commandManager) {
        commandManager.command(
                commandManager.commandBuilder("au", Description.of("Calculate the best way to upgrade an item's attribute"))
                        .apply(ReplySetting.defer(false))
                        .apply(CommandScope.guilds())
                        .permission(PredicatePermission.of(jdaInteraction -> Utils.isDeveloper(jdaInteraction.user())))
                        .required("item", StringParser.quotedStringParser(), Description.of("The item name"), SuggestionProvider.noSuggestions())
                        .required("attribute", StringParser.quotedStringParser(), Description.description("The attribute name"), SuggestionProvider.noSuggestions())
                        .required("start", IntegerParser.integerParser(1, 10), Description.of("The start level of the attribute"), DiscordChoices.integers(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
                        .required("end", IntegerParser.integerParser(1, 10), Description.of("The end level of the attribute"), DiscordChoices.integers(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
                        .required("minimum", IntegerParser.integerParser(1, 10), Description.of("The minimum level an attribute has to have"), DiscordChoices.integers(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
                        .handler(context -> {
                            JDAInteraction interaction = context.sender();
                            GenericCommandInteractionEvent event = interaction.interactionEvent();
                            if (event == null) return;
                            String item = context.get("item");
                            String attribute = context.get("attribute");
                            int start = context.get("start");
                            int end = context.get("end");
                            int minimum = context.get("minimum");
                            List<AuctionItem> auctionItems = Utils.scanAuctions();
                            event.getHook().sendMessageEmbeds(List.of(AttributeUpgrade.calculateBestPrices(auctionItems, item, attribute, start, end, minimum))).queue();
                        })
        );
    }
}
