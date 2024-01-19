package com.joaocsoliveira;

import io.vertx.core.Vertx;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class App {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting App!");

        AtomicInteger value = new AtomicInteger(10);
        AtomicBoolean increment_stopped = new AtomicBoolean(false);
        AtomicBoolean decrement_1_stopped = new AtomicBoolean(false);

        Vertx vertxA = Vertx.vertx();
        Vertx vertxB = Vertx.vertx();

        vertxA.setPeriodic(100, 1000, id -> {
            System.out.printf("vertexA Periodic1 decrement %d\n", value.decrementAndGet());
            if (value.get() <= 0 && increment_stopped.get()) {
                vertxA.cancelTimer(id);
                vertxA.close();
                decrement_1_stopped.set(true);
            }
        });
        vertxA.setTimer(200, id -> {
            vertxA.setPeriodic(1000, id2 -> {
                System.out.printf("vertexA Periodic2 decrement %d\n", value.decrementAndGet());
                if (decrement_1_stopped.get()) {
                    vertxA.cancelTimer(id2);
                    vertxA.close();
                }
            });
        });

        vertxB.setPeriodic(1000, id -> {
            System.out.printf("vertexB Periodic increment %d\n", value.incrementAndGet());
            if (value.get() <= 0) {
                vertxB.cancelTimer(id);
                increment_stopped.set(true);
                vertxB.close();
            }
        });

        System.out.println("Exiting App!");
    }
}
