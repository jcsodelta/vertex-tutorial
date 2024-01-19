package com.joaocsoliveira;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class App {
    private static final Vertx vertxA = Vertx.vertx();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting App::main!");

        Future.join(Arrays.asList(
                Future.all(get_some_bad_futures()).onComplete(ar -> {
                    if (ar.succeeded()) {
                        System.out.println("Future.all good: success");
                    } else {
                        System.out.println("Future.all good: failure");
                    }
                }),
                Future.any(get_some_bad_futures()).onComplete(ar -> {
                    if (ar.succeeded()) {
                        System.out.println("Future.any good: success");
                    } else {
                        System.out.println("Future.any good: failure");
                    }
                }),
                Future.join(get_some_bad_futures()).onComplete(ar -> {
                    if (ar.succeeded()) {
                        System.out.println("Future.join good: success");
                    } else {
                        System.out.println("Future.join good: failure");
                    }
                }))
        ).onComplete(ar -> {
            vertxA.close();
        });

        System.out.println("Exiting App::main!");
    }

    private static List<Future<Integer>> get_good_futures() {
        Promise<Integer> p1 = Promise.promise();
        vertxA.setTimer(1000, id -> {
            System.out.println("good future 1");
            p1.complete(1);
        });

        Promise<Integer> p2 = Promise.promise();
        vertxA.setTimer(2000, id -> {
            System.out.println("good future 2");
            p2.complete(2);
        });

        Promise<Integer> p3 = Promise.promise();
        vertxA.setTimer(3000, id -> {
            System.out.println("good future 3");
            p3.complete(3);
        });

        return Arrays.asList(p1.future(), p2.future(), p3.future());
    }

    static List<Future<Integer>> get_some_bad_futures() {
        Promise<Integer> p1 = Promise.promise();
        vertxA.setTimer(1000, id -> {
            System.out.println("good future 1");
            p1.complete(1);
        });

        Promise<Integer> p2 = Promise.promise();
        vertxA.setTimer(2000, id -> {
            System.out.println("bad future 2");
            p2.fail("bad 2");
        });

        Promise<Integer> p3 = Promise.promise();
        vertxA.setTimer(3000, id -> {
            System.out.println("good future 3");
            p3.complete(3);
        });

        return Arrays.asList(p1.future(), p2.future(), p3.future());
    }
}
