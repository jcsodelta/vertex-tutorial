package com.joaocsoliveira.verticles;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;

import static com.joaocsoliveira.Config.FACTORIAL_REQUEST_NAME;

public class AlwaysFailingVerticle extends AbstractVerticle {
    public void start() {
        System.out.println("AlwaysFailingVerticle is being deployed");

        EventBus eb = vertx.eventBus();
        eb.consumer(FACTORIAL_REQUEST_NAME, message -> {
            message.fail(99, "Won't do it");
        });
    }

    public void stop() {
        System.out.println("AlwaysFailingVerticle is being undeployed");
    }
}
