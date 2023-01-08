package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.constants.RequestHeaders;
import bif3.tolan.swe1.mcg.constants.RequestPaths;
import bif3.tolan.swe1.mcg.database.respositories.interfaces.CardRepository;
import bif3.tolan.swe1.mcg.database.respositories.interfaces.DeckRepository;
import bif3.tolan.swe1.mcg.database.respositories.interfaces.UserRepository;
import bif3.tolan.swe1.mcg.exceptions.InvalidCardParameterException;
import bif3.tolan.swe1.mcg.exceptions.InvalidInputException;
import bif3.tolan.swe1.mcg.httpserver.*;
import bif3.tolan.swe1.mcg.model.Card;
import bif3.tolan.swe1.mcg.model.User;
import bif3.tolan.swe1.mcg.utils.CardUtils;
import bif3.tolan.swe1.mcg.utils.UserUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
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
        Method method = request.getMethod();

        if (request.getPathArray().length > 1) {
            requestedPath = request.getPathArray()[1];
        }

        // Executes requested methods
        switch (method) {
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

        return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Unknown path");
    }

    private HttpResponse getDeck(HttpRequest request) {
        String authorizationToken = request.getHeaderMap().get(RequestHeaders.AUTH_HEADER);
        String username = UserUtils.extractUsernameFromToken(authorizationToken);

        try {
            User requestingUser = userRepository.getUserByUsername(username);
            if (requestingUser != null) {
                int deckIdOfRequestingUser = deckRepository.getDeckIdByUserId(requestingUser.getId());
                Vector<Card> cardDeckOfRequestingUser = cardRepository.getAllCardsByDeckIdAsList(deckIdOfRequestingUser);

                String formatParameter = request.getParameterMap().get("format");

                if (formatParameter != null && formatParameter.equals("plain")) {
                    return new HttpResponse(HttpStatus.OK, ContentType.PLAIN_TEXT, CardUtils.getMultipleCardDisplayForUser(requestingUser.getUsername(), cardDeckOfRequestingUser));
                } else {
                    return new HttpResponse(HttpStatus.OK, ContentType.PLAIN_TEXT, CardUtils.getCardDetails(requestingUser.getUsername(), cardDeckOfRequestingUser));
                }
            } else {
                return new HttpResponse(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Not logged in");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InvalidCardParameterException e) {
            e.printStackTrace();
        }
        return new HttpResponse(HttpStatus.NOT_ACCEPTABLE, ContentType.PLAIN_TEXT, "The json string is not formatted properly");
    }

    private synchronized HttpResponse configureDeck(HttpRequest request) {
        String authorizationToken = request.getHeaderMap().get(RequestHeaders.AUTH_HEADER);
        String username = UserUtils.extractUsernameFromToken(authorizationToken);

        try {
            User requestingUser = userRepository.getUserByUsername(username);
            if (requestingUser != null) {
                ObjectMapper mapper = new ObjectMapper();
                String jsonString = request.getBody();

                Vector<String> requestedCardIdsForDeck = mapper.readValue(jsonString, new TypeReference<>() {
                });

                if (requestedCardIdsForDeck.size() == 4) {
                    int deckIdOfRequestingUser = deckRepository.getDeckIdByUserId(requestingUser.getId());

                    for (String cardId : requestedCardIdsForDeck) {
                        if (cardRepository.doesCardBelongToUser(cardId, requestingUser.getId()) == false) {
                            return new HttpResponse(HttpStatus.NOT_ACCEPTABLE, ContentType.PLAIN_TEXT, "At least one of the card IDs provided is not in the users card stack. Please enter valid ids");
                        }
                    }

                    Vector<Card> currentCardDeckOfRequestingUser = cardRepository.getAllCardsByDeckIdAsList(deckIdOfRequestingUser);
                    for (Card c : currentCardDeckOfRequestingUser) {
                        cardRepository.assignCardToUserStack(c.getCardId(), requestingUser.getId());
                    }

                    for (String cardId : requestedCardIdsForDeck) {
                        cardRepository.assignCardToUserDeck(cardId, deckIdOfRequestingUser);
                    }
                    return new HttpResponse(HttpStatus.OK, ContentType.PLAIN_TEXT, "Successfully updated deck");
                } else {
                    return new HttpResponse(HttpStatus.NOT_ACCEPTABLE, ContentType.PLAIN_TEXT, "You must define exactly 4 cards");
                }
            } else {
                return new HttpResponse(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Not logged in");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InvalidInputException e) {
            e.printStackTrace();
        } catch (InvalidCardParameterException e) {
            e.printStackTrace();
        }

        return new HttpResponse(HttpStatus.NOT_ACCEPTABLE, ContentType.PLAIN_TEXT, "The json string is not formatted properly");
    }
}
