package com.joaocsoliveira;

import io.vertx.core.Vertx;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());
    private static final AtomicBoolean incrementStopped = new AtomicBoolean(false);
    private static final AtomicInteger value = new AtomicInteger(10);

    public static void main(String[] args) {
        logger.info("Starting App::main!");

        runVertexA();
        runVertexB();

        logger.info("Exiting App::main!");
    }

    private static void runVertexA() {
        Vertx vertxA = Vertx.vertx();

        vertxA.setPeriodic(100, 1000, id -> {
            logger.info(String.format("vertxA Periodic1 decrement %d", value.decrementAndGet()));
            if (value.get() <= 0 && incrementStopped.get()) {
                vertxA.close().onComplete(ar -> {
                    if (ar.succeeded()) {
                        logger.info("Success closing vertxA");
                    } else {
                        logger.info("Failure closing vertxA");
                    }
                });

            }
        });

        vertxA.setTimer(200, id ->
                vertxA.setPeriodic(1000, id2 -> {
                    logger.info(String.format("vertxA Periodic2 decrement %d", value.decrementAndGet()));

                    // force blocking event loop
                    if (value.get() == 5) {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            logger.warning("vertxA Thread.sleep failed...");
                            Thread.currentThread().interrupt();
                        }
                    }
                })
        );
    }

    private static void runVertexB() {
        Vertx vertxB = Vertx.vertx();
        vertxB.setPeriodic(1000, id -> {
            logger.info(String.format("vertxB Periodic increment %d", value.incrementAndGet()));
            if (value.get() <= 0) {
                incrementStopped.set(true);
                vertxB.close().onComplete(ar -> {
                    if (ar.succeeded()) {
                        logger.info("Success closing vertxB");
                    } else {
                        logger.info("Failure closing vertxB");
                    }
                });
            }
        });
    }
}
