package com.joaocsoliveira.verticles;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;

public class PrinterVerticle extends AbstractVerticle {
    public void start() {
        String verticle_name = config().getString("name");

        System.out.printf("PrinterVerticle (%s) is being deployed\n", verticle_name);

        EventBus eb = vertx.eventBus();
        eb.consumer("output.printer", message -> {
            System.out.printf("PrinterVerticle (%s) : %s\n", verticle_name, message.body());
        });
    }

    public void stop() {
        System.out.printf("PrinterVerticle (%s) is being undeployed\n", config().getString("name"));
    }
}
