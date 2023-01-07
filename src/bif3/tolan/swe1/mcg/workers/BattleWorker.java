package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.constants.DefaultValues;
import bif3.tolan.swe1.mcg.constants.Headers;
import bif3.tolan.swe1.mcg.constants.Paths;
import bif3.tolan.swe1.mcg.database.respositories.CardRepository;
import bif3.tolan.swe1.mcg.database.respositories.DeckRepository;
import bif3.tolan.swe1.mcg.database.respositories.UserRepository;
import bif3.tolan.swe1.mcg.exceptions.BattleFinishedException;
import bif3.tolan.swe1.mcg.exceptions.InvalidCardParameterException;
import bif3.tolan.swe1.mcg.exceptions.InvalidDeckException;
import bif3.tolan.swe1.mcg.exceptions.InvalidUserException;
import bif3.tolan.swe1.mcg.httpserver.*;
import bif3.tolan.swe1.mcg.model.Battle;
import bif3.tolan.swe1.mcg.model.User;
import bif3.tolan.swe1.mcg.utils.EloUtils;
import bif3.tolan.swe1.mcg.utils.UserUtils;

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
        Method method = request.getMethod();

        if (request.getPathArray().length > 1) {
            requestedPath = request.getPathArray()[1];
        }

        // Executes requested methods
        switch (method) {
            case POST:
                switch (requestedPath) {
                    case Paths.BATTLE_WORKER_BATTLE:
                        return battle(request);
                }
        }

        return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Unknown path");
    }

    private HttpResponse battle(HttpRequest request) {
        String authorizationToken = request.getHeaderMap().get(Headers.AUTH_HEADER);
        String username = UserUtils.getUsernameFromToken(authorizationToken);

        try {
            User dbUser = userRepository.getByUsername(username);
            if (dbUser != null) {
                if (waitingForBattle == null) {
                    return createBattleAndWaitForOpponent(dbUser);
                } else {
                    return joinBattleAndBattle(dbUser);
                }
            } else {
                return new HttpResponse(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Not logged in");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (InvalidDeckException e) {
            throw new RuntimeException(e);
        } catch (InvalidUserException e) {
            throw new RuntimeException(e);
        } catch (InvalidCardParameterException e) {
            throw new RuntimeException(e);
        } catch (BattleFinishedException e) {
            throw new RuntimeException(e);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        return new HttpResponse(HttpStatus.NOT_ACCEPTABLE, ContentType.PLAIN_TEXT, "The json string is not formatted properly");
    }

    private synchronized HttpResponse joinBattleAndBattle(User dbUser) throws SQLException, InvalidCardParameterException, InvalidUserException, InvalidDeckException, BattleFinishedException, CloneNotSupportedException {
        if (waitingForBattle != dbUser) {
            battle = prepareBattle((User) waitingForBattle.clone(), (User) dbUser.clone());
            waitingForBattle = null;

            while (battle.getGameFinished() == false) {
                battle.nextRound();
            }

            EloUtils.NewEloValues newEloValues;
            User winner = battle.getWinner();
            User loser = battle.getLoser();
            boolean draw = battle.isDraw();

            newEloValues = EloUtils.calculateNewElo(
                    winner.getElo(),
                    loser.getElo(),
                    winner.getGamesPlayed(),
                    loser.getGamesPlayed(),
                    draw);

            winner.setElo(newEloValues.winnerElo());
            loser.setElo(newEloValues.loserElo());

            if (draw == false) {
                winner.setWins(winner.getWins() + 1);
            }

            winner.setGamesPlayed(winner.getGamesPlayed() + 1);
            loser.setGamesPlayed(loser.getGamesPlayed() + 1);

            userRepository.updateUser(winner);
            userRepository.updateUser(loser);

            String battleLog = battle.getBattleLog();

            this.notify();

            return new HttpResponse(HttpStatus.OK, ContentType.PLAIN_TEXT, battleLog);
        } else {
            return new HttpResponse(HttpStatus.NOT_ACCEPTABLE, ContentType.PLAIN_TEXT, "You cannot battle yourself");
        }
    }

    private Battle prepareBattle(User player1, User player2) throws SQLException, InvalidCardParameterException, InvalidUserException, InvalidDeckException {
        int player1DeckId = deckRepository.getDeckIdForUser(player1.getId());
        int player2DeckId = deckRepository.getDeckIdForUser(player2.getId());

        player1.setDeck(cardRepository.getCardsByDeckIdAsMap(player1DeckId));
        player2.setDeck(cardRepository.getCardsByDeckIdAsMap(player2DeckId));

        Battle currentBattle = new Battle(player1, player2);
        return currentBattle;
    }

    private synchronized HttpResponse createBattleAndWaitForOpponent(User dbUser) throws InterruptedException, CloneNotSupportedException {
        waitingForBattle = (User) dbUser.clone();

        this.wait(DefaultValues.DEFAULT_TIMEOUT);

        if (battle == null) {
            waitingForBattle = null;
            return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "No user found to battle");
        } else {
            String battleLog = battle.getBattleLog();
            battle = null;
            return new HttpResponse(HttpStatus.OK, ContentType.PLAIN_TEXT, battleLog);
        }
    }
}
