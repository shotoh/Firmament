package io.github.shotoh.firmament;

import com.google.gson.Gson;
import io.github.shotoh.firmament.commands.AttributeUpgradeCommand;
import io.github.shotoh.firmament.commands.PingCommand;
import io.leangen.geantyref.TypeToken;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.discord.jda5.JDA5CommandManager;
import org.incendo.cloud.discord.jda5.JDAInteraction;
import org.incendo.cloud.exception.handling.ExceptionContext;
import org.incendo.cloud.exception.handling.ExceptionHandler;
import org.incendo.cloud.exception.handling.ExceptionHandlerRegistration;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Firmament {
    public static final Logger LOGGER = LoggerFactory.getLogger(Firmament.class);
    public static final Gson GSON = new Gson();
    public static final String TOKEN = System.getenv("FIRMAMENT_TOKEN");

    public static void main(String[] args) throws InterruptedException {
        new Firmament();
    }

    private final JDA5CommandManager<JDAInteraction> commandManager;
    private final JDA jda;

    public Firmament() throws InterruptedException {
        LOGGER.info("Creating command manager...");
        this.commandManager = new JDA5CommandManager<>(
                ExecutionCoordinator.simpleCoordinator(),
                JDAInteraction.InteractionMapper.identity()
        );

        new AttributeUpgradeCommand().register(commandManager);
        new PingCommand().register(commandManager);

        LOGGER.info("Starting JDA...");
        if (TOKEN == null) {
            LOGGER.error("Token unspecified. Aborting.");
            System.exit(1);
        }
        this.jda = JDABuilder.createDefault(TOKEN)
                .addEventListeners(commandManager.createListener())
                .setActivity(Activity.watching("subjects"))
                .build();

        jda.awaitReady();
    }
}
