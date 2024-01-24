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
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.joaocsoliveira.Config.*;

public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        logger.info("Starting App::main!");

        Vertx vertx = Vertx.vertx();
        DeliveryOptions deliveryOptions = new DeliveryOptions().setSendTimeout(5000);

        EventBus eb = vertx.eventBus();
        eb.registerDefaultCodec(Data.class, new DataCodec());

        Future.all(Arrays.asList(
                vertx.deployVerticle("com.joaocsoliveira.verticles.StringVerticle"),
                vertx.deployVerticle("com.joaocsoliveira.verticles.JsonStringVerticle"),
                vertx.deployVerticle("com.joaocsoliveira.verticles.JsonVerticle"),
                vertx.deployVerticle("com.joaocsoliveira.verticles.DataVerticle")
        )).onComplete(arDeployment -> {
            if (arDeployment.failed()) {
                logger.info("failed deploying verticles");
            } else {
                // string
                String normalString = "Some normal string";
                String jsonString = "{ \"name\": \"Some Json String\" }";

                // json
                JsonObject jsonObject = new JsonObject().put("name", "Some JsonObject");

                // ClusterSerializable
                DataClusterSerializable dataClusterSerializable = new DataClusterSerializable("Some data ClusterSerializable");

                // serializable
                DataSerializable dataSerializable = new DataSerializable("Some data Serializable");

                // pojo
                Data data = new Data("Some data");

                //////////////////
                // Requests

                List<Future<Message<Object>>> responses = new ArrayList<>();
                responses.add(eb.request(PRINTER_STRING_ADDRESS, normalString, deliveryOptions).onComplete(ar -> 
                    print(PRINTER_STRING_ADDRESS, ar)
                ));
                responses.add(eb.request(PRINTER_STRING_ADDRESS, jsonString, deliveryOptions).onComplete(ar -> 
                    print(PRINTER_STRING_ADDRESS, ar)
                ));

                responses.add(eb.request(PRINTER_JSON_STRING_ADDRESS, normalString, deliveryOptions).onComplete(ar -> 
                    print(PRINTER_JSON_STRING_ADDRESS, ar)
                ));
                responses.add(eb.request(PRINTER_JSON_STRING_ADDRESS, jsonString, deliveryOptions).onComplete(ar -> 
                    print(PRINTER_JSON_STRING_ADDRESS, ar)
                ));
                responses.add(eb.request(PRINTER_JSON_STRING_ADDRESS, jsonObject, deliveryOptions).onComplete(ar -> 
                    print(PRINTER_JSON_STRING_ADDRESS, ar)
                ));

                responses.add(eb.request(PRINTER_JSON_OBJECT_ADDRESS, jsonObject, deliveryOptions).onComplete(ar -> 
                    print(PRINTER_JSON_OBJECT_ADDRESS, ar)
                ));

                responses.add(eb.request(PRINTER_DATA_ADDRESS, data, deliveryOptions).onComplete(ar -> 
                    print(PRINTER_DATA_ADDRESS, ar)
                ));
                responses.add(eb.request(PRINTER_DATA_ADDRESS, dataClusterSerializable, deliveryOptions).onComplete(ar -> 
                    print(PRINTER_DATA_ADDRESS, ar)
                ));
                responses.add(eb.request(PRINTER_DATA_ADDRESS, dataSerializable, deliveryOptions).onComplete(ar -> 
                    print(PRINTER_DATA_ADDRESS, ar)
                ));

                Future.join(responses)
                    .onComplete(ar -> {
                        logger.info("Calling vertx.close()");
                        vertx.close();
                    });
            }
        });

        logger.info("Exiting App::main!");
    }

    private static void print(String destination, AsyncResult<Message<Object>> ar) {
        Object result = ar.result() == null ? null : ar.result().body();
        logger.log(Level.INFO, "\t{0} | response: {1} | success: {2} | {3}", new Object[]{destination, result, ar.succeeded(), ar.cause()});
    }
}
