package com.joaocsoliveira.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.joaocsoliveira.Config.*;

public class JudgeVerticle extends AbstractVerticle {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Map<UUID, Integer> playersMap = new HashMap<>();
    private String gameId;
    private int currentRound = 0;

    @Override
    public void start() {
        gameId = config().getString(GAME_ID_ATTRIBUTE_NAME);

        logger.log(Level.INFO, "JudgeVerticle ({0}) is being deployed", gameId);

        playersMap.put(UUID.fromString(config().getString(PLAYER_1_ID_ATTRIBUTE_NAME)), 0);
        playersMap.put(UUID.fromString(config().getString(PLAYER_2_ID_ATTRIBUTE_NAME)), 0);

        EventBus eb = vertx.eventBus();
        eb.consumer(String.format("game.%s", gameId), message -> {
            String text = (String) message.body();
            if (text.equals("start")) {
                eb.send(OUTPUT_PRINTER_ADDRESS, "game starting");
                runTurn();
            } else {
                eb.send(OUTPUT_PRINTER_ADDRESS, String.format("game received unknown instruction: %s", text));
            }
        });
    }

    private void runTurn() {
        EventBus eb = vertx.eventBus();

        ++currentRound;
        eb.send(OUTPUT_PRINTER_ADDRESS, String.format("\tcurrent round: %s", currentRound));

        var responses = playersMap.keySet().stream().map(playerId ->
            eb.request(String.format("game.%s.turn.%s", gameId, playerId), "")
        ).toList();

        Future.all(responses).onComplete(ar -> {
            if (ar.failed()) {
                eb.send(OUTPUT_PRINTER_ADDRESS, String.format("game error: %s", ar.cause()));
                eb.send(String.format("game.%s.stop", gameId), "");
                return;
            }

            var winnerResponse = responses.stream().reduce(null, (previousResponse, response) -> {
                if (previousResponse == null) {
                    return response;
                }

                Integer previousValue = (Integer) previousResponse.result().body();
                Integer currentValue = (Integer) response.result().body();

                if (previousValue > currentValue) {
                    return previousResponse;
                } else if (previousValue < currentValue) {
                    return response;
                } else {
                    return null;
                }
            });

            if (winnerResponse != null) {
                UUID playerId = UUID.fromString(winnerResponse.result().headers().get("player_id"));
                int newPunctuation = playersMap.get(playerId) + 1;

                if (newPunctuation >= 5) {
                    eb.send(OUTPUT_PRINTER_ADDRESS, String.format("game winner: %s", playerId));
                    eb.send(String.format("game.%s.stop", gameId), "");
                    return;
                }

                playersMap.put(playerId, newPunctuation);
            }

            runTurn();
        });
    }

    @Override
    public void stop() {
        logger.log(Level.INFO, "JudgeVerticle ({0}) is being undeployed\n", gameId);
    }
}
