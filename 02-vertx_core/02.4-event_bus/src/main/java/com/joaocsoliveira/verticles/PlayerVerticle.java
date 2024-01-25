package com.joaocsoliveira.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;

import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.joaocsoliveira.Config.*;

public class PlayerVerticle extends AbstractVerticle {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final SecureRandom random = new SecureRandom();

    @Override
    public void start() {
        String playerId = config().getString(PLAYER_ID_ATTRIBUTE_NAME);
        String gameId = config().getString(GAME_ID_ATTRIBUTE_NAME);

        logger.log(Level.INFO, "PlayerVerticle ({0}) is being deployed", playerId);

        EventBus eb = vertx.eventBus();
        eb.consumer(String.format("game.%s.turn.%s", gameId, playerId), message -> {
            int value = random.nextInt(10);
            eb.send(OUTPUT_PRINTER_ADDRESS, String.format("%s: playing %d", playerId, value));
            message.reply(value, new DeliveryOptions().addHeader(PLAYER_ID_ATTRIBUTE_NAME, playerId));
        });
    }

    @Override
    public void stop() {
        logger.log(Level.INFO, "PlayerVerticle ({0}) is being undeployed\n", config().getString(PLAYER_ID_ATTRIBUTE_NAME));
    }
}
