package com.joaocsoliveira.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.UUID;

public class InstantiatorVerticle extends AbstractVerticle {
    public void start() {
        System.out.println("InstantiatorVerticle is being deployed");

        EventBus eb = vertx.eventBus();
        eb.consumer("game.create", message -> {
            String game_id = (String) message.body();
            System.out.printf("InstantiatorVerticle: creating game : %s\n", game_id);

            String player1_id = UUID.randomUUID().toString();
            String player2_id = UUID.randomUUID().toString();

            Future.all(Arrays.asList(
                vertx.deployVerticle("com.joaocsoliveira.verticles.JudgeVerticle", new DeploymentOptions().setConfig(new JsonObject().put("game_id", game_id).put("player1_id", player1_id).put("player2_id", player2_id))),
                vertx.deployVerticle("com.joaocsoliveira.verticles.PlayerVerticle", new DeploymentOptions().setConfig(new JsonObject().put("game_id", game_id).put("player_id", player1_id))),
                vertx.deployVerticle("com.joaocsoliveira.verticles.PlayerVerticle", new DeploymentOptions().setConfig(new JsonObject().put("game_id", game_id).put("player_id", player2_id)))
            )).onComplete(ar -> {
                if (ar.failed()) {
                    System.out.printf("InstantiatorVerticle: creating game failed : %s\n", game_id);
                } else {
                    eb.send(String.format("game.%s", game_id), "start");
                }
            });
        });
    }

    public void stop() {
        System.out.println("InstantiatorVerticle is being undeployed");
    }
}
