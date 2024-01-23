package com.joaocsoliveira.verticles;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;

import static com.joaocsoliveira.Config.FACTORIAL_REQUEST_NAME;

public class CanTimeoutVerticle extends AbstractVerticle {
    public void start() {
        System.out.println("CanTimeoutVerticle is being deployed");

        EventBus eb = vertx.eventBus();
        eb.consumer(FACTORIAL_REQUEST_NAME, message -> {
            Integer value = (Integer) message.body();

            // throws exception if value is 0
            vertx.setTimer(value * 1000, id -> {
                int result = 1;
                for (int i = 1; i <= value; i++) {
                    result *= i;
                }

                message.reply(result);
            });
        });
    }

    public void stop() {
        System.out.println("CanTimeoutVerticle is being undeployed");
    }
}
