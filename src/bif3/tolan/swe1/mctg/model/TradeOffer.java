package bif3.tolan.swe1.mctg.model;

import bif3.tolan.swe1.mctg.model.enums.CardType;
import bif3.tolan.swe1.mctg.model.jsonViews.CardViews;
import bif3.tolan.swe1.mctg.model.jsonViews.TradeOfferViews;
import com.fasterxml.jackson.annotation.*;

/**
 * Class that represents a trade offer
 *
 * @author Christopher Tolan
 */
@JsonAutoDetect(
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonClassDescription("TradeOffer")
public class TradeOffer {
    @JsonProperty("Id")
    @JsonView({TradeOfferViews.ReadTradeOffer.class, TradeOfferViews.CreateTradeOffer.class})
    private String tradeId;
    @JsonIgnore
    private int userId;
    @JsonProperty("MinimumDamage")
    @JsonView({TradeOfferViews.ReadTradeOffer.class, TradeOfferViews.CreateTradeOffer.class})
    private int minDamage;
    @JsonProperty("RequestedCardType")
    @JsonView(TradeOfferViews.ReadTradeOffer.class)
    private CardType requestedCardType;
    @JsonProperty("RequestedCardGroup")
    @JsonView(TradeOfferViews.ReadTradeOffer.class)
    private CardType.CardGroup requestedCardGroup;
    @JsonProperty("CardToTrade")
    @JsonView(TradeOfferViews.CreateTradeOffer.class)
    private String tradeCardId;
    @JsonProperty("Card")
    @JsonView(CardViews.ReadCard.class)
    private Card card;

    /**
     * This constructor accepts a card type as requirement
     *
     * @param tradeId           Id for the trade
     * @param userId            Id from the user
     * @param minDamage         Wanted minimum damage
     * @param requestedCardType Wanted Card Type
     */
    public TradeOffer(
            String tradeId,
            int userId,
            int minDamage,
            CardType requestedCardType) {
        this.requestedCardType = requestedCardType;
        this.requestedCardGroup = null;
        this.tradeId = tradeId;
        this.userId = userId;
        this.minDamage = minDamage;
    }

    /**
     * This construct accepts a card group as requirement
     *
     * @param tradeId            Id for the trade
     * @param userId             Id from the user
     * @param minDamage          Wanted minimum damage
     * @param requestedCardGroup Wanted Card Type
     */
    public TradeOffer(
            String tradeId,
            int userId,
            int minDamage,
            CardType.CardGroup requestedCardGroup) {
        this.requestedCardGroup = requestedCardGroup;
        this.requestedCardType = null;
        this.tradeId = tradeId;
        this.userId = userId;
        this.minDamage = minDamage;
    }

    /**
     * Default constructor for Jackson
     */
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

    public CardType getRequestedCardType() {
        return requestedCardType;
    }

    @JsonSetter("Card")
    public void setRequestedCardType(String requestedCardType) throws IllegalArgumentException {
        this.requestedCardType = CardType.valueOf(requestedCardType.toUpperCase());
    }

    public CardType.CardGroup getRequestedCardGroup() {
        return requestedCardGroup;
    }

    @JsonSetter("Type")
    public void setRequestedCardGroup(String requestedCardGroup) throws IllegalArgumentException {
        this.requestedCardGroup = CardType.CardGroup.valueOf(requestedCardGroup.toUpperCase());
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
}
