package com.joaocsoliveira.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

public class BlockingVerticle extends AbstractVerticle {

    public void start() {
        System.out.println("BlockingVerticle is being deployed");

        EventBus eb = vertx.eventBus();
        eb.consumer("action.do_something", message -> {
            try {
                Thread.sleep(1000);
                System.out.printf("done %s\n", message.body());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void stop() {
        System.out.println("BlockingVerticle is being undeployed");
    }
}
