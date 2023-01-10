package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.constants.DefaultValues;
import bif3.tolan.swe1.mcg.constants.GenericHttpResponses;
import bif3.tolan.swe1.mcg.constants.RequestHeaders;
import bif3.tolan.swe1.mcg.constants.RequestPaths;
import bif3.tolan.swe1.mcg.exceptions.UnsupportedCardTypeException;
import bif3.tolan.swe1.mcg.exceptions.UnsupportedElementTypeException;
import bif3.tolan.swe1.mcg.exceptions.UserDoesNotExistException;
import bif3.tolan.swe1.mcg.httpserver.HttpRequest;
import bif3.tolan.swe1.mcg.httpserver.HttpResponse;
import bif3.tolan.swe1.mcg.httpserver.enums.HttpContentType;
import bif3.tolan.swe1.mcg.httpserver.enums.HttpMethod;
import bif3.tolan.swe1.mcg.httpserver.enums.HttpStatus;
import bif3.tolan.swe1.mcg.json.BattleViews;
import bif3.tolan.swe1.mcg.model.Battle;
import bif3.tolan.swe1.mcg.model.User;
import bif3.tolan.swe1.mcg.persistence.respositories.interfaces.CardRepository;
import bif3.tolan.swe1.mcg.persistence.respositories.interfaces.DeckRepository;
import bif3.tolan.swe1.mcg.persistence.respositories.interfaces.UserRepository;
import bif3.tolan.swe1.mcg.utils.EloUtils;
import bif3.tolan.swe1.mcg.utils.UserUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;

public class BattleWorker implements Workable {
    private UserRepository userRepository;
    private CardRepository cardRepository;
    private DeckRepository deckRepository;

    private User waitingForBattle;
    private Battle battle;

    public BattleWorker(UserRepository userRepository, CardRepository cardRepository, DeckRepository deckRepository) {
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
        this.battle = null;
    }

    @Override
    public HttpResponse executeRequest(HttpRequest request) {
        String requestedPath = "";
        HttpMethod httpMethod = request.getMethod();

        if (request.getPathArray().length > 1) {
            requestedPath = request.getPathArray()[1];
        }

        switch (httpMethod) {
            case POST:
                switch (requestedPath) {
                    case RequestPaths.BATTLE_WORKER_BATTLE:
                        return battle(request);
                }
        }

        return GenericHttpResponses.INVALID_PATH;
    }

    private HttpResponse battle(HttpRequest request) {
        // get username from token
        String authorizationToken = request.getHeaderMap().get(RequestHeaders.AUTH_HEADER);
        String username = UserUtils.extractUsernameFromToken(authorizationToken);

        try {
            User requestingUser = userRepository.getUserByUsername(username);
            // check if user exists

            int deckIdOfRequestingUser = deckRepository.getDeckIdByUserId(requestingUser.getId());
            requestingUser.setDeck(cardRepository.getAllCardsByDeckIdAsMap(deckIdOfRequestingUser));
            // check deck validity
            if (requestingUser.getDeck().size() == 4) {
                if (waitingForBattle == null) {
                    // if there is no waiting user, create a lobby
                    return createLobbyAndWaitForOpponent(requestingUser);
                } else {
                    // if there is a waiting user, battle them
                    return joinBattleAndBattle(requestingUser);
                }
            } else {
                return GenericHttpResponses.INVALID_DECK;
            }
        } catch (InterruptedException | CloneNotSupportedException | UnsupportedCardTypeException |
                 UnsupportedElementTypeException | SQLException | JsonProcessingException e) {
            e.printStackTrace();
            return GenericHttpResponses.INTERNAL_ERROR;
        } catch (UserDoesNotExistException e) {
            return GenericHttpResponses.INVALID_TOKEN;
        }
    }

    private synchronized HttpResponse joinBattleAndBattle(User requestingUser) throws SQLException, CloneNotSupportedException, JsonProcessingException {
        // check to not play against yourself
        if (waitingForBattle != requestingUser) {
            // create a battle
            battle = new Battle((User) waitingForBattle.clone(), (User) requestingUser.clone());

            // no one is waiting for a battle anymore
            waitingForBattle = null;

            // battle
            while (battle.getGameFinished() == false) {
                battle.nextRound();
            }

            // calculate new elo
            EloUtils.NewEloValues newEloValues;
            User winner = battle.getWinner();
            User loser = battle.getLoser();
            boolean draw = battle.getIsDraw();

            // winner and loser assignment is irrelevant when it is a draw
            newEloValues = EloUtils.calculateNewElo(
                    winner.getElo(),
                    loser.getElo(),
                    winner.getGamesPlayed(),
                    loser.getGamesPlayed(),
                    draw);

            winner.setElo(newEloValues.winnerElo());
            loser.setElo(newEloValues.loserElo());

            // increase win count for winner if it is no draw
            if (draw == false) {
                winner.setWins(winner.getWins() + 1);
            }

            // increase games played for both users
            winner.setGamesPlayed(winner.getGamesPlayed() + 1);
            loser.setGamesPlayed(loser.getGamesPlayed() + 1);

            // update users
            userRepository.updateUser(winner);
            userRepository.updateUser(loser);

            // convert battle results to json
            String jsonString = getBattleAsJsonString();

            // notify other player that the game finished
            this.notify();

            // return result
            return new HttpResponse(HttpStatus.OK, HttpContentType.JSON, jsonString);
        } else {
            return GenericHttpResponses.IDENTICAL_USER;
        }
    }

    private synchronized HttpResponse createLobbyAndWaitForOpponent(User requestingUser) throws InterruptedException, CloneNotSupportedException, JsonProcessingException {
        // set user as waiting
        waitingForBattle = (User) requestingUser.clone();

        // wait for confirmation that another player has been found and battle commenced
        this.wait(DefaultValues.BATTLE_TIMEOUT);

        if (battle == null) {
            // no opponent found in timeout window
            waitingForBattle = null;
            return GenericHttpResponses.BATTLE_REQUEST_TIMEOUT;
        } else {
            // Get battle results and convert to json
            String jsonString = getBattleAsJsonString();

            // reset battle
            battle = null;

            return new HttpResponse(HttpStatus.OK, HttpContentType.JSON, jsonString);
        }
    }

    private String getBattleAsJsonString() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper
                .writerWithView(BattleViews.ReadBattle.class)
                .writeValueAsString(battle);
        return jsonString;
    }
}
