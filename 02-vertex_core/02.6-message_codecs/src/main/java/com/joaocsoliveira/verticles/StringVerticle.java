package com.joaocsoliveira.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

public class StringVerticle extends AbstractVerticle {
    public void start() {
        System.out.println("StringVerticle is being deployed");

        EventBus eb = vertx.eventBus();
        eb.consumer("printer.string", message -> {
            String value = (String) message.body();
            message.reply(String.format("StringVerticle: '%s'", value));
        });
    }

    public void stop() {
        System.out.println("StringVerticle is being undeployed");
    }
}
