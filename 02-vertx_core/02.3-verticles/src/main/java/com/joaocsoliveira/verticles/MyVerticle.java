package com.joaocsoliveira.verticles;

import io.vertx.core.*;

public class MyVerticle extends AbstractVerticle {
    public void start() {
        System.out.printf("MyVerticle (%s %s) is being deployed\n", config().getString("name"), context.deploymentID());
    }

    public void stop() {
        System.out.printf("MyVerticle (%s %s) is being undeployed\n", config().getString("name"), context.deploymentID());
    }
}
