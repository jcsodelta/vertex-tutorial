package com.joaocsoliveira.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;

import java.util.Random;

public class PlayerVerticle extends AbstractVerticle {
    private final Random random = new Random();

    public void start() {
        String player_id = config().getString("player_id");
        String game_id = config().getString("game_id");

        System.out.printf("PlayerVerticle (%s) is being deployed\n", player_id);

        EventBus eb = vertx.eventBus();
        eb.consumer(String.format("game.%s.turn.%s", game_id, player_id), message -> {
            int value = random.nextInt(10);
            eb.send("output.printer", String.format("%s: playing %d", player_id, value));
            message.reply(value, new DeliveryOptions().addHeader("player_id", player_id));
        });
    }

    public void stop() {
        System.out.printf("PlayerVerticle (%s) is being undeployed\n", config().getString("player_id"));
    }
}
