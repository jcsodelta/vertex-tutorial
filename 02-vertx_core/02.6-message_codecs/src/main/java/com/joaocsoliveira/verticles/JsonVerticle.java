package com.joaocsoliveira.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

import java.util.logging.Logger;

import static com.joaocsoliveira.Config.PRINTER_JSON_OBJECT_ADDRESS;

public class JsonVerticle extends AbstractVerticle {
    private final Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public void start() {
        logger.info("JsonVerticle is being deployed");

        EventBus eb = vertx.eventBus();
        eb.consumer(PRINTER_JSON_OBJECT_ADDRESS, message -> {
            JsonObject jsonObject = (JsonObject) message.body();
            message.reply(String.format("JsonVerticle: name '%s'", jsonObject.getString("name")));
        });
    }

    @Override
    public void stop() {
        logger.info("JsonVerticle is being undeployed");
    }
}
