package com.joaocsoliveira;

import io.vertx.core.Vertx;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class App {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting App!");

        AtomicInteger value = new AtomicInteger(10);
        AtomicBoolean increment_stopped = new AtomicBoolean(false);

        Vertx vertxA = Vertx.vertx();
        Vertx vertxB = Vertx.vertx();

        vertxA.setPeriodic(100, 1000, id -> {
            System.out.printf("vertexA Periodic1 decrement %d\n", value.decrementAndGet());
            if (value.get() <= 0 && increment_stopped.get()) {
                vertxA.close().onComplete(ar -> {
                    if (ar.succeeded()) {
                        System.out.println("Success closing vertxA");
                    } else {
                        System.out.println("Failure closing vertxA");
                    }
                });

            }
        });
        vertxA.setTimer(200, id -> {
            vertxA.setPeriodic(1000, id2 -> {
                System.out.printf("vertexA Periodic2 decrement %d\n", value.decrementAndGet());

                // force blocking event loop
                if (value.get() == 5) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        });

        vertxB.setPeriodic(1000, id -> {
            System.out.printf("vertexB Periodic increment %d\n", value.incrementAndGet());
            if (value.get() <= 0) {
                increment_stopped.set(true);
                vertxB.close().onComplete(ar -> {
                    if (ar.succeeded()) {
                        System.out.println("Success closing vertxB");
                    } else {
                        System.out.println("Failure closing vertxB");
                    }
                });
            }
        });

        System.out.println("Exiting App!");
    }
}
