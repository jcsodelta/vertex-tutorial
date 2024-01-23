package com.joaocsoliveira;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static com.joaocsoliveira.Config.FACTORIAL_REQUEST_NAME;

public class App {
    private static final Vertx vertx = Vertx.vertx();
    private static final List<Integer> values = new ArrayList<>(IntStream.range(0, 20).boxed().toList());
    private static final DeliveryOptions delivery_options = new DeliveryOptions().setSendTimeout(5000);

    public static void main(String[] args) {
        System.out.println("Starting App::main!");

        Future.all(Arrays.asList(
                vertx.deployVerticle("com.joaocsoliveira.verticles.AlwaysSuccessfulVerticle"),
                vertx.deployVerticle("com.joaocsoliveira.verticles.AlwaysFailingVerticle"),
                vertx.deployVerticle("com.joaocsoliveira.verticles.CanTimeoutVerticle")
        )).onComplete(ar -> {
            if (ar.failed()) {
                System.out.println("failed deploying verticles");
            } else {
                send_requests();
            }
        });

        System.out.println("Exiting App::main!");
    }

    private static void send_requests() {
        System.out.printf("sending: %s\n", values);

        EventBus eb = vertx.eventBus();

        List<Integer> successful_values = new ArrayList<>();
        Future.join(values.stream().map(i ->
            eb.request(FACTORIAL_REQUEST_NAME, i, delivery_options)
                .onComplete(ar_request -> {
                    if (ar_request.failed()) {
                        System.out.printf("[%d] failed: %s\n", i, ar_request.cause());
                    } else {
                        successful_values.add(i);
                        System.out.printf("[%d] success: %s\n", i, ar_request.result().body());
                    }
                })
        ).toList()).onComplete(ar_finished -> {
            values.removeAll(successful_values);
            if (!values.isEmpty()) {
                send_requests();
            } else {
                vertx.close();
            }
        });
    }
}
