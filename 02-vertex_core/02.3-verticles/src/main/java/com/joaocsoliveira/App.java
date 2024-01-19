package com.joaocsoliveira;

import com.joaocsoliveira.verticles.MyVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class App {
    private static final Vertx vertx = Vertx.vertx();

    public static void main(String[] args) {
        System.out.println("Starting App::main!");

        vertx.deployVerticle(new MyVerticle())
                .onComplete(v -> {
                    if (v.succeeded()) {
                        System.out.println("MyVerticle deployment succeeded");
                    } else {
                        System.out.println("MyVerticle deployment failed");
                    }
                });

        var options = new DeploymentOptions().setInstances(5).setConfig(new JsonObject().put("name", "from factory"));
        vertx.deployVerticle("com.joaocsoliveira.verticles.MyVerticle", options)
                .onComplete(v -> {
                    if (v.succeeded()) {
                        System.out.println("MyVerticle from factory deployment succeeded");
                    } else {
                        System.out.println("MyVerticle from factory deployment failed");
                    }
                });

        System.out.println("Exiting App::main!");
    }
}
