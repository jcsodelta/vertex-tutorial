package com.joaocsoliveira;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;

public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());
    private static final String PRODUCER_NAME = "producer";
    private static final String CONSUMER_NAME = "consumer";

    public static void main(String[] args) {
        logger.info("Starting App::main!");

        VertxOptions options = new VertxOptions();

        Vertx.clusteredVertx(options)
            .onComplete(arCreation -> {
                if (arCreation.failed()) {
                    logger.log(Level.INFO, "Failed instantiating clustered vertx: {0}", arCreation.cause().toString());
                    System.exit(1);
                }

                Vertx vertx = arCreation.result();

                EventBus eb = vertx.eventBus();

                boolean consumer;
                int instances;
                if (args.length == 1) {
                    if (PRODUCER_NAME.equals(args[0])){
                        consumer = false;
                        instances = 1;
                    } else {
                        consumer = true;
                        instances = parseInt(args[0]);
                    }
                } else {
                    consumer = true;
                    instances = 1;
                }
                String name = consumer ? CONSUMER_NAME : PRODUCER_NAME;
                String params = String.join(" ", args);
                logger.log(Level.INFO, "initializing {0} args = \"{1}\"", new String[]{name, params});

                if (consumer) {
                    logger.info("starting worker");
                    vertx.deployVerticle("com.joaocsoliveira.verticles.BlockingVerticle", new DeploymentOptions().setInstances(instances));
                    eb.consumer("action.finished", message ->
                        vertx.close()
                    );
                } else {
                    logger.info("starting producer");
                    Future.join(IntStream.range(0, 10).boxed().toList()
                        .stream().map(order -> {
                            logger.log(Level.INFO, "sending {0}", order);
                            return eb.request("action.do_something", order);
                        }).toList())
                    .onComplete(ar -> {
                        logger.info("Calling vertx.close()");
                        eb.publish("action.finished", "");
                        vertx.close();
                    });
                }
            });

        logger.info("Exiting App::main!");
    }
}
