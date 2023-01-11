package bif3.tolan.swe1.mctg.workers;

import bif3.tolan.swe1.mctg.constants.GenericHttpResponses;
import bif3.tolan.swe1.mctg.constants.RequestHeaders;
import bif3.tolan.swe1.mctg.constants.RequestPaths;
import bif3.tolan.swe1.mctg.exceptions.*;
import bif3.tolan.swe1.mctg.httpserver.HttpRequest;
import bif3.tolan.swe1.mctg.httpserver.HttpResponse;
import bif3.tolan.swe1.mctg.httpserver.enums.HttpContentType;
import bif3.tolan.swe1.mctg.httpserver.enums.HttpMethod;
import bif3.tolan.swe1.mctg.httpserver.enums.HttpStatus;
import bif3.tolan.swe1.mctg.model.Card;
import bif3.tolan.swe1.mctg.model.TradeOffer;
import bif3.tolan.swe1.mctg.model.User;
import bif3.tolan.swe1.mctg.model.jsonViews.TradeOfferViews;
import bif3.tolan.swe1.mctg.persistence.respositories.interfaces.CardRepository;
import bif3.tolan.swe1.mctg.persistence.respositories.interfaces.TradeOfferRepository;
import bif3.tolan.swe1.mctg.persistence.respositories.interfaces.UserRepository;
import bif3.tolan.swe1.mctg.utils.TradeOfferUtils;
import bif3.tolan.swe1.mctg.utils.UserUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.Vector;

/**
 * Implementation of Workable responsible for trading
 *
 * @author Christopher Tolan
 */
public class TradeWorker implements Workable {
    private UserRepository userRepository;
    private TradeOfferRepository tradeOfferRepository;
    private CardRepository cardRepository;

    public TradeWorker(UserRepository userRepository, TradeOfferRepository tradeOfferRepository, CardRepository cardRepository) {
        this.userRepository = userRepository;
        this.tradeOfferRepository = tradeOfferRepository;
        this.cardRepository = cardRepository;
    }

    public HttpResponse executeRequest(HttpRequest request) {
        String requestedPath = "";
        HttpMethod httpMethod = request.getMethod();

        if (request.getPathArray().length > 1) {
            requestedPath = request.getPathArray()[1];
        }

        switch (httpMethod) {
            case GET:
                switch (requestedPath) {
                    case RequestPaths.TRADE_WORKER_GET_TRADES:
                        return getAllTradeDeals(request);
                }
            case POST:
                switch (requestedPath) {
                    case RequestPaths.TRADE_WORKER_ADD_TRADE:
                        return createTrade(request);
                    default:
                        return acceptTrade(request, requestedPath);
                }
            case DELETE:
                return deleteTrade(request, requestedPath);
        }

        return GenericHttpResponses.INVALID_PATH;
    }

    private HttpResponse getAllTradeDeals(HttpRequest request) {
        // get username from token
        String authorizationToken = request.getHeaderMap().get(RequestHeaders.AUTH_HEADER);
        String username = UserUtils.extractUsernameFromToken(authorizationToken);

        try {
            userRepository.getUserByUsername(username);

            // get all trade offers
            Vector<TradeOffer> allTradeOffers = tradeOfferRepository.getAllTradeOffersAsList();

            // load card details for trade offers
            for (TradeOffer tradeOffer : allTradeOffers) {
                tradeOffer.setCard(cardRepository.getCardByTradeOfferId(tradeOffer.getTradeId()));
            }

            // Convert trade offers to json
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper
                    .writerWithView(TradeOfferViews.ReadTradeOffer.class)
                    .writeValueAsString(allTradeOffers);

            // return all trade offers as json
            return new HttpResponse(HttpStatus.OK, HttpContentType.JSON, jsonString);
        } catch (SQLException | UnsupportedCardTypeException | UnsupportedElementTypeException |
                 JsonProcessingException e) {
            e.printStackTrace();
            return GenericHttpResponses.INTERNAL_ERROR;
        } catch (UserDoesNotExistException e) {
            return GenericHttpResponses.INVALID_TOKEN;
        } catch (NoActiveTradeOffersException e) {
            return GenericHttpResponses.NO_ACTIVE_TRADES;
        }
    }

