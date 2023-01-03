package bif3.tolan.swe1.mcg.model;

import bif3.tolan.swe1.mcg.enums.CardType;

/**
 * Class that represents a trade offer
 *
 * @author Christopher Tolan
 */
public class TradeOffer {

    private final String tradeId;
    private final Card tradeCard;
    private final int minDamage;
    private final CardType cardType;
    private final CardType.CardGroup cardGroup;

    /**
     * This constructor accepts a card type as requirement
     *
     * @param tradeId   Id for the trade
     * @param tradeCard Card to trade in
     * @param minDamage Wanted minimum damage
     * @param cardType  Wanted Card Type
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    public TradeOffer(String tradeId, Card tradeCard, int minDamage, CardType cardType) throws NullPointerException, IllegalArgumentException {
        checkParameterValidity(tradeId, tradeCard, minDamage, cardType == null);
        this.cardType = cardType;
        this.cardGroup = null;
        this.tradeId = tradeId;
        this.minDamage = minDamage;
        this.tradeCard = tradeCard;
    }

    /**
     * This construct accepts a card group as requirement
     *
     * @param tradeId   Id for the trade
     * @param tradeCard Card to trade in
     * @param minDamage Wanted minimum damage
     * @param cardGroup Wanted Card Type
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    public TradeOffer(String tradeId, Card tradeCard, int minDamage, CardType.CardGroup cardGroup) throws NullPointerException, IllegalArgumentException {
        checkParameterValidity(tradeId, tradeCard, minDamage, cardGroup == null);
        this.cardGroup = cardGroup;
        this.cardType = null;
        this.tradeId = tradeId;
        this.minDamage = minDamage;
        this.tradeCard = tradeCard;
    }

    private void checkParameterValidity(String tradeId, Card tradeCard, int minDamage, boolean b) throws NullPointerException, IllegalArgumentException {
        if (tradeCard == null || tradeId == null || b)
            throw new NullPointerException();
        if (minDamage < 0)
            throw new IllegalArgumentException();
    }

    public String getTradeId() {
        return tradeId;
    }

    public Card getTradeCard() {
        return tradeCard;
    }

    public int getMinDamage() {
        return minDamage;
    }

    public CardType getCardType() {
        return cardType;
    }

    public CardType.CardGroup getCardGroup() {
        return cardGroup;
    }
}
