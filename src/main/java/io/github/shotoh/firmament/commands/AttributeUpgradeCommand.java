package io.github.shotoh.firmament.commands;

import io.github.shotoh.firmament.core.auctions.AuctionItem;
import io.github.shotoh.firmament.features.AttributeUpgrade;
import io.github.shotoh.firmament.utils.Utils;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.discord.jda5.JDA5CommandManager;
import org.incendo.cloud.discord.jda5.JDAInteraction;
import org.incendo.cloud.discord.jda5.ReplySetting;
import org.incendo.cloud.discord.slash.CommandScope;
import org.incendo.cloud.discord.slash.DiscordChoices;
import org.incendo.cloud.parser.standard.BooleanParser;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.permission.PredicatePermission;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

public class AttributeUpgradeCommand implements Command {
    @Override
    public void register(@NonNull JDA5CommandManager<JDAInteraction> commandManager) {
        commandManager.command(
                commandManager.commandBuilder("au", Description.of("Calculate the best way to upgrade an item's attribute"))
                        .apply(ReplySetting.defer(false))
                        .apply(CommandScope.guilds())
                        .permission(PredicatePermission.of(jdaInteraction -> Utils.isDeveloper(jdaInteraction.user())))
                        .required("item", StringParser.quotedStringParser(), Description.of("The item name"))
                        .required("attribute", StringParser.quotedStringParser(), Description.description("The attribute name"), DiscordChoices.strings(Arrays.stream(AttributeUpgrade.VALID_ATTRIBUTES).map(string -> "\"" + string + "\"").toList()))
                        .required("start", IntegerParser.integerParser(1, 10), Description.of("The start level of the attribute"), DiscordChoices.integers(IntStream.range(1, 11).boxed().toList()))
                        .required("end", IntegerParser.integerParser(1, 10), Description.of("The end level of the attribute"), DiscordChoices.integers(IntStream.range(1, 11).boxed().toList()))
                        .required("minimum", IntegerParser.integerParser(1, 10), Description.of("The minimum level an attribute has to have"), DiscordChoices.integers(IntStream.range(1, 11).boxed().toList()))
                        .required("overflow", BooleanParser.booleanParser(), Description.of("Whether items should go past required weight"))
                        .handler(context -> {
                            JDAInteraction interaction = context.sender();
                            GenericCommandInteractionEvent event = interaction.interactionEvent();
                            if (event == null) return;
                            InteractionHook hook = event.getHook();
                            String item = context.get("item");
                            String attribute = context.get("attribute");
                            int start = context.get("start");
                            int end = context.get("end");
                            int minimum = context.get("minimum");
                            boolean overflow = context.get("overflow");
                            if (start >= end || minimum > start) {
                                hook.sendMessage("Start cannot be greater than the end or the minimum cannot be greater than the start!").queue();
                                return;
                            }
                            List<AuctionItem> auctionItems = Utils.scanAuctions();
                            hook.sendMessageEmbeds(AttributeUpgrade.calculateBestPrices(auctionItems, item, attribute, start, end, minimum, overflow)).queue();
                        })
        );
    }
}
