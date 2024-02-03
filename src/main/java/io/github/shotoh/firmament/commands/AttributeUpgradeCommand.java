package io.github.shotoh.firmament.commands;

import io.github.shotoh.firmament.utils.Utils;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.discord.jda5.JDA5CommandManager;
import org.incendo.cloud.discord.jda5.JDAInteraction;
import org.incendo.cloud.discord.jda5.ReplySetting;
import org.incendo.cloud.discord.slash.CommandScope;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.permission.PredicatePermission;

public class AttributeUpgradeCommand implements Command {
    @Override
    public void register(@NonNull JDA5CommandManager<JDAInteraction> commandManager) {
        commandManager.command(
                commandManager.commandBuilder("attributeupgrade",
                                Description.of("Calculate the best way to upgrade an item's attribute"), "au")
                        .apply(ReplySetting.doNotDefer())
                        .apply(CommandScope.guilds())
                        .permission(PredicatePermission.of(interaction -> Utils.isDeveloper(interaction.user())))
                        .required("item", StringParser.quotedStringParser(), Description.of("The item name"))
                        .required("attribute", StringParser.quotedStringParser(), Description.description("The attribute name"))
                        .required("start", IntegerParser.integerParser(1, 10), Description.of("The start level of the attribute"))
                        .required("end", IntegerParser.integerParser(1, 10), Description.of(("The end level of the attribute")))
                        .handler(context -> {
                            JDAInteraction interaction = context.sender();
                            IReplyCallback callback = interaction.replyCallback();
                            if (callback == null) return;
                            String item = context.get("item");
                            String attribute = context.get("attribute");
                            int start = context.get("start");
                            int end = context.get("end");
                            //callback.replyEmbeds()
                        })
        );
    }
}
