package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.constants.Headers;
import bif3.tolan.swe1.mcg.constants.Paths;
import bif3.tolan.swe1.mcg.database.respositories.CardRepository;
import bif3.tolan.swe1.mcg.database.respositories.TradeOfferRepository;
import bif3.tolan.swe1.mcg.database.respositories.UserRepository;
import bif3.tolan.swe1.mcg.exceptions.HasActiveTradeException;
import bif3.tolan.swe1.mcg.exceptions.InvalidCardParameterException;
import bif3.tolan.swe1.mcg.exceptions.InvalidInputException;
import bif3.tolan.swe1.mcg.exceptions.TradeOfferNotFoundException;
import bif3.tolan.swe1.mcg.httpserver.*;
import bif3.tolan.swe1.mcg.model.Card;
import bif3.tolan.swe1.mcg.model.TradeOffer;
import bif3.tolan.swe1.mcg.model.User;
import bif3.tolan.swe1.mcg.utils.TradeUtils;
import bif3.tolan.swe1.mcg.utils.UserUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.Vector;

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
        Method method = request.getMethod();

        if (request.getPathArray().length > 1) {
            requestedPath = request.getPathArray()[1];
        }

        // Executes requested methods
        switch (method) {
            case GET:
                switch (requestedPath) {
                    case Paths.TRADE_WORKER_GET_TRADES:
                        return getAllTradeDeals(request);
                }
            case POST:
                switch (requestedPath) {
                    case Paths.TRADE_WORKER_ADD_TRADE:
                        return createTrade(request);
                    default:
                        return acceptTrade(request, requestedPath);
                }
            case DELETE:
                return deleteTrade(request, requestedPath);
        }

        return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Unknown path");
    }

    private HttpResponse getAllTradeDeals(HttpRequest request) {
        String authorizationToken = request.getHeaderMap().get(Headers.AUTH_HEADER);
        String username = UserUtils.getUsernameFromToken(authorizationToken);

        try {
            User dbUser = userRepository.getByUsername(username);
            if (dbUser != null) {
                Vector<TradeOffer> tradeOffers = tradeOfferRepository.getAllTradeOffers();
                if (tradeOffers.isEmpty() == false) {
                    for (TradeOffer trade : tradeOffers) {
                        trade.setCard(cardRepository.getCardByTradeOfferId(trade.getTradeId()));
                    }
                    return new HttpResponse(HttpStatus.OK, ContentType.PLAIN_TEXT, TradeUtils.printAllTradeOffers(tradeOffers));
                } else {
                    return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "No active trade offers");
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

    private synchronized HttpResponse acceptTrade(HttpRequest request, String requestedTradeId) {
        String authorizationToken = request.getHeaderMap().get(Headers.AUTH_HEADER);
        String username = UserUtils.getUsernameFromToken(authorizationToken);

        try {
            User dbUser = userRepository.getByUsername(username);
            if (dbUser != null) {
                TradeOffer wantedTrade = tradeOfferRepository.getTradeOfferById(requestedTradeId);
                if (wantedTrade != null) {
                    if (wantedTrade.getUserId() != dbUser.getId()) {
                        String tradeInCardId = extractStringFromJson(request.getBody());
                        Card cardToTradeIn = cardRepository.getCardById(tradeInCardId);

                        if (cardRepository.doesCardBelongToUser(cardToTradeIn.getCardId(), dbUser.getId())) {
                            if (cardMeetsRequirement(cardToTradeIn, wantedTrade)) {
                                wantedTrade.setCard(cardRepository.getCardByTradeOfferId(wantedTrade.getTradeId()));
                                cardRepository.assignCardToUserStack(wantedTrade.getCard().getCardId(), dbUser.getId());
                                cardRepository.assignCardToUserStack(tradeInCardId, wantedTrade.getUserId());

                                tradeOfferRepository.deleteTrade(wantedTrade.getTradeId());
                                return new HttpResponse(HttpStatus.OK, ContentType.PLAIN_TEXT, "Successfully traded cards");
                            } else {
                                return new HttpResponse(HttpStatus.NOT_ACCEPTABLE, ContentType.PLAIN_TEXT, "The card you want to trade in does not meet the trade criteria");
                            }
                        } else {
                            return new HttpResponse(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "The card you are trying to trade in does not belong to you");
                        }
                    } else {
                        return new HttpResponse(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "You cannot trade with yourself");
                    }
                } else {
                    return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Trade not found");
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
        } catch (InvalidCardParameterException e) {
            e.printStackTrace();
        } catch (InvalidInputException e) {
            e.printStackTrace();
        } catch (TradeOfferNotFoundException e) {
            e.printStackTrace();
        }

        return new HttpResponse(HttpStatus.NOT_ACCEPTABLE, ContentType.PLAIN_TEXT, "The json string is not formatted properly");
    }

    private synchronized HttpResponse createTrade(HttpRequest request) {
        String authorizationToken = request.getHeaderMap().get(Headers.AUTH_HEADER);
        String username = UserUtils.getUsernameFromToken(authorizationToken);

        try {
            User dbUser = userRepository.getByUsername(username);
            if (dbUser != null) {
                if (tradeOfferRepository.getTradeOfferByUserId(dbUser.getId()) == null) {
                    ObjectMapper mapper = new ObjectMapper();
                    String jsonString = request.getBody();

                    TradeOffer tradeOffer = mapper.readValue(jsonString, TradeOffer.class);

                    if (tradeOfferRepository.getTradeOfferById(tradeOffer.getTradeId()) == null) {
                        if (cardRepository.doesCardBelongToUser(tradeOffer.getTradeCardId(), dbUser.getId())) {
                            tradeOffer.setUserId(dbUser.getId());

                            tradeOfferRepository.createTradeOffer(tradeOffer);
                            cardRepository.assignCardToTradeOffer(tradeOffer.getTradeCardId(), tradeOffer.getTradeId());
                            return new HttpResponse(HttpStatus.OK, ContentType.PLAIN_TEXT, "Successfully created trade offer");
                        } else {
                            return new HttpResponse(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "The card you try to create a trade with does not belong to you");
                        }
                    } else {
                        return new HttpResponse(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "A trade with this ID already exists");
                    }
                } else {
                    return new HttpResponse(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "You can only have one active trade at the same time");
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
        } catch (HasActiveTradeException e) {
            e.printStackTrace();
        } catch (InvalidCardParameterException e) {
            e.printStackTrace();
        }

        return new HttpResponse(HttpStatus.NOT_ACCEPTABLE, ContentType.PLAIN_TEXT, "The json string is not formatted properly");
    }

    private synchronized HttpResponse deleteTrade(HttpRequest request, String requestedTradeId) {
        String authorizationToken = request.getHeaderMap().get(Headers.AUTH_HEADER);
        String username = UserUtils.getUsernameFromToken(authorizationToken);

        try {
            User dbUser = userRepository.getByUsername(username);
            if (dbUser != null) {
                TradeOffer tradeOffer = tradeOfferRepository.getTradeOfferById(requestedTradeId);
                if (tradeOffer != null) {
                    if (tradeOffer.getUserId() == dbUser.getId()) {
                        tradeOffer.setCard(cardRepository.getCardByTradeOfferId(tradeOffer.getTradeId()));
                        cardRepository.assignCardToUserStack(tradeOffer.getCard().getCardId(), dbUser.getId());
                        tradeOfferRepository.deleteTrade(tradeOffer.getTradeId());
                        return new HttpResponse(HttpStatus.OK, ContentType.PLAIN_TEXT, "Trade offer deleted");
                    } else {
                        return new HttpResponse(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "You cannot delete a trade that does not belong to you");
                    }
                } else {
                    return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "The trade does not exist");
                }
            } else {
                return new HttpResponse(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Not logged in");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InvalidInputException e) {
            e.printStackTrace();
        } catch (InvalidCardParameterException e) {
            e.printStackTrace();
        } catch (TradeOfferNotFoundException e) {
            e.printStackTrace();
        }

        return new HttpResponse(HttpStatus.NOT_ACCEPTABLE, ContentType.PLAIN_TEXT, "The json string is not formatted properly");
    }

    private String extractStringFromJson(String jsonString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String tradeInCardId = mapper.readValue(jsonString, String.class);
        return tradeInCardId;
    }

    /**
     * Checks if the requirement for the trade is met.
     * Since a trade offer can be specified by either monster type or card grouop, an XOR is used to check
     *
     * @param card       Card offering from the buyer
     * @param tradeOffer Requirements from the seller
     * @return True if the requirements are met
     */
    private boolean cardMeetsRequirement(Card card, TradeOffer tradeOffer) {
        return card.getDamage() >= tradeOffer.getMinDamage() &&
                (
                        (tradeOffer.getCardGroup() == null && tradeOffer.getCardType() == card.getMonsterType())
                                ^
                                (tradeOffer.getCardType() == null && card.getMonsterType().isInGroup(tradeOffer.getCardGroup()))
                );
    }
}
