package io.github.shotoh.firmament.commands;

import io.github.shotoh.firmament.utils.NBTUtils;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.querz.nbt.io.NBTDeserializer;
import net.querz.nbt.io.NBTInput;
import net.querz.nbt.io.NBTUtil;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.discord.jda5.JDA5CommandManager;
import org.incendo.cloud.discord.jda5.JDAInteraction;
import org.incendo.cloud.discord.jda5.ReplySetting;
import org.incendo.cloud.discord.slash.CommandScope;
import org.incendo.cloud.parser.standard.StringParser;

public class NBTCommand implements Command {
    @Override
    public void register(@NonNull JDA5CommandManager<JDAInteraction> commandManager) {
        commandManager.command(
                commandManager.commandBuilder("nbt", Description.of("NBT decoding command"))
                        .apply(ReplySetting.defer(false))
                        .apply(CommandScope.guilds())
                        .required("bytes", StringParser.greedyStringParser(), Description.of("Item bytes"))
                        .handler(context -> {
                            JDAInteraction interaction = context.sender();
                            GenericCommandInteractionEvent event = interaction.interactionEvent();
                            if (event == null) return;
                            String bytes = context.get("bytes");
                            event.getHook().sendMessage(NBTUtils.decodeNBT(bytes).toString()).queue();
                        })
        );
    }
}
