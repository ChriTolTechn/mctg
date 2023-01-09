package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.constants.DefaultValues;
import bif3.tolan.swe1.mcg.constants.GenericHttpResponses;
import bif3.tolan.swe1.mcg.constants.RequestHeaders;
import bif3.tolan.swe1.mcg.constants.RequestPaths;
import bif3.tolan.swe1.mcg.exceptions.*;
import bif3.tolan.swe1.mcg.httpserver.HttpRequest;
import bif3.tolan.swe1.mcg.httpserver.HttpResponse;
import bif3.tolan.swe1.mcg.httpserver.enums.HttpContentType;
import bif3.tolan.swe1.mcg.httpserver.enums.HttpMethod;
import bif3.tolan.swe1.mcg.httpserver.enums.HttpStatus;
import bif3.tolan.swe1.mcg.model.Card;
import bif3.tolan.swe1.mcg.model.User;
import bif3.tolan.swe1.mcg.persistence.respositories.interfaces.CardRepository;
import bif3.tolan.swe1.mcg.persistence.respositories.interfaces.DeckRepository;
import bif3.tolan.swe1.mcg.persistence.respositories.interfaces.UserRepository;
import bif3.tolan.swe1.mcg.utils.CardUtils;
import bif3.tolan.swe1.mcg.utils.UserUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.Vector;

public class DeckWorker implements Workable {

    private UserRepository userRepository;

    private DeckRepository deckRepository;

    private CardRepository cardRepository;

    public DeckWorker(UserRepository userRepository, DeckRepository deckRepository, CardRepository cardRepository) {
        this.userRepository = userRepository;
        this.deckRepository = deckRepository;
        this.cardRepository = cardRepository;
    }

    @Override
    public HttpResponse executeRequest(HttpRequest request) {
        String requestedPath = "";
        HttpMethod httpMethod = request.getMethod();

        if (request.getPathArray().length > 1) {
            requestedPath = request.getPathArray()[1];
        }

        switch (httpMethod) {
            case PUT:
                switch (requestedPath) {
                    case RequestPaths.DECK_WORKER_CONFIGURE_DECK:
                        return configureDeck(request);
                }
            case GET:
                switch (requestedPath) {
                    case RequestPaths.DECK_WORKER_SHOW_DECK:
                        return getDeck(request);
                }
        }

        return GenericHttpResponses.INVALID_PATH;
    }

    private HttpResponse getDeck(HttpRequest request) {
        // get username from token
        String authorizationToken = request.getHeaderMap().get(RequestHeaders.AUTH_HEADER);
        String username = UserUtils.extractUsernameFromToken(authorizationToken);

        try {
            User requestingUser = userRepository.getUserByUsername(username);

            // get deck of user
            int deckIdOfRequestingUser = deckRepository.getDeckIdByUserId(requestingUser.getId());
            Vector<Card> cardDeckOfRequestingUser = cardRepository.getAllCardsByDeckIdAsList(deckIdOfRequestingUser);

            // check what format the user wants the cards to be displayed with
            String formatParameter = request.getParameterMap().get("format");

            // return cards with requested format
            if (formatParameter != null && formatParameter.equals("plain")) {
                return new HttpResponse(HttpStatus.OK, HttpContentType.PLAIN_TEXT, CardUtils.getMultipleCardDisplayForUser(requestingUser.getUsername(), cardDeckOfRequestingUser));
            } else {
                return new HttpResponse(HttpStatus.OK, HttpContentType.PLAIN_TEXT, CardUtils.getCardDetails(requestingUser.getUsername(), cardDeckOfRequestingUser));
            }
        } catch (SQLException | UnsupportedCardTypeException | UnsupportedElementTypeException e) {
            e.printStackTrace();
            return GenericHttpResponses.INTERNAL_ERROR;
        } catch (UserDoesNotExistException e) {
            return GenericHttpResponses.INVALID_TOKEN;
        }
    }

    private synchronized HttpResponse configureDeck(HttpRequest request) {
        // get username from token
        String authorizationToken = request.getHeaderMap().get(RequestHeaders.AUTH_HEADER);
        String username = UserUtils.extractUsernameFromToken(authorizationToken);

        try {
            User requestingUser = userRepository.getUserByUsername(username);

            // init object mapper and read json strings of requested cardIds
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = request.getBody();
            Vector<String> requestedCardIdsForDeck = mapper.readValue(jsonString, new TypeReference<>() {
            });

            // check if there are 4 ids
            if (requestedCardIdsForDeck.size() == DefaultValues.DECK_SIZE) {
                // check if all cards belong to user
                for (String cardId : requestedCardIdsForDeck) {
                    cardRepository.checkCardBelongsToUser(cardId, requestingUser.getId());
                }

                // assign old cards in deck to user stack
                int deckIdOfRequestingUser = deckRepository.getDeckIdByUserId(requestingUser.getId());
                Vector<Card> currentCardDeckOfRequestingUser = cardRepository.getAllCardsByDeckIdAsList(deckIdOfRequestingUser);
                for (Card c : currentCardDeckOfRequestingUser) {
                    cardRepository.assignCardToUserStack(c.getCardId(), requestingUser.getId());
                }

                // assign new cards to user deck
                for (String cardId : requestedCardIdsForDeck) {
                    cardRepository.assignCardToUserDeck(cardId, deckIdOfRequestingUser);
                }
                return GenericHttpResponses.SUCCESS_UPDATE;
            } else {
                return GenericHttpResponses.INVALID_DECK;
            }

        } catch (SQLException | InvalidInputException | UnsupportedCardTypeException |
                 UnsupportedElementTypeException e) {
            e.printStackTrace();
            return GenericHttpResponses.INTERNAL_ERROR;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return GenericHttpResponses.INVALID_INPUT;
        } catch (UserDoesNotExistException e) {
            return GenericHttpResponses.INVALID_TOKEN;
        } catch (ItemDoesNotBelongToUserException e) {
            return GenericHttpResponses.ITEM_NOT_OWNED;
        }
    }
}
