package com.joaocsoliveira;

import com.joaocsoliveira.verticles.MyVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.logging.Logger;

public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());
    private static final Vertx vertx = Vertx.vertx();

    public static void main(String[] args) {
        logger.info("Starting App::main!");

        vertx.deployVerticle(new MyVerticle())
                .onComplete(v -> {
                    if (v.succeeded()) {
                        logger.info("MyVerticle deployment succeeded");
                    } else {
                        logger.info("MyVerticle deployment failed");
                    }
                });

        var options = new DeploymentOptions().setInstances(5).setConfig(new JsonObject().put("name", "from factory"));
        vertx.deployVerticle("com.joaocsoliveira.verticles.MyVerticle", options)
                .onComplete(v -> {
                    if (v.succeeded()) {
                        logger.info("MyVerticle from factory deployment succeeded");
                    } else {
                        logger.info("MyVerticle from factory deployment failed");
                    }
                });

        logger.info("Exiting App::main!");
    }
}
