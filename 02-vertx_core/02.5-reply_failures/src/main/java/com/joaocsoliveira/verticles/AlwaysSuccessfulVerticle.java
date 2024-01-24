package com.joaocsoliveira.verticles;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;

import java.util.logging.Logger;

import static com.joaocsoliveira.Config.FACTORIAL_REQUEST_NAME;

public class AlwaysSuccessfulVerticle extends AbstractVerticle {
    private final Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public void start() {
        logger.info("AlwaysSuccessfulVerticle is being deployed");

        EventBus eb = vertx.eventBus();
        eb.consumer(FACTORIAL_REQUEST_NAME, message -> {
            Integer value = (Integer) message.body();

            int result = 1;
            for (int i = 1; i <= value; i++) {
                result *= i;
            }

            message.reply(result);
        });
    }

    @Override
    public void stop() {
        logger.info("AlwaysSuccessfulVerticle is being undeployed");
    }
}
