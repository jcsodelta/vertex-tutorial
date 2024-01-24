package com.joaocsoliveira.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BlockingVerticle extends AbstractVerticle {
    private final Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public void start() {
        logger.info("BlockingVerticle is being deployed");

        EventBus eb = vertx.eventBus();
        eb.consumer("action.do_something", message -> {
            try {
                Thread.sleep(1000);
                logger.log(Level.INFO, "done {0}", message.body());
            } catch (InterruptedException e) {
                logger.warning("Thread.sleep failed...");
                Thread.currentThread().interrupt();
            }
        });
    }

    @Override
    public void stop() {
        logger.info("BlockingVerticle is being undeployed");
    }
}
