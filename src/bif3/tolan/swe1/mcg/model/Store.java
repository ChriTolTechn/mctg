package bif3.tolan.swe1.mcg.model;

import bif3.tolan.swe1.mcg.constants.DefaultValues;
import bif3.tolan.swe1.mcg.exceptions.*;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class that represents the store
 *
 * @author Christopher Tolan
 */
public class Store {
    //TODO delete this later
    private ConcurrentHashMap<String, Vector<Card>> packageMap;
    private ConcurrentHashMap<String, TradeOffer> openTradeOfferMap;
    private ConcurrentHashMap<String, User> tradeOfferToUserMap;

    public Store() {
        packageMap = new ConcurrentHashMap<>();
        openTradeOfferMap = new ConcurrentHashMap<>();
        tradeOfferToUserMap = new ConcurrentHashMap<>();
    }

    /**
     * Buys a package for a user
     *
     * @param user        the user the package will be bought for
     * @param packageName the name of the package
     * @throws PackageNotFoundException   if no package with the specified name was found
     * @throws InsufficientFundsException if the user does not have enough coins to buy the package
     */
    public void buyPackage(
            User user,
            String packageName)
            throws PackageNotFoundException, InsufficientFundsException {

        Vector<Card> wantedPackage = packageMap.get(packageName);

        if (wantedPackage != null) {
            if (user.canPurchase(DefaultValues.DEFAULT_PACKAGE_COST)) {
                //user.addCardsToStack(wantedPackage);
                user.payCoins(DefaultValues.DEFAULT_PACKAGE_COST);
            } else {
                throw new InsufficientFundsException();
            }
        } else {
            throw new PackageNotFoundException();
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
            tradeOfferToUserMap.put(tradeOffer.getTradeId(), user);
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
    public void removeFromTrade(User user) {
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
    public boolean hasActiveTrade(User user) {
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
    public void trade(User user, Card tradeForId, String wantedTradeOfferId) throws NullPointerException, CardsNotInStackException, TradeOfferNotFoundException, TradeDeniedException {
        if (user == null || tradeForId == null || wantedTradeOfferId == null) throw new NullPointerException();

        /*
        if (user.hasUserCardInStack(tradeForId.getCardId())) {
            TradeOffer tradeOffer = openTradeOfferMap.get(wantedTradeOfferId);
            if (tradeOffer != null) {
                if (cardMeetsRequirement(tradeForId, tradeOffer)) {
                    user.addCardToStack(tradeOffer.getTradeCard());
                    tradeOfferToUserMap.get(wantedTradeOfferId).addCardToStack(tradeForId);
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

    // Getter
    public ConcurrentHashMap<String, Vector<Card>> getPackageMap() {
        return packageMap;
    }

    public ConcurrentHashMap<String, TradeOffer> getOpenTradeOfferMap() {
        return openTradeOfferMap;
    }

    public ConcurrentHashMap<String, User> getTradeOfferToUserMap() {
        return tradeOfferToUserMap;
    }
}
