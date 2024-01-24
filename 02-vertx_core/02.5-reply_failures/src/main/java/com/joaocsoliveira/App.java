package com.joaocsoliveira;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import static com.joaocsoliveira.Config.FACTORIAL_REQUEST_NAME;

public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());
    private static final Vertx vertx = Vertx.vertx();
    private static final List<Integer> values = new ArrayList<>(IntStream.range(0, 20).boxed().toList());
    private static final DeliveryOptions delivery_options = new DeliveryOptions().setSendTimeout(5000);

    public static void main(String[] args) {
        logger.info("Starting App::main!");

        Future.all(Arrays.asList(
                vertx.deployVerticle("com.joaocsoliveira.verticles.AlwaysSuccessfulVerticle"),
                vertx.deployVerticle("com.joaocsoliveira.verticles.AlwaysFailingVerticle"),
                vertx.deployVerticle("com.joaocsoliveira.verticles.CanTimeoutVerticle")
        )).onComplete(ar -> {
            if (ar.failed()) {
                logger.info("failed deploying verticles");
            } else {
                sendRequests();
            }
        });

        logger.info("Exiting App::main!");
    }

    private static void sendRequests() {
        logger.log(Level.INFO, "sending: {0}", values);

        EventBus eb = vertx.eventBus();

        List<Integer> successfulValues = new ArrayList<>();
        Future.join(values.stream().map(i ->
                eb.request(FACTORIAL_REQUEST_NAME, i, delivery_options)
                        .onComplete(arRequest -> {
                            if (arRequest.failed()) {
                                logger.log(Level.WARNING, "[{0}] failed: {1}", new Object[]{i, arRequest.cause()});
                            } else {
                                successfulValues.add(i);
                                logger.log(Level.INFO, "[{0}] success: {1}", new Object[]{i, arRequest.result().body()});
                            }
                        })
        ).toList()).onComplete(arFinished -> {
            values.removeAll(successfulValues);
            if (!values.isEmpty()) {
                sendRequests();
            } else {
                vertx.close();
            }
        });
    }
}
