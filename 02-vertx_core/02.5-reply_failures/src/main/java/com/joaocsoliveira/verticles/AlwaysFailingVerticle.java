package com.joaocsoliveira.verticles;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;

import java.util.logging.Logger;

import static com.joaocsoliveira.Config.FACTORIAL_REQUEST_NAME;

public class AlwaysFailingVerticle extends AbstractVerticle {
    private final Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public void start() {
        logger.info("AlwaysFailingVerticle is being deployed");

        EventBus eb = vertx.eventBus();
        eb.consumer(FACTORIAL_REQUEST_NAME, message ->
                message.fail(99, "Won't do it")
        );
    }

    @Override
    public void stop() {
        logger.info("AlwaysFailingVerticle is being undeployed");
    }
}
