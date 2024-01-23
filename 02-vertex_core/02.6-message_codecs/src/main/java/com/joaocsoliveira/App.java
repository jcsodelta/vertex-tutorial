package com.joaocsoliveira;

import com.joaocsoliveira.codecs.DataCodec;
import com.joaocsoliveira.models.Data;
import com.joaocsoliveira.models.DataClusterSerializable;
import com.joaocsoliveira.models.DataSerializable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {

    public static void main(String[] args) {
        System.out.println("Starting App::main!");

        Vertx vertx = Vertx.vertx();
        DeliveryOptions delivery_options = new DeliveryOptions().setSendTimeout(5000);

        EventBus eb = vertx.eventBus();
        eb.registerDefaultCodec(Data.class, new DataCodec());

        Future.all(Arrays.asList(
                vertx.deployVerticle("com.joaocsoliveira.verticles.StringVerticle"),
                vertx.deployVerticle("com.joaocsoliveira.verticles.JsonStringVerticle"),
                vertx.deployVerticle("com.joaocsoliveira.verticles.JsonVerticle"),
                vertx.deployVerticle("com.joaocsoliveira.verticles.DataVerticle")
        )).onComplete(ar_deployment -> {
            if (ar_deployment.failed()) {
                System.out.println("failed deploying verticles");
            } else {
                // string
                String normal_string = "Some normal string";
                String json_string = "{ \"name\": \"Some Json String\" }";

                // json
                JsonObject json_object = new JsonObject().put("name", "Some JsonObject");

                // ClusterSerializable
                DataClusterSerializable data_cluster_serializable = new DataClusterSerializable("Some data ClusterSerializable");

                // serializable
                DataSerializable data_serializable = new DataSerializable("Some data Serializable");

                // pojo
                Data data = new Data("Some data");

                //////////////////
                // Requests

                List<Future<Message<Object>>> responses = new ArrayList<>();
                responses.add(eb.request("printer.string", normal_string, delivery_options).onComplete(ar -> {
                    print("printer.string", ar);
                }));
                responses.add(eb.request("printer.string", json_string, delivery_options).onComplete(ar -> {
                    print("printer.string", ar);
                }));

                responses.add(eb.request("printer.json_string", normal_string, delivery_options).onComplete(ar -> {
                    print("printer.json_string", ar);
                }));
                responses.add(eb.request("printer.json_string", json_string, delivery_options).onComplete(ar -> {
                    print("printer.json_string", ar);
                }));
                responses.add(eb.request("printer.json_string", json_object, delivery_options).onComplete(ar -> {
                    print("printer.json_string", ar);
                }));

                responses.add(eb.request("printer.json_object", json_object, delivery_options).onComplete(ar -> {
                    print("printer.json_object", ar);
                }));

                responses.add(eb.request("printer.data", data, delivery_options).onComplete(ar -> {
                    print("printer.data", ar);
                }));
                responses.add(eb.request("printer.data", data_cluster_serializable, delivery_options).onComplete(ar -> {
                    print("printer.data", ar);
                }));
                responses.add(eb.request("printer.data", data_serializable, delivery_options).onComplete(ar -> {
                    print("printer.data", ar);
                }));

                Future.join(responses)
                    .onComplete(ar -> {
                        System.out.println("Calling vertx.close()");
                        vertx.close();
                    });
            }
        });

        System.out.println("Exiting App::main!");
    }

    private static void print(String destination, AsyncResult<Message<Object>> ar) {
        Object result = ar.result() == null ? null : ar.result().body();
        System.out.printf("\t%s | response: %s | success: %s | %s\n", destination, result, ar.succeeded(), ar.cause());
    }
}
