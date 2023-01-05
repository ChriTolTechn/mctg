package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.exceptions.CardsNotInStackException;
import bif3.tolan.swe1.mcg.exceptions.HasActiveTradeException;
import bif3.tolan.swe1.mcg.exceptions.TradeDeniedException;
import bif3.tolan.swe1.mcg.exceptions.TradeOfferNotFoundException;
import bif3.tolan.swe1.mcg.httpserver.ContentType;
import bif3.tolan.swe1.mcg.httpserver.HttpRequest;
import bif3.tolan.swe1.mcg.httpserver.HttpResponse;
import bif3.tolan.swe1.mcg.httpserver.HttpStatus;
import bif3.tolan.swe1.mcg.model.Card;
import bif3.tolan.swe1.mcg.model.TradeOffer;
import bif3.tolan.swe1.mcg.model.User;

import java.util.concurrent.ConcurrentHashMap;

public class TradeMarketWorker {
    private ConcurrentHashMap<String, TradeOffer> openTradeOfferMap;

    public TradeMarketWorker() {
        //TODO load from database
        openTradeOfferMap = new ConcurrentHashMap<>();
    }

    public HttpResponse executeRequest(HttpRequest request) {
        String requestedMethod = "";
        if (request.getPathArray().length > 1) {
            requestedMethod = request.getPathArray()[1];
        }

        // Executes requested methods
        switch (requestedMethod) {
            default:
                return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Unknown path");
        }
    }

    /**
     * Add a card from a user for trade
     *
     * @param user       The user trading
     * @param tradeOffer Specifies what the user is looking for and what card they put up for trade
     * @throws HasActiveTradeException  If the user has already an active trade
     * @throws CardsNotInStackException If the card the user wants to trade in is not in their stack
     */
    private void addForTrade(User user, TradeOffer tradeOffer) throws HasActiveTradeException, CardsNotInStackException {
        if (openTradeOfferMap.get(user) == null) {
            user.removeCardFromStack(tradeOffer.getTradeCardId());
            openTradeOfferMap.put(tradeOffer.getTradeId(), tradeOffer);
        } else {
            throw new HasActiveTradeException();
        }
    }

    /**
     * Removes the current trade offer from one user and returns their card back to their stack
     *
     * @param user The user that the trade offer shall be taken back
     * @throws NullPointerException if the user does not have an active trade
     */
    private void removeFromTrade(User user) {
        /*
        if (hasActiveTrade(user)) {
            TradeOffer trade = openTradeOfferMap.get(user);
            if (trade != null) {
                user.addCardToStack(trade.getTradeCard());
                openTradeOfferMap.remove(user);
            }
        } else {
            throw new NullPointerException();
        }

         */
    }

    /**
     * Checks if the user has an active trade offer
     *
     * @param user The user the trade offer should be checked for
     * @return True if the user has an active trade offer
     */
    private boolean hasActiveTrade(User user) {
        return openTradeOfferMap.get(user) != null;
    }

    /**
     * Trades the buyers card with the offerers card
     *
     * @param user               The user trading for the card in the trade offer
     * @param tradeForId         The cardId the user wants to trade for
     * @param wantedTradeOfferId The id of the wanted offer
     * @throws NullPointerException
     * @throws CardsNotInStackException
     * @throws TradeOfferNotFoundException
     * @throws TradeDeniedException
     */
    private void trade(User user, Card tradeForId, String wantedTradeOfferId) throws NullPointerException, CardsNotInStackException, TradeOfferNotFoundException, TradeDeniedException {
        if (user == null || tradeForId == null || wantedTradeOfferId == null) throw new NullPointerException();

        /*
        if (user.hasUserCardInStack(tradeForId.getCardId())) {
            TradeOffer tradeOffer = openTradeOfferMap.get(wantedTradeOfferId);
            if (tradeOffer != null) {
                if (cardMeetsRequirement(tradeForId, tradeOffer)) {
                    user.addCardToStack(tradeOffer.getTradeCard());
                    //TODO load trading user from db and assign card
                    //tradeOfferToUserMap.get(wantedTradeOfferId).addCardToStack(tradeForId);
                    user.removeCardFromStack(tradeOffer.getTradeCard().getCardId());
                } else {
                    throw new TradeDeniedException();
                }
            } else {
                throw new TradeOfferNotFoundException();
            }
        } else {
            throw new CardsNotInStackException();
        }

         */
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
