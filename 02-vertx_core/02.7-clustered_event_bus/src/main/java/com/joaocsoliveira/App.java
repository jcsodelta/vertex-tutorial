package com.joaocsoliveira;

import com.hazelcast.core.HazelcastInstance;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;

import java.util.Arrays;
import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;

public class App {

    public static void main(String[] args) {
        System.out.println("Starting App::main!");

        VertxOptions options = new VertxOptions();

        Vertx.clusteredVertx(options)
            .onComplete(ar_creation -> {
                if (ar_creation.failed()) {
                    System.out.printf("Failed instantiating clustered vertx: %s\n", ar_creation.cause());
                    System.exit(1);
                }

                Vertx vertx = ar_creation.result();

                EventBus eb = vertx.eventBus();

                boolean worker;
                int instances;
                if (args.length == 1 && args[0].equals("producer")) {
                    worker = false;
                    instances = 1;
                } else if (args.length == 1 && !args[0].equals("producer")) {
                    worker = true;
                    instances = parseInt(args[0]);
                } else {
                    worker = true;
                    instances = 1;
                }
                System.out.printf("initializing %s  args = '%s'\n", worker ? "worker" : "producer", String.join(" ", args));

                if (worker) {
                    System.out.println("starting worker");
                    vertx.deployVerticle("com.joaocsoliveira.verticles.BlockingVerticle", new DeploymentOptions().setInstances(instances));
                    eb.consumer("action.finished", message -> {
                        vertx.close();
                    });
                } else {
                    System.out.println("starting producer");
                    Future.join(IntStream.range(0, 10).boxed().toList()
                        .stream().map(order -> {
                            System.out.printf("sending %s\n", order);
                            return eb.request("action.do_something", order);
                        }).toList())
                    .onComplete(ar -> {
                        System.out.println("Calling vertx.close()");
                        eb.publish("action.finished", "");
                        vertx.close();
                    });
                }
            });

        System.out.println("Exiting App::main!");
    }
}
