package com.joaocsoliveira;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.ThreadingModel;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Logger;

public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        logger.info("Starting App::main!");

        Vertx vertx = Vertx.vertx();

        Future.all(Arrays.asList(
                vertx.deployVerticle("com.joaocsoliveira.verticles.InstantiatorVerticle"),
                vertx.deployVerticle("com.joaocsoliveira.verticles.PrinterVerticle",
                        new DeploymentOptions()
                                .setThreadingModel(ThreadingModel.WORKER)
                                .setConfig(new JsonObject().put("name", "first"))),
                vertx.deployVerticle("com.joaocsoliveira.verticles.PrinterVerticle",
                        new DeploymentOptions()
                                .setThreadingModel(ThreadingModel.WORKER)
                                .setConfig(new JsonObject().put("name", "second")))
        )).onComplete(ar -> {
            if (ar.failed()) {
                logger.info("failed deploying verticles");
            } else {
                String gameId = UUID.randomUUID().toString();
                EventBus eb = vertx.eventBus();
                vertx.setTimer(1000, id ->
                        eb.send("game.create", gameId)
                );

                eb.consumer(String.format("game.%s.stop", gameId), message ->
                        vertx.close()
                );
            }
        });

        logger.info("Exiting App::main!");
    }
}
