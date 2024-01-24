package com.joaocsoliveira.verticles;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.joaocsoliveira.Config.OUTPUT_PRINTER_ADDRESS;

public class PrinterVerticle extends AbstractVerticle {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void start() {
        String verticleName = config().getString("name");

        logger.log(Level.INFO, "PrinterVerticle ({0}) is being deployed\n", verticleName);

        EventBus eb = vertx.eventBus();
        eb.consumer(OUTPUT_PRINTER_ADDRESS, message ->
            logger.log(Level.INFO, "PrinterVerticle ({0}): {1}\n", new Object[]{verticleName, message.body()})
        );
    }

    @Override
    public void stop() {
        logger.log(Level.INFO, "PrinterVerticle ({0}) is being undeployed\n", config().getString("name"));
    }
}
