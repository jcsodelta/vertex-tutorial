package com.joaocsoliveira.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

public class JsonVerticle extends AbstractVerticle {

    public void start() {
        System.out.println("JsonVerticle is being deployed");

        EventBus eb = vertx.eventBus();
        eb.consumer("printer.json_object", message -> {
            JsonObject json_object = (JsonObject) message.body();
            message.reply(String.format("JsonVerticle: name '%s'", json_object.getString("name")));
        });
    }

    public void stop() {
        System.out.println("JsonVerticle is being undeployed");
    }
}
