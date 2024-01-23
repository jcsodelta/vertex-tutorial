package com.joaocsoliveira.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class JudgeVerticle extends AbstractVerticle {
    private final Map<UUID, Integer> players_map = new HashMap<>();
    private String game_id;
    private int current_round = 0;

    public void start() {
        game_id = config().getString("game_id");

        System.out.printf("JudgeVerticle (%s) is being deployed\n", game_id);

        players_map.put(UUID.fromString(config().getString("player1_id")), 0);
        players_map.put(UUID.fromString(config().getString("player2_id")), 0);

        EventBus eb = vertx.eventBus();
        eb.consumer(String.format("game.%s", game_id), message -> {
            String text = (String) message.body();
            switch (text) {
                case "start": {
                    eb.send("output.printer", "game starting");
                    run_turn();
                    break;
                }
                default: {
                    eb.send("output.printer", String.format("game received unknown instruction: %s", text));
                }
            }
        });
    }

    private void run_turn() {
        EventBus eb = vertx.eventBus();

        ++current_round;
        eb.send("output.printer", String.format("\tcurrent round: %s", current_round));

        var responses = players_map.keySet().stream().map(player_id ->
            eb.request(String.format("game.%s.turn.%s", game_id, player_id), "")
        ).collect(Collectors.toList());

        Future.all(responses).onComplete(ar -> {
            if (ar.failed()) {
                eb.send("output.printer", String.format("game error: %s", ar.cause()));
                eb.send(String.format("game.%s.stop", game_id), "");
                return;
            }

            var winner_response = responses.stream().reduce(null, (previous_response, response) -> {
                if (previous_response == null) {
                    return response;
                }

                Integer previous_value = (Integer) previous_response.result().body();
                Integer current_value = (Integer) response.result().body();

                if (previous_value > current_value) {
                    return previous_response;
                } else if (previous_value < current_value) {
                    return response;
                } else {
                    return null;
                }
            });

            if (winner_response != null) {
                UUID player_id = UUID.fromString(winner_response.result().headers().get("player_id"));
                int new_ponctuation = players_map.get(player_id) + 1;

                if (new_ponctuation >= 5) {
                    eb.send("output.printer", String.format("game winner: %s", player_id));
                    eb.send(String.format("game.%s.stop", game_id), "");
                    return;
                }

                players_map.put(player_id, new_ponctuation);
            }

            run_turn();
        });
    }

    public void stop() {
        System.out.printf("JudgeVerticle (%s) is being undeployed\n", game_id);
    }
}
