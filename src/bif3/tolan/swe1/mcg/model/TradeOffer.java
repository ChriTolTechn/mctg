package bif3.tolan.swe1.mcg.model;

import bif3.tolan.swe1.mcg.enums.CardType;

/**
 * Class that represents a trade offer
 *
 * @author Christopher Tolan
 */
public class TradeOffer {

    private final String tradeId;
    private final int userId;
    private final int minDamage;
    private final CardType cardType;
    private final CardType.CardGroup cardGroup;
    private String tradeCardId;

    /**
     * This constructor accepts a card type as requirement
     *
     * @param tradeId   Id for the trade
     * @param userId    Id from the user
     * @param minDamage Wanted minimum damage
     * @param cardType  Wanted Card Type
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    public TradeOffer(
            String tradeId,
            int userId,
            int minDamage,
            CardType cardType)
            throws NullPointerException, IllegalArgumentException {

        checkParameterValidity(tradeId, userId, minDamage, cardType == null);

        this.cardType = cardType;
        this.cardGroup = null;
        this.tradeId = tradeId;
        this.userId = userId;
        this.minDamage = minDamage;
    }

    /**
     * This construct accepts a card group as requirement
     *
     * @param tradeId   Id for the trade
     * @param userId    Id from the user
     * @param minDamage Wanted minimum damage
     * @param cardGroup Wanted Card Type
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    public TradeOffer(
            String tradeId,
            int userId,
            int minDamage,
            CardType.CardGroup cardGroup)
            throws NullPointerException, IllegalArgumentException {

        checkParameterValidity(tradeId, userId, minDamage, cardGroup == null);

        this.cardGroup = cardGroup;
        this.cardType = null;
        this.tradeId = tradeId;
        this.userId = userId;
        this.minDamage = minDamage;
    }

    private void checkParameterValidity(String tradeId, int userId, int minDamage, boolean b) throws NullPointerException, IllegalArgumentException {
        if (tradeId == null || userId < 0 || b)
            throw new NullPointerException();
        if (minDamage < 0)
            throw new IllegalArgumentException();
    }

    public String getTradeId() {
        return tradeId;
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

    public int getUserId() {
        return userId;
    }

    public String getTradeCardId() {
        return tradeCardId;
    }

    public void setTradeCardId(String tradeCardId) {
        this.tradeCardId = tradeCardId;
    }
}
