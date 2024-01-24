package com.joaocsoliveira.verticles;

import com.joaocsoliveira.models.Data;
import com.joaocsoliveira.models.DataSerializable;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

import java.util.logging.Logger;

import static com.joaocsoliveira.Config.PRINTER_DATA_ADDRESS;

public class DataVerticle extends AbstractVerticle {
    private final Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public void start() {
        logger.info("DataVerticle is being deployed");

        EventBus eb = vertx.eventBus();
        eb.consumer(PRINTER_DATA_ADDRESS, message -> {
            String name = switch (message.body()) {
                case Data data -> data.getName();
                case DataSerializable data -> data.getName();
                default -> throw new IllegalStateException("Unexpected value: " + message.body());
            };
            message.reply(String.format("DataVerticle: name '%s'", name));
        });
    }

    @Override
    public void stop() {
        logger.info("DataVerticle is being undeployed");
    }
}
