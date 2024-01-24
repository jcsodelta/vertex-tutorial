package com.joaocsoliveira;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.ThreadingModel;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.UUID;

public class App {
    public static void main(String[] args) {
        System.out.println("Starting App::main!");

        Vertx vertx = Vertx.vertx();

        Future.all(Arrays.asList(
            vertx.deployVerticle("com.joaocsoliveira.verticles.InstantiatorVerticle"),
            vertx.deployVerticle("com.joaocsoliveira.verticles.PrinterVerticle", new DeploymentOptions().setThreadingModel(ThreadingModel.WORKER).setConfig(new JsonObject().put("name", "first"))),
            vertx.deployVerticle("com.joaocsoliveira.verticles.PrinterVerticle", new DeploymentOptions().setThreadingModel(ThreadingModel.WORKER).setConfig(new JsonObject().put("name", "second")))
        )).onComplete(ar -> {
            if (ar.failed()) {
                System.out.println("failed deploying verticles");
            } else {
                String game_id = UUID.randomUUID().toString();
                EventBus eb = vertx.eventBus();
                vertx.setTimer(1000, id -> {
                    eb.send("game.create", game_id);
                });

                eb.consumer(String.format("game.%s.stop", game_id), message -> {
                    vertx.close();
                });
            }
        });

        System.out.println("Exiting App::main!");
    }
}
