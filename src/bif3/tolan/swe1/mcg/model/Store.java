package bif3.tolan.swe1.mcg.model;

import bif3.tolan.swe1.mcg.constants.DefaultValues;
import bif3.tolan.swe1.mcg.enums.CardType;
import bif3.tolan.swe1.mcg.exceptions.HasActiveTradeException;
import bif3.tolan.swe1.mcg.exceptions.InsufficientFundsException;
import bif3.tolan.swe1.mcg.exceptions.InvalidUserException;
import bif3.tolan.swe1.mcg.exceptions.PackageNotFoundException;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Set;

public class Store {
    private Dictionary<String, Set<Card>> packages;

    private Dictionary<User, TradeRequirement> openTrades;

    public Store() {
        packages = new Hashtable<String, Set<Card>>();
        openTrades = new Hashtable<User, TradeRequirement>();
    }

    public void buyPackage(User user, String packageName) throws PackageNotFoundException, InsufficientFundsException {
        var wantedPackage = packages.get(packageName);

        if (wantedPackage != null) {
            user.payCoins(DefaultValues.DEFAULT_PACKAGE_COST);
            user.addCardsToStack(wantedPackage);
            return;
        }
        throw new PackageNotFoundException();
    }

    private void addForTrade(User user, TradeRequirement tradeRequirement) throws HasActiveTradeException {
        if (openTrades.get(user) == null) {
            user.removeCardFromStack(tradeRequirement.tradeCard);
            openTrades.put(user, tradeRequirement);
        } else {
            throw new HasActiveTradeException();
        }
    }

    public void removeFromTrade(User user) throws InvalidUserException {
        if (hasActiveTrade(user)) {
            var trade = openTrades.get(user);
            if (trade != null) {
                user.addCardToStack(trade.tradeCard);
                openTrades.remove(user);
            }
        }
    }

    public boolean hasActiveTrade(User user) {
        return openTrades.get(user) != null;
    }

    public void trade(User user, Card tradeFor, User trader, TradeRequirement wanted) {
        if (user == null || tradeFor == null || trader == null || wanted == null) throw new NullPointerException();

        if (user.hasUserCardInStack(tradeFor)) {
            if (openTrades.get(trader) == wanted) {
                if (cardMeetsRequirement(tradeFor, wanted)) {
                    user.addCardToStack(wanted.tradeCard);
                    trader.addCardToStack(tradeFor);
                    user.removeCardFromStack(tradeFor);
                }
            }
        }
    }

    private boolean cardMeetsRequirement(Card card, TradeRequirement tradeRequirement) {
        return card.getDamage() >= tradeRequirement.minDamage &&
                (
                        (tradeRequirement.cardGroup == null && tradeRequirement.cardType == card.getMonsterType())
                                ^
                                (tradeRequirement.cardType == null && card.getMonsterType().isInGroup(tradeRequirement.cardGroup))
                );
    }

    public void addForTrade(User user, Card tradeInCard, int minDamage, CardType cardType) throws InvalidUserException, HasActiveTradeException {
        if (user == null) throw new InvalidUserException();
        if (user.hasUserCardInStack(tradeInCard))
            addForTrade(user, new TradeRequirement(tradeInCard, minDamage, cardType));
    }

    public void addForTrade(User user, Card tradeInCard, int minDamage, CardType.CardGroup cardGroup) throws InvalidUserException, HasActiveTradeException {
        if (user == null) throw new InvalidUserException();
        if (user.hasUserCardInStack(tradeInCard))
            addForTrade(user, new TradeRequirement(tradeInCard, minDamage, cardGroup));
    }

    public Dictionary<String, Set<Card>> getPackages() {
        return packages;
    }

    public Dictionary<User, TradeRequirement> getOpenTrades() {
        return openTrades;
    }

    private class TradeRequirement {
        private final Card tradeCard;
        private final int minDamage;
        private final CardType cardType;
        private final CardType.CardGroup cardGroup;

        public TradeRequirement(Card tradeCard, int minDamage, CardType cardType) {
            if (tradeCard == null)
                throw new NullPointerException("tradeCard");

            this.minDamage = minDamage < 0 ? 0 : minDamage;
            this.tradeCard = tradeCard;
            this.cardType = cardType;
            this.cardGroup = null;
        }

        public TradeRequirement(Card tradeCard, int minDamage, CardType.CardGroup cardGroup) {
            if (tradeCard == null)
                throw new NullPointerException("tradeCard");

            this.minDamage = minDamage < 0 ? 0 : minDamage;
            this.tradeCard = tradeCard;
            this.cardGroup = cardGroup;
            this.cardType = null;
        }
    }
}
