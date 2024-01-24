package com.joaocsoliveira.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

import java.util.logging.Logger;

import static com.joaocsoliveira.Config.PRINTER_STRING_ADDRESS;

public class StringVerticle extends AbstractVerticle {
    private final Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public void start() {
        logger.info("StringVerticle is being deployed");

        EventBus eb = vertx.eventBus();
        eb.consumer(PRINTER_STRING_ADDRESS, message -> {
            String value = (String) message.body();
            message.reply(String.format("StringVerticle: '%s'", value));
        });
    }

    @Override
    public void stop() {
        logger.info("StringVerticle is being undeployed");
    }
}
