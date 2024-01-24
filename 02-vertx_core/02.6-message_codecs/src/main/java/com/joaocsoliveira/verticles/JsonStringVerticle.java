package com.joaocsoliveira.verticles;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;

import java.util.Map;
import java.util.logging.Logger;

import static com.joaocsoliveira.Config.PRINTER_JSON_STRING_ADDRESS;

public class JsonStringVerticle extends AbstractVerticle {
    private final Logger logger = Logger.getLogger(getClass().getName());
    private static final Gson gson = new Gson();

    @Override
    public void start() {
        logger.info("JsonStringVerticle is being deployed");

        EventBus eb = vertx.eventBus();
        eb.consumer(PRINTER_JSON_STRING_ADDRESS, message -> {
            try {
                String jsonString = (String) message.body();

                Map<String, String> jsonMap = gson.fromJson(jsonString, new TypeToken<>() {});
                message.reply(String.format("JsonStringVerticle: name '%s'", jsonMap.get("name")));
            } catch (JsonSyntaxException | ClassCastException ex) {
                message.fail(0, ex.getMessage());
            }
        });
    }

    @Override
    public void stop() {
        logger.info("JsonStringVerticle is being undeployed");
    }
}
