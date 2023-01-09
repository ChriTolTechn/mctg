package bif3.tolan.swe1.mcg.model;

import bif3.tolan.swe1.mcg.model.enums.CardType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Class that represents a trade offer
 *
 * @author Christopher Tolan
 */
public class TradeOffer {

    @JsonProperty("Id")
    private String tradeId;
    @JsonIgnore
    private int userId;
    @JsonProperty("MinimumDamage")
    private int minDamage;
    @JsonIgnore
    private CardType cardType;
    @JsonIgnore
    private CardType.CardGroup cardGroup;
    @JsonProperty("CardToTrade")
    private String tradeCardId;

    @JsonIgnore
    private Card card;

    /**
     * This constructor accepts a card type as requirement
     *
     * @param tradeId   Id for the trade
     * @param userId    Id from the user
     * @param minDamage Wanted minimum damage
     * @param cardType  Wanted Card Type
     */
    public TradeOffer(
            String tradeId,
            int userId,
            int minDamage,
            CardType cardType) {
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
     */
    public TradeOffer(
            String tradeId,
            int userId,
            int minDamage,
            CardType.CardGroup cardGroup) {
        this.cardGroup = cardGroup;
        this.cardType = null;
        this.tradeId = tradeId;
        this.userId = userId;
        this.minDamage = minDamage;
    }

    public TradeOffer() {
    }

    public String getTradeId() {
        return tradeId;
    }

    public int getMinDamage() {
        return minDamage;
    }

    public void setMinDamage(int minDamage) {
        this.minDamage = minDamage;
    }

    public CardType getCardType() {
        return cardType;
    }

    @JsonSetter("Card")
    public void setCardType(String cardType) throws IllegalArgumentException {
        this.cardType = CardType.valueOf(cardType.toUpperCase());
    }

    public CardType.CardGroup getCardGroup() {
        return cardGroup;
    }

    @JsonSetter("Type")
    public void setCardGroup(String cardGroup) throws IllegalArgumentException {
        this.cardGroup = CardType.CardGroup.valueOf(cardGroup.toUpperCase());
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTradeCardId() {
        return tradeCardId;
    }

    public void setTradeCardId(String tradeCardId) {
        this.tradeCardId = tradeCardId;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    @Override
    public String toString() {
        return "TradeId: " + tradeId + ", " +
                "UserId: " + userId + ", " +
                "MinDamae: " + minDamage + ", " +
                "CardType: " + (cardType == null ? "n/a" : cardType) + ", " +
                "CardGroup: " + (cardGroup == null ? "n/a" : cardGroup);
    }
}
