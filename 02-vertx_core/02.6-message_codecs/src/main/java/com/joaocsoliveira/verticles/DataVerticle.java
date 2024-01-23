package com.joaocsoliveira.verticles;

import com.joaocsoliveira.models.Data;
import com.joaocsoliveira.models.DataSerializable;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

public class DataVerticle extends AbstractVerticle {

    public void start() {
        System.out.println("DataVerticle is being deployed");

        EventBus eb = vertx.eventBus();
        eb.consumer("printer.data", message -> {
            String name = switch (message.body()) {
                case Data data -> data.getName();
                case DataSerializable data -> data.getName();
                default -> throw new IllegalStateException("Unexpected value: " + message.body());
            };
            message.reply(String.format("DataVerticle: name '%s'", name));
        });
    }

    public void stop() {
        System.out.println("DataVerticle is being undeployed");
    }
}
