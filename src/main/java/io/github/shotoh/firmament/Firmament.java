package io.github.shotoh.firmament;

import com.google.gson.Gson;
import io.github.shotoh.firmament.commands.NBTCommand;
import io.github.shotoh.firmament.commands.PingCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.incendo.cloud.discord.jda5.JDA5CommandManager;
import org.incendo.cloud.discord.jda5.JDAInteraction;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Firmament {
    public static final Logger LOGGER = LoggerFactory.getLogger(Firmament.class);
    public static final Gson GSON = new Gson();
    public static final String TOKEN = System.getenv("FIRMAMENT_TOKEN");

    public static void main(String[] args) {
        new Firmament();
    }

    private final JDA5CommandManager<JDAInteraction> commandManager;
    private final JDA jda;

    public Firmament() {
        LOGGER.info("Creating command manager...");
        this.commandManager = new JDA5CommandManager<>(
                ExecutionCoordinator.simpleCoordinator(),
                JDAInteraction.InteractionMapper.identity()
        );

        new NBTCommand().register(commandManager);
        new PingCommand().register(commandManager);

        LOGGER.info("Starting JDA...");
        if (TOKEN == null) {
            LOGGER.error("Token unspecified. Aborting.");
            System.exit(1);
        }
        this.jda = JDABuilder.createDefault(TOKEN)
                .addEventListeners(commandManager)
                .build();
    }
}