    private synchronized HttpResponse acceptTrade(HttpRequest request, String requestedTradeId) {
        // get username from token
        String authorizationToken = request.getHeaderMap().get(RequestHeaders.AUTH_HEADER);
        String username = UserUtils.extractUsernameFromToken(authorizationToken);

        try {
            User requestingUser = userRepository.getUserByUsername(username);

            // retrieve wanted trade offer
            TradeOffer wantedTrade = tradeOfferRepository.getTradeOfferById(requestedTradeId);

            // check to not trade with yourself
            if (wantedTrade.getUserId() != requestingUser.getId()) {
                // convert the card id of the requesting user to string
                ObjectMapper mapper = new ObjectMapper();
                String jsonString = request.getBody();
                String offerCardIdOfRequestingUser = mapper
                        .readValue(jsonString, String.class);

                // retrieving the card from db
                Card offerCardOfRequestingUser = cardRepository.getCardById(offerCardIdOfRequestingUser);

                // check if the card belongs to the user
                cardRepository.checkCardBelongsToUser(offerCardOfRequestingUser.getCardId(), requestingUser.getId());

                // check if requirements for trade are met
                if (TradeOfferUtils.cardMeetsRequirement(offerCardOfRequestingUser, wantedTrade)) {
                    // load card in trade offer
                    wantedTrade.setCard(cardRepository.getCardByTradeOfferId(wantedTrade.getTradeId()));

                    // assign card from trade offer to user accepting
                    cardRepository.assignCardToUserStack(wantedTrade.getCard().getCardId(), requestingUser.getId());
                    // assign card offered to the user initiating the trade
                    cardRepository.assignCardToUserStack(offerCardIdOfRequestingUser, wantedTrade.getUserId());

                    // delete empty trade
                    tradeOfferRepository.deleteTrade(wantedTrade.getTradeId());

                    return GenericHttpResponses.SUCCESS_TRADE;
                } else {
                    return GenericHttpResponses.TRADE_CRITERIA_NOT_MET;
                }
            } else {
                return GenericHttpResponses.IDENTICAL_USER;
            }
        } catch (SQLException | UnsupportedElementTypeException | UnsupportedCardTypeException e) {
            e.printStackTrace();
            return GenericHttpResponses.INTERNAL_ERROR;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return GenericHttpResponses.INVALID_INPUT;
        } catch (InvalidInputException e) {
            return GenericHttpResponses.INVALID_INPUT;
        } catch (TradeOfferNotFoundException e) {
            return GenericHttpResponses.TRADE_NOT_FOUND;
        } catch (UserDoesNotExistException e) {
            return GenericHttpResponses.INVALID_TOKEN;
        } catch (ItemDoesNotBelongToUserException e) {
            return GenericHttpResponses.ITEM_NOT_OWNED;
        }
    }

    private synchronized HttpResponse createTrade(HttpRequest request) {
        // get username from token
        String authorizationToken = request.getHeaderMap().get(RequestHeaders.AUTH_HEADER);
        String username = UserUtils.extractUsernameFromToken(authorizationToken);

        try {
            User dbUser = userRepository.getUserByUsername(username);

            // create trade offer from jsonString
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = request.getBody();
            TradeOffer tradeOffer = mapper
                    .readerWithView(TradeOfferViews.CreateTradeOffer.class)
                    .forType(TradeOffer.class)
                    .readValue(jsonString);

            try {
                // check if trade with ID existss
                tradeOfferRepository.getTradeOfferById(tradeOffer.getTradeId());

                return GenericHttpResponses.ID_EXISTS;
            } catch (TradeOfferNotFoundException e) {
                // check if card belongs to user
                cardRepository.checkCardBelongsToUser(tradeOffer.getTradeCardId(), dbUser.getId());

                // create trade and remove card from user deck
                tradeOffer.setUserId(dbUser.getId());
                tradeOfferRepository.createTradeOffer(tradeOffer);
                cardRepository.assignCardToTradeOffer(tradeOffer.getTradeCardId(), tradeOffer.getTradeId());

                return GenericHttpResponses.SUCCESS_CREATE;
            }
        } catch (SQLException | UnsupportedCardTypeException |
                 UnsupportedElementTypeException e) {
            e.printStackTrace();
            return GenericHttpResponses.INTERNAL_ERROR;
        } catch (InvalidInputException e) {
            return GenericHttpResponses.INVALID_INPUT;
        } catch (HasActiveTradeException e) {
            return GenericHttpResponses.HAS_ACTIVE_TRADE;
        } catch (ItemDoesNotBelongToUserException e) {
            return GenericHttpResponses.ITEM_NOT_OWNED;
        } catch (UserDoesNotExistException e) {
            return GenericHttpResponses.INVALID_TOKEN;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return GenericHttpResponses.INVALID_INPUT;
        }
    }

    private synchronized HttpResponse deleteTrade(HttpRequest request, String requestedTradeId) {
        // get username from token
        String authorizationToken = request.getHeaderMap().get(RequestHeaders.AUTH_HEADER);
        String username = UserUtils.extractUsernameFromToken(authorizationToken);

        try {
            User dbUser = userRepository.getUserByUsername(username);

            // check if trade offer exists
            TradeOffer tradeOffer = tradeOfferRepository.getTradeOfferById(requestedTradeId);
            // check if trade belongs to user
            if (tradeOffer.getUserId() == dbUser.getId()) {
                // delete trade and put card back to user deck
                tradeOffer.setCard(cardRepository.getCardByTradeOfferId(tradeOffer.getTradeId()));
                cardRepository.assignCardToUserStack(tradeOffer.getCard().getCardId(), dbUser.getId());
                tradeOfferRepository.deleteTrade(tradeOffer.getTradeId());

                return GenericHttpResponses.SUCCESS_DELETE;
            } else {
                return GenericHttpResponses.ITEM_NOT_OWNED;
            }
        } catch (SQLException | UnsupportedCardTypeException | UnsupportedElementTypeException e) {
            e.printStackTrace();
            return GenericHttpResponses.INTERNAL_ERROR;
        } catch (InvalidInputException e) {
            return GenericHttpResponses.INVALID_INPUT;
        } catch (TradeOfferNotFoundException e) {
            return GenericHttpResponses.TRADE_NOT_FOUND;
        } catch (UserDoesNotExistException e) {
            return GenericHttpResponses.INVALID_TOKEN;
        }
    }
}
