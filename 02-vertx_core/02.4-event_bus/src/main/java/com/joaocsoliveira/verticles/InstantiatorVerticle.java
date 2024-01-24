package com.joaocsoliveira.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.joaocsoliveira.Config.*;

public class InstantiatorVerticle extends AbstractVerticle {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void start() {
        logger.info("InstantiatorVerticle is being deployed");

        EventBus eb = vertx.eventBus();
        eb.consumer("game.create", message -> {
            String gameId = (String) message.body();
            logger.log(Level.INFO, "InstantiatorVerticle: creating game : {0}", gameId);

            String player1Id = UUID.randomUUID().toString();
            String player2Id = UUID.randomUUID().toString();

            Future.all(Arrays.asList(
                vertx.deployVerticle("com.joaocsoliveira.verticles.JudgeVerticle", new DeploymentOptions().setConfig(new JsonObject().put(GAME_ID_ATTRIBUTE_NAME, gameId).put(PLAYER_1_ID_ATTRIBUTE_NAME, player1Id).put(PLAYER_2_ID_ATTRIBUTE_NAME, player2Id))),
                vertx.deployVerticle("com.joaocsoliveira.verticles.PlayerVerticle", new DeploymentOptions().setConfig(new JsonObject().put(GAME_ID_ATTRIBUTE_NAME, gameId).put(PLAYER_ID_ATTRIBUTE_NAME, player1Id))),
                vertx.deployVerticle("com.joaocsoliveira.verticles.PlayerVerticle", new DeploymentOptions().setConfig(new JsonObject().put(GAME_ID_ATTRIBUTE_NAME, gameId).put(PLAYER_ID_ATTRIBUTE_NAME, player2Id)))
            )).onComplete(ar -> {
                if (ar.failed()) {
                    logger.log(Level.INFO, "InstantiatorVerticle: creating game {0} failed : {1}", new Object[]{gameId, ar.cause()});
                } else {
                    eb.send(String.format("game.%s", gameId), "start");
                }
            });
        });
    }

    @Override
    public void stop() {
        logger.info("InstantiatorVerticle is being undeployed");
    }
}
