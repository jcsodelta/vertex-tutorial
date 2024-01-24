package com.joaocsoliveira.verticles;

import io.vertx.core.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MyVerticle extends AbstractVerticle {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void start() {
        logger.log(Level.INFO, "MyVerticle ({0} {1}) is being deployed", new Object[]{config().getString("name"), context.deploymentID()});
    }

    @Override
    public void stop() {
        logger.log(Level.INFO, "MyVerticle ({0} {1}) is being undeployed", new Object[]{config().getString("name"), context.deploymentID()});
    }
}
