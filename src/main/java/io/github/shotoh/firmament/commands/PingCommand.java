package io.github.shotoh.firmament.commands;

import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.discord.jda5.JDA5CommandManager;
import org.incendo.cloud.discord.jda5.JDAInteraction;
import org.incendo.cloud.discord.jda5.ReplySetting;
import org.incendo.cloud.discord.slash.CommandScope;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.SuggestionProvider;

public class PingCommand implements Command {
    @Override
    public void register(@NonNull JDA5CommandManager<JDAInteraction> commandManager) {
        commandManager.command(
                commandManager.commandBuilder("ping", Description.of("A ping command"))
                        .apply(ReplySetting.defer(true))
                        .apply(CommandScope.guilds())
                        .required("message", StringParser.greedyStringParser(), Description.of("The message"), SuggestionProvider.noSuggestions())
                        .handler(context -> {
                            JDAInteraction interaction = context.sender();
                            GenericCommandInteractionEvent event = interaction.interactionEvent();
                            if (event == null) return;
                            String message = context.get("message");
                            event.getHook().sendMessage(message).queue();
                        })
        );
    }
}
