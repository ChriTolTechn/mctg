package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.constants.Headers;
import bif3.tolan.swe1.mcg.constants.Paths;
import bif3.tolan.swe1.mcg.database.respositories.CardRepository;
import bif3.tolan.swe1.mcg.database.respositories.DeckRepository;
import bif3.tolan.swe1.mcg.database.respositories.UserRepository;
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
                    case Paths.DECK_WORKER_CONFIGURE_DECK:
                        return configureDeck(request);
                }
            case GET:
                switch (requestedPath) {
                    case Paths.DECK_WORKER_SHOW_DECK:
                        return getDeck(request);
                }
        }

        return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Unknown path");
    }

    private HttpResponse getDeck(HttpRequest request) {
        String authorizationToken = request.getHeaderMap().get(Headers.AUTH_HEADER);
        String username = UserUtils.extractUsernameFromToken(authorizationToken);

        try {
            User dbUser = userRepository.getByUsername(username);
            if (dbUser != null) {
                int deckId = deckRepository.getDeckIdForUser(dbUser.getId());
                Vector<Card> cards = cardRepository.getCardsByDeckId(deckId);

                String formatParameter = request.getParameterMap().get("format");

                if (formatParameter != null && formatParameter.equals("plain")) {
                    return new HttpResponse(HttpStatus.OK, ContentType.PLAIN_TEXT, CardUtils.getMultipleCardDisplayForUser(dbUser.getUsername(), cards));
                } else {
                    return new HttpResponse(HttpStatus.OK, ContentType.PLAIN_TEXT, CardUtils.getCardDetails(dbUser.getUsername(), cards));
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
        String authorizationToken = request.getHeaderMap().get(Headers.AUTH_HEADER);
        String username = UserUtils.extractUsernameFromToken(authorizationToken);

        try {
            User dbUser = userRepository.getByUsername(username);
            if (dbUser != null) {
                ObjectMapper mapper = new ObjectMapper();
                String jsonString = request.getBody();

                Vector<String> stringList = mapper.readValue(jsonString, new TypeReference<>() {
                });

                if (stringList.size() == 4) {
                    int deckId = deckRepository.getDeckIdForUser(dbUser.getId());

                    for (String cardId : stringList) {
                        if (cardRepository.doesCardBelongToUser(cardId, dbUser.getId()) == false) {
                            return new HttpResponse(HttpStatus.NOT_ACCEPTABLE, ContentType.PLAIN_TEXT, "At least one of the card IDs provided is not in the users card stack. Please enter valid ids");
                        }
                    }

                    Vector<Card> cardsInDeck = cardRepository.getCardsByDeckId(deckId);
                    for (Card c : cardsInDeck) {
                        cardRepository.assignCardToUserStack(c.getCardId(), dbUser.getId());
                    }

                    for (String cardId : stringList) {
                        cardRepository.assignCardToUserDeck(cardId, deckId);
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
