package com.joaocsoliveira;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());
    private static final Vertx vertxA = Vertx.vertx();

    public static void main(String[] args) {
        logger.info("Starting App::main!");

        runWithParameters(getGoodFutures());
        runWithParameters(getSomeBadFutures());

        logger.info("Exiting App::main!");
    }

    private static void runWithParameters(List<Future<Integer>> futures) {
        Future.join(Arrays.asList(
                Future.all(futures).onComplete(ar -> {
                    if (ar.succeeded()) {
                        logger.info("Future.all good: success");
                    } else {
                        logger.info("Future.all good: failure");
                    }
                }),
                Future.any(futures).onComplete(ar -> {
                    if (ar.succeeded()) {
                        logger.info("Future.any good: success");
                    } else {
                        logger.info("Future.any good: failure");
                    }
                }),
                Future.join(futures).onComplete(ar -> {
                    if (ar.succeeded()) {
                        logger.info("Future.join good: success");
                    } else {
                        logger.info("Future.join good: failure");
                    }
                }))
        ).onComplete(ar -> vertxA.close());
    }

    private static List<Future<Integer>> getGoodFutures() {
        Promise<Integer> p1 = Promise.promise();
        vertxA.setTimer(1000, id -> {
            logger.info("good future 1");
            p1.complete(1);
        });

        Promise<Integer> p2 = Promise.promise();
        vertxA.setTimer(2000, id -> {
            logger.info("good future 2");
            p2.complete(2);
        });

        Promise<Integer> p3 = Promise.promise();
        vertxA.setTimer(3000, id -> {
            logger.info("good future 3");
            p3.complete(3);
        });

        return Arrays.asList(p1.future(), p2.future(), p3.future());
    }

    static List<Future<Integer>> getSomeBadFutures() {
        Promise<Integer> p1 = Promise.promise();
        vertxA.setTimer(1000, id -> {
            logger.info("good future 1");
            p1.complete(1);
        });

        Promise<Integer> p2 = Promise.promise();
        vertxA.setTimer(2000, id -> {
            logger.info("bad future 2");
            p2.fail("bad 2");
        });

        Promise<Integer> p3 = Promise.promise();
        vertxA.setTimer(3000, id -> {
            logger.info("good future 3");
            p3.complete(3);
        });

        return Arrays.asList(p1.future(), p2.future(), p3.future());
    }
}
