package com.joaocsoliveira.verticles;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;

import java.util.Map;

public class JsonStringVerticle extends AbstractVerticle {
    private static final Gson gson = new Gson();

    public void start() {
        System.out.println("JsonStringVerticle is being deployed");

        EventBus eb = vertx.eventBus();
        eb.consumer("printer.json_string", message -> {
            try {
                String json_string = (String) message.body();

                Map<String, String> json_map = gson.fromJson(json_string, new TypeToken<>() {});
                message.reply(String.format("JsonStringVerticle: name '%s'", json_map.get("name")));
            } catch (JsonSyntaxException | ClassCastException ex) {
                message.fail(0, ex.getMessage());
            }
        });
    }

    public void stop() {
        System.out.println("JsonStringVerticle is being undeployed");
    }
}
